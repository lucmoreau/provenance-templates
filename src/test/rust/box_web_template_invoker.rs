//! Rust equivalent of WebTemplateInvoker / RemoteEnactor for the *transport* (box) domain.
//!
//! This is the transport-catalogue counterpart of `fs_web_template_invoker` (which covers
//! the fs/file-system catalogue).  The structure is identical; only the bean types,
//! `BeanCompleter2`, `CompositeBeanCompleter2`, `BeanHistory` and `InputOutputProcessor`
//! are swapped for their transport equivalents under
//! `org::openprovenance::templates::catalogue::transport`.
//!
//! `BoxRemoteEnactor` = `transport::BeanHistory<BoxWebTemplateInvoker>`.

use std::any::Any;
use std::collections::HashMap;

use serde::Serialize;
use serde_json::Value;
use crate::org::openprovenance::book::physical::client::integrator::{
    agent_init_inputs::AgentInitInputs,
    agent_init_outputs::AgentInitOutputs,
    item_init_inputs::ItemInitInputs,
    item_init_outputs::ItemInitOutputs,
    packing_composite_inputs::PackingCompositeInputs,
    packing_composite_outputs::PackingCompositeOutputs,
    packing_inputs::PackingInputs,
    packing_outputs::PackingOutputs,
    transporting_inputs::TransportingInputs,
    transporting_outputs::TransportingOutputs,
    unpacking_composite_inputs::UnpackingCompositeInputs,
    unpacking_composite_outputs::UnpackingCompositeOutputs,
    unpacking_inputs::UnpackingInputs,
    unpacking_outputs::UnpackingOutputs,
    weighing_inputs::WeighingInputs,
    weighing_outputs::WeighingOutputs,
};
use crate::org::openprovenance::book::responsibility::client::integrator::{
    handingover_inputs::HandingoverInputs,
    handingover_outputs::HandingoverOutputs,
};
use crate::org::openprovenance::templates::catalogue::transport::integrator::{
    bean_completer2::BeanCompleter2,
    bean_history::BeanHistory,
    composite_bean_completer2::CompositeBeanCompleter2,
    input_output_processor::InputOutputProcessor,
};

// ---------------------------------------------------------------------------
// BoxServiceInvoker
// ---------------------------------------------------------------------------

pub struct BoxServiceInvoker;

impl BoxServiceInvoker {
    pub fn new() -> Self {
        BoxServiceInvoker
    }

    /// POST `body` (serialised as a one-element JSON array) to `url` with a
    /// Bearer token.  Blocks until the full response body arrives.
    ///
    /// Returns the full parsed JSON response value (expected to be an array
    /// whose first element is the output map).
    pub fn post_instructions_in_out<T: Serialize>(
        &self,
        url: &str,
        body: &T,
        access_token: &str,
    ) -> Result<serde_json::Value, Box<dyn std::error::Error>> {
        let json_body = serde_json::to_string(&[body])?;

        let response = ureq::post(url)
            .set("Authorization", &format!("Bearer {}", access_token))
            .set("Content-Type", "application/vnd.kcl.prov-template+json")
            .set("Accept", "application/vnd.kcl.prov-template+json")
            .send_string(&json_body)?;

        let value: serde_json::Value = response.into_json()?;
        Ok(value)
    }
}

// ---------------------------------------------------------------------------
// BoxWebTemplateInvoker
// ---------------------------------------------------------------------------

pub struct BoxWebTemplateInvoker {
    pub url: String,
    pub access_token: String,
    /// When `true`, the JSON request body and response are printed to stderr
    /// before and after every POST.
    pub debug: bool,
    si: BoxServiceInvoker,
}

impl BoxWebTemplateInvoker {
    pub fn new(url: impl Into<String>, access_token: impl Into<String>) -> Self {
        Self {
            url: url.into(),
            access_token: access_token.into(),
            debug: false,
            si: BoxServiceInvoker::new(),
        }
    }

    pub fn with_debug(mut self, debug: bool) -> Self {
        self.debug = debug;
        self
    }

    /// Core HTTP dispatch — mirrors `WebTemplateInvoker::generic_post_and_return`.
    ///
    /// 1. Serialise `inputs` as `[inputs]` and POST to `self.url`.
    /// 2. Parse `response[0]` into `HashMap<String, Box<dyn Any>>`:
    ///    - JSON strings  → `Box<String>`
    ///    - JSON integers → `Box<i32>`
    ///    - JSON booleans → `Box<bool>`
    ///    - JSON null / nested objects are skipped (not consumed by BeanCompleter2)
    /// 3. Call `completer(map, outbean)` and return the result.
    ///
    /// On any I/O or parse error the method logs to stderr and returns `outbean`
    /// unchanged, avoiding panics in network-facing code.
    pub fn generic_post_and_return<IN, OUT>(
        &self,
        outbean: OUT,
        inputs: &IN,
        completer: impl Fn(HashMap<String, Box<dyn Any>>, OUT) -> OUT,
    ) -> OUT
    where
        IN: Serialize,
    {
        // ---- Serialise request (and optionally debug-print) ----
        let json_body = match serde_json::to_string_pretty(&[inputs]) {
            Ok(s)  => s,
            Err(e) => {
                eprintln!("BoxWebTemplateInvoker: serialisation error: {}", e);
                return outbean;
            }
        };
        if self.debug {
            eprintln!(">> POST {}\n{}", self.url, json_body);
        }

        // ---- Blocking HTTP POST ----
        let result0 = match self.si.post_instructions_in_out(&self.url, inputs, &self.access_token) {
            Ok(v)  => v,
            Err(e) => {
                eprintln!("BoxWebTemplateInvoker: HTTP error: {}", e);
                return outbean;
            }
        };
        if self.debug {
            eprintln!("<< Response\n{}", serde_json::to_string_pretty(&result0).unwrap_or_else(|_| format!("{:?}", result0)));
        }

        // JS: let result1 = result0[0]
        let result1 = match result0.get(0) {
            Some(v) => v.clone(),
            None => {
                eprintln!("BoxWebTemplateInvoker: response array is empty");
                return outbean;
            }
        };

        // JS: for (let key in result1) { if (result1.hasOwnProperty(key)) map.put(key, result1[key]) }
        let map = Self::convert_json_value_to_map(result1);

        completer(map, outbean)
    }

    fn convert_json_value_to_map(json_value: Value) -> HashMap<String, Box<dyn Any>> {
        let mut map: HashMap<String, Box<dyn Any>> = HashMap::new();
        if let Some(obj) = json_value.as_object() {
            for (k, v) in obj {
                match v {
                    serde_json::Value::String(s) => {
                        map.insert(k.clone(), Box::new(s.clone()));
                    }
                    serde_json::Value::Number(n) => {
                        if let Some(i) = n.as_i64() {
                            map.insert(k.clone(), Box::new(i as i32));
                        }
                    }
                    serde_json::Value::Bool(b) => {
                        map.insert(k.clone(), Box::new(*b));
                    }
                    serde_json::Value::Array(arr) => {
                        // convert each value in the vector recursively,
                        let arr1=arr.iter().map(|v| Self::convert_json_value_to_map(v.clone())).collect::<Vec<_>>();
                        map.insert(k.clone(), Box::new(arr1));
                    }
                    _ => {
                        panic!("BoxWebTemplateInvoker: JSON Value is not an object");
                    } // null and nested objects not consumed by BeanCompleter2
                }
            }
        }
        map
    }
}

// ---------------------------------------------------------------------------
// impl InputOutputProcessor (transport) for BoxWebTemplateInvoker
// ---------------------------------------------------------------------------

impl InputOutputProcessor for BoxWebTemplateInvoker {
    fn process_transporting_inputs(&mut self, bean: &TransportingInputs) -> TransportingOutputs {
        self.generic_post_and_return(
            TransportingOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_transporting_outputs(o),
        )
    }

    fn process_handingover_inputs(&mut self, bean: &HandingoverInputs) -> HandingoverOutputs {
        self.generic_post_and_return(
            HandingoverOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_handingover_outputs(o),
        )
    }

    fn process_weighing_inputs(&mut self, bean: &WeighingInputs) -> WeighingOutputs {
        self.generic_post_and_return(
            WeighingOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_weighing_outputs(o),
        )
    }

    fn process_agent_init_inputs(&mut self, bean: &AgentInitInputs) -> AgentInitOutputs {
        self.generic_post_and_return(
            AgentInitOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_agent_init_outputs(o),
        )
    }

    fn process_item_init_inputs(&mut self, bean: &ItemInitInputs) -> ItemInitOutputs {
        self.generic_post_and_return(
            ItemInitOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_item_init_outputs(o),
        )
    }

    fn process_packing_inputs(&mut self, bean: &PackingInputs) -> PackingOutputs {
        self.generic_post_and_return(
            PackingOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_packing_outputs(o),
        )
    }

    fn process_packing_composite_inputs(
        &mut self,
        bean: &PackingCompositeInputs,
    ) -> PackingCompositeOutputs {
        self.generic_post_and_return(
            PackingCompositeOutputs::new(),
            bean,
            |m, mut o| {
                o.elements = Vec::new();
                CompositeBeanCompleter2::new(m).process_packing_composite_outputs(o)
            },
        )
    }

    fn process_unpacking_inputs(&mut self, bean: &UnpackingInputs) -> UnpackingOutputs {
        self.generic_post_and_return(
            UnpackingOutputs::new(),
            bean,
            |m, o| BeanCompleter2::new(m).process_unpacking_outputs(o),
        )
    }

    fn process_unpacking_composite_inputs(
        &mut self,
        bean: &UnpackingCompositeInputs,
    ) -> UnpackingCompositeOutputs {
        self.generic_post_and_return(
            UnpackingCompositeOutputs::new(),
            bean,
            |m, mut o| {
                o.elements = Vec::new();
                CompositeBeanCompleter2::new(m).process_unpacking_composite_outputs(o)
            },
        )
    }
}

// ---------------------------------------------------------------------------
// BoxRemoteEnactor  (= transport::BeanHistory<BoxWebTemplateInvoker>)
// ---------------------------------------------------------------------------

/// `BoxRemoteEnactor` wraps a `BoxWebTemplateInvoker` in a `BeanHistory` so that every
/// `process_xxx_inputs` call is both dispatched to the remote service and recorded
/// in the history vec — exactly as the local transport enactor does for the local path.
pub type BoxRemoteEnactor = BeanHistory<BoxWebTemplateInvoker>;

/// Constructor helper — pass `debug = true` to enable JSON request/response logging
/// on stderr.
pub fn new_box_remote_enactor(
    url: impl Into<String>,
    access_token: impl Into<String>,
    debug: bool,
) -> BoxRemoteEnactor {
    BeanHistory::new(
        BoxWebTemplateInvoker::new(url, access_token).with_debug(debug),
        Vec::new(),
    )
}
