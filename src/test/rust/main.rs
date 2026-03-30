//! Rust equivalent of src/test/js/fs_run_workflow.js
//!
//! Usage:
//!   transport_template_library local    – run the PleadWorkflow using the local enactor
//!   transport_template_library remote   – run the PleadWorkflow against the remote web service
//!
//! # Local mode
//!
//!   JS: templateInstantion = new LocalEnactor(false);
//!
//!   Constructs `BeanHistory<BeanLocalEnactor3>` and runs the workflow through it.
//!   After the workflow, prints history (as JSON), the ID of the last output bean,
//!   the counter map, and the recorded values — matching every console.log in the JS.
//!
//! # Remote mode
//!
//!   JS:
//!     var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
//!     templateInstantion = new RemoteEnactor(url, accessToken);
//!
//!   Reads the Keycloak token from `~/.keycloak_token`, constructs
//!   `BeanHistory<WebTemplateInvoker>` (= `RemoteEnactor`), and runs the same
//!   workflow.  After the workflow prints history and the last output ID.
//!   Counter map / recorded values are not available in remote mode.

mod org;
mod fs_web_template_invoker;
mod box_web_template_invoker;

use std::any::Any;
use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::Rc;
use std::sync::atomic::Ordering;

use serde::{Deserialize, Serialize};

use org::openprovenance::book::fs::client::integrator::{
    file_approving_inputs::FileApprovingInputs,
    file_approving_outputs::FileApprovingOutputs,
    file_filtering_inputs::FileFilteringInputs,
    file_filtering_outputs::FileFilteringOutputs,
    file_init_inputs::FileInitInputs,
    file_init_outputs::FileInitOutputs,
    file_splitting_inputs::FileSplittingInputs,
    file_splitting_outputs::FileSplittingOutputs,
    file_training_inputs::FileTrainingInputs,
    file_training_outputs::FileTrainingOutputs,
    file_transforming_composite_inputs::FileTransformingCompositeInputs,
    file_transforming_composite_outputs::FileTransformingCompositeOutputs,
    file_transforming_inputs::FileTransformingInputs,
    file_transforming_outputs::FileTransformingOutputs,
    file_validating_inputs::FileValidatingInputs,
    file_validating_outputs::FileValidatingOutputs,
};
use crate::org::openprovenance::book::fs::client::common::{
    file_approving_bean::FileApprovingBean,
    file_filtering_bean::FileFilteringBean,
    file_init_bean::FileInitBean,
    file_splitting_bean::FileSplittingBean,
    file_training_bean::FileTrainingBean,
    file_transforming_bean::FileTransformingBean,
    file_transforming_composite_bean::FileTransformingCompositeBean,
    file_validating_bean::FileValidatingBean,
};
use org::openprovenance::book::workflows::plead_workflow::PleadWorkflow;
use org::openprovenance::templates::catalogue::fs::integrator::{
    bean_history::BeanHistory,
    bean_local_enactor3::BeanLocalEnactor3,
    input_output_processor::InputOutputProcessor,
};
use fs_web_template_invoker::{FsWebTemplateInvoker, new_remote_enactor};

// Box-workflow imports
use org::openprovenance::book::physical::client::{
    common::{
        agent_init_bean::AgentInitBean,
        item_init_bean::ItemInitBean,
        packing_bean::PackingBean,
        packing_composite_bean::PackingCompositeBean,
        transporting_bean::TransportingBean,
        unpacking_bean::UnpackingBean,
        unpacking_composite_bean::UnpackingCompositeBean,
        weighing_bean::WeighingBean,
    },
    integrator::unpacking_composite_outputs::UnpackingCompositeOutputs,
};
use org::openprovenance::book::responsibility::client::common::handingover_bean::HandingoverBean;
use org::openprovenance::book::workflows::box_workflow::BoxWorkflow;
use org::openprovenance::templates::catalogue::transport::integrator::{
    bean_history::BeanHistory as TransportBeanHistory,
    bean_local_enactor3::BeanLocalEnactor3 as TransportBeanLocalEnactor3,
    input_output_processor::InputOutputProcessor as TransportInputOutputProcessor,
};
use box_web_template_invoker::{BoxWebTemplateInvoker, new_box_remote_enactor};

// ---------------------------------------------------------------------------
// HistoryEntry — typed envelope for JSON serialisation of the history vec.
// ---------------------------------------------------------------------------

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type")]
pub enum HistoryEntry {
    FileInit(FileInitBean),
    FileTransforming(FileTransformingBean),
    FileFiltering(FileFilteringBean),
    FileTraining(FileTrainingBean),
    FileValidating(FileValidatingBean),
    FileApproving(FileApprovingBean),
    FileSplitting(FileSplittingBean),
    FileTransformingComposite(FileTransformingCompositeBean),
}

fn to_history_entries(vec: &[Box<dyn Any>]) -> Vec<HistoryEntry> {
    vec.iter()
       .filter_map(|entry| try_convert(entry.as_ref()))
       .collect()
}

fn try_convert(entry: &dyn Any) -> Option<HistoryEntry> {
    if let Some(b) = entry.downcast_ref::<FileInitBean>()                  { return Some(HistoryEntry::FileInit(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileTransformingBean>()          { return Some(HistoryEntry::FileTransforming(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileFilteringBean>()             { return Some(HistoryEntry::FileFiltering(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileTrainingBean>()              { return Some(HistoryEntry::FileTraining(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileValidatingBean>()            { return Some(HistoryEntry::FileValidating(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileApprovingBean>()             { return Some(HistoryEntry::FileApproving(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileSplittingBean>()             { return Some(HistoryEntry::FileSplitting(b.clone())); }
    if let Some(b) = entry.downcast_ref::<FileTransformingCompositeBean>() { return Some(HistoryEntry::FileTransformingComposite(b.clone())); }
    None
}

// ---------------------------------------------------------------------------
// HistoryProcessor<T>
//
// Generic Rc<RefCell<BeanHistory<T>>> wrapper that implements InputOutputProcessor
// by delegating to the shared inner history.  Keeping a second Rc handle in `main`
// lets us inspect history / counters after the workflow has consumed the first handle.
// ---------------------------------------------------------------------------

struct HistoryProcessor<T: InputOutputProcessor>(Rc<RefCell<BeanHistory<T>>>);

impl<T: InputOutputProcessor> InputOutputProcessor for HistoryProcessor<T> {
    fn process_file_init_inputs(&mut self, bean: &FileInitInputs) -> FileInitOutputs {
        self.0.borrow_mut().process_file_init_inputs(bean)
    }
    fn process_file_transforming_inputs(&mut self, bean: &FileTransformingInputs) -> FileTransformingOutputs {
        self.0.borrow_mut().process_file_transforming_inputs(bean)
    }
    fn process_file_filtering_inputs(&mut self, bean: &FileFilteringInputs) -> FileFilteringOutputs {
        self.0.borrow_mut().process_file_filtering_inputs(bean)
    }
    fn process_file_training_inputs(&mut self, bean: &FileTrainingInputs) -> FileTrainingOutputs {
        self.0.borrow_mut().process_file_training_inputs(bean)
    }
    fn process_file_validating_inputs(&mut self, bean: &FileValidatingInputs) -> FileValidatingOutputs {
        self.0.borrow_mut().process_file_validating_inputs(bean)
    }
    fn process_file_approving_inputs(&mut self, bean: &FileApprovingInputs) -> FileApprovingOutputs {
        self.0.borrow_mut().process_file_approving_inputs(bean)
    }
    fn process_file_splitting_inputs(&mut self, bean: &FileSplittingInputs) -> FileSplittingOutputs {
        self.0.borrow_mut().process_file_splitting_inputs(bean)
    }
    fn process_file_transforming_composite_inputs(
        &mut self,
        bean: &FileTransformingCompositeInputs,
    ) -> FileTransformingCompositeOutputs {
        self.0.borrow_mut().process_file_transforming_composite_inputs(bean)
    }
}

// ---------------------------------------------------------------------------
// ThisWorkflow
//
// Mirrors the JS:
//   class ThisWorkflow extends PleadWorkflow {
//       constructor(templateInstantion, inputs, outputs) { super(...) }
//       time() { return new Date().toISOString() }
//   }
//
// Holds a Box<dyn InputOutputProcessor> so it works for both local and remote modes.
// ---------------------------------------------------------------------------

struct ThisWorkflow {
    template_instantiation: Option<Box<dyn InputOutputProcessor>>,
    inputs:  Option<Vec<Box<dyn Any>>>,
    outputs: Option<Vec<Box<dyn Any>>>,
}

impl ThisWorkflow {
    fn new(processor: impl InputOutputProcessor + 'static) -> Self {
        Self {
            template_instantiation: Some(Box::new(processor)),
            inputs:  Some(Vec::new()),
            outputs: Some(Vec::new()),
        }
    }
}

impl PleadWorkflow for ThisWorkflow {
    /// Current UTC time as an ISO-8601 string — mirrors JS `new Date().toISOString()`.
    fn time(&self) -> String {
        use std::time::{SystemTime, UNIX_EPOCH};
        let secs = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map(|d| d.as_secs())
            .unwrap_or(0);
        unix_secs_to_iso8601(secs)
    }

    fn inputs(&mut self) -> &mut Option<Vec<Box<dyn Any>>> {
        &mut self.inputs
    }

    fn outputs(&mut self) -> &mut Option<Vec<Box<dyn Any>>> {
        &mut self.outputs
    }

    fn template_instantiation(&mut self) -> &mut Option<Box<dyn InputOutputProcessor>> {
        &mut self.template_instantiation
    }
}

// ---------------------------------------------------------------------------
// Minimal ISO-8601 UTC formatter (no chrono dependency).
// ---------------------------------------------------------------------------

fn unix_secs_to_iso8601(secs: u64) -> String {
    let sec  = secs % 60;
    let min  = (secs / 60) % 60;
    let hour = (secs / 3600) % 24;
    let days = secs / 86400;

    let mut year = 1970u32;
    let mut rem  = days;
    loop {
        let diy = days_in_year(year) as u64;
        if rem < diy { break; }
        rem  -= diy;
        year += 1;
    }

    let month_days: [u64; 12] = [
        31, if is_leap(year) { 29 } else { 28 },
        31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
    ];
    let mut month = 1u32;
    let mut day   = rem + 1;
    for &md in &month_days {
        if day <= md { break; }
        day   -= md;
        month += 1;
    }

    format!("{:04}-{:02}-{:02}T{:02}:{:02}:{:02}Z", year, month, day, hour, min, sec)
}

fn days_in_year(y: u32) -> u32 { if is_leap(y) { 366 } else { 365 } }
fn is_leap(y: u32) -> bool { (y % 4 == 0 && y % 100 != 0) || y % 400 == 0 }

// ---------------------------------------------------------------------------
// Shared printing helpers
// ---------------------------------------------------------------------------

/// Print history as JSON — used by both local and remote modes.
fn print_history(history: &[Box<dyn Any>]) {
    println!("=== History ({} entries) ===", history.len());
    let entries = to_history_entries(history);
    match serde_json::to_string_pretty(&entries) {
        Ok(json) => println!("{}", json),
        Err(e)   => eprintln!("  serialization error: {}", e),
    }
}

/// Print the ID of the last workflow output — used by both modes.
fn print_last_output_id(outputs: &[Box<dyn Any>]) {
    println!("\n=== ID of last element in history ===");
    match outputs.last() {
        None       => println!("  (no outputs)"),
        Some(last) => match last.downcast_ref::<FileApprovingOutputs>() {
            Some(o) => println!("  {:?}", o.i_d),
            None    => println!("  (unexpected type for last output)"),
        },
    }
}

// ---------------------------------------------------------------------------
// main
// ---------------------------------------------------------------------------

fn main() {
    // -----------------------------------------------------------------------
    // Parse command-line argument: local | remote
    //
    // JS:
    //   const mode = (process.argv[2] || 'notdefined').toLowerCase();
    // -----------------------------------------------------------------------
    let args: Vec<String> = std::env::args().collect();
    let wkflw  = args.get(1).map(String::as_str).unwrap_or("workflow not defined");
    let mode  = args.get(2).map(String::as_str).unwrap_or("mode not defined");
    let debug = args.iter().any(|a| a == "--debug");



    match wkflw {
        "box" => { run_box_workflow(mode,debug) }
        "fs"  => { run_fs_workflow(mode, debug) }
        other => {
            eprintln!("Unknown workflow: {:?}", other);
            eprintln!("Usage: transport_template_library [box|fs] [local|remote] [--debug]");

        }
    }

}

fn run_fs_workflow(mode: &str, debug: bool) {
    // Fixed workflow parameters — same values as in the JS script.
    let workflow_args = (
        Some(111i32),                        // engineer
        Some(333i32),                        // manager
        "inputfile",                         // filename_root
        Some(123i32),                        // old_file_id
        Some(56i32),                         // tmethod
        Some(78i32),                         // fmethod
        Some(456i32),                        // n_rows
        Some(768i32),                        // n_cols
        "/home/bob",                         // path
        "2026-03-01T09:03:51.168987Z",       // start
        "2026-03-01T09:03:51.168987Z",       // end
    );

    match mode {
        // -------------------------------------------------------------------
        // LOCAL mode
        //
        // JS:
        //   templateInstantion = new LocalEnactor(false);
        //   …
        //   console.log(templateInstantion.getCounterMap());
        //   console.log(templateInstantion.getRecordedValues());
        // -------------------------------------------------------------------
        "local" => {
            // Build BeanHistory<BeanLocalEnactor3> and share via Rc<RefCell<>>.
            let shared: Rc<RefCell<BeanHistory<BeanLocalEnactor3>>> = Rc::new(RefCell::new(
                BeanHistory::<BeanLocalEnactor3>::new(
                    BeanLocalEnactor3::new(HashMap::new(), HashMap::new(), false),
                    Vec::new(),
                ),
            ));

            let mut workflow = ThisWorkflow::new(HistoryProcessor(Rc::clone(&shared)));

            workflow.workflow(
                workflow_args.0,  workflow_args.1,  workflow_args.2,
                workflow_args.3,  workflow_args.4,  workflow_args.5,
                workflow_args.6,  workflow_args.7,  workflow_args.8,
                workflow_args.9,  workflow_args.10,
            );

            // JS: console.log(templateInstantion.getHistory())
            print_history(shared.borrow().get_history());

            // JS: console.log("ID of last element in history " + outputs[outputs.length-1].ID)
            print_last_output_id(workflow.outputs.as_deref().unwrap_or_default());

            // JS: console.log(templateInstantion.getCounterMap())
            println!("\n=== Counter map ===");
            {
                let enactor = shared.borrow();
                let counter_map = enactor.get_delegator().get_counter_map();
                if counter_map.is_empty() {
                    println!("  (empty)");
                } else {
                    let mut keys: Vec<&String> = counter_map.keys().collect();
                    keys.sort();
                    for k in keys {
                        println!("  {}: {}", k, counter_map[k].load(Ordering::Relaxed));
                    }
                }
            }

            // JS: console.log(templateInstantion.getRecordedValues())
            println!("\n=== Recorded values ===");
            {
                let enactor = shared.borrow();
                let recorded = enactor.get_delegator().get_recorded_values();
                if recorded.is_empty() {
                    println!("  (empty)");
                } else {
                    let mut keys: Vec<&String> = recorded.keys().collect();
                    keys.sort();
                    for k in keys {
                        println!("  {}: {:?}", k, recorded[k]);
                    }
                }
            }
        }

        // -------------------------------------------------------------------
        // REMOTE mode
        //
        // JS:
        //   var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
        //   templateInstantion = new RemoteEnactor(url, accessToken);
        // -------------------------------------------------------------------
        "remote" => {
            let token = std::fs::read_to_string("/Users/luc/.keycloak_token")
                .expect("Cannot read /Users/luc/.keycloak_token — is the Keycloak token file present?")
                .trim()
                .to_string();

            let url = "http://localhost:7075/book/provapi/statements";

            // RemoteEnactor = BeanHistory<WebTemplateInvoker>
            let shared: Rc<RefCell<BeanHistory<FsWebTemplateInvoker>>> = Rc::new(RefCell::new(
                new_remote_enactor(url, token, debug),
            ));

            let mut workflow = ThisWorkflow::new(HistoryProcessor(Rc::clone(&shared)));

            workflow.workflow(
                workflow_args.0,  workflow_args.1,  workflow_args.2,
                workflow_args.3,  workflow_args.4,  workflow_args.5,
                workflow_args.6,  workflow_args.7,  workflow_args.8,
                workflow_args.9,  workflow_args.10,
            );

            // JS: console.log(templateInstantion.getHistory())
            print_history(shared.borrow().get_history());

            // JS: console.log("ID of last element in history " + outputs[outputs.length-1].ID)
            print_last_output_id(workflow.outputs.as_deref().unwrap_or_default());

            // Counter map / recorded values are on BeanLocalEnactor3, not available remotely.
        }

        // -------------------------------------------------------------------
        // Unknown mode
        // -------------------------------------------------------------------
        other => {
            eprintln!("Unknown mode: {:?}", other);
            eprintln!("Usage: transport_template_library [local|remote] [--debug]");
            std::process::exit(1);
        }
    }
}

// ---------------------------------------------------------------------------
// Box workflow — HistoryEntry, HistoryProcessor, helpers
// ---------------------------------------------------------------------------

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type")]
pub enum BoxHistoryEntry {
    AgentInit(AgentInitBean),
    ItemInit(ItemInitBean),
    Packing(PackingBean),
    PackingComposite(PackingCompositeBean),
    Transporting(TransportingBean),
    Handingover(HandingoverBean),
    Weighing(WeighingBean),
    Unpacking(UnpackingBean),
    UnpackingComposite(UnpackingCompositeBean),
}

fn to_box_history_entries(vec: &[Box<dyn Any>]) -> Vec<BoxHistoryEntry> {
    vec.iter()
       .filter_map(|e| try_convert_box(e.as_ref()))
       .collect()
}

fn try_convert_box(entry: &dyn Any) -> Option<BoxHistoryEntry> {
    if let Some(b) = entry.downcast_ref::<AgentInitBean>()          { return Some(BoxHistoryEntry::AgentInit(b.clone())); }
    if let Some(b) = entry.downcast_ref::<ItemInitBean>()           { return Some(BoxHistoryEntry::ItemInit(b.clone())); }
    if let Some(b) = entry.downcast_ref::<PackingBean>()            { return Some(BoxHistoryEntry::Packing(b.clone())); }
    if let Some(b) = entry.downcast_ref::<PackingCompositeBean>()   { return Some(BoxHistoryEntry::PackingComposite(b.clone())); }
    if let Some(b) = entry.downcast_ref::<TransportingBean>()       { return Some(BoxHistoryEntry::Transporting(b.clone())); }
    if let Some(b) = entry.downcast_ref::<HandingoverBean>()        { return Some(BoxHistoryEntry::Handingover(b.clone())); }
    if let Some(b) = entry.downcast_ref::<WeighingBean>()           { return Some(BoxHistoryEntry::Weighing(b.clone())); }
    if let Some(b) = entry.downcast_ref::<UnpackingBean>()          { return Some(BoxHistoryEntry::Unpacking(b.clone())); }
    if let Some(b) = entry.downcast_ref::<UnpackingCompositeBean>() { return Some(BoxHistoryEntry::UnpackingComposite(b.clone())); }
    None
}

fn print_box_history(history: &[Box<dyn Any>]) {
    println!("=== History ({} entries) ===", history.len());
    let entries = to_box_history_entries(history);
    match serde_json::to_string_pretty(&entries) {
        Ok(json) => println!("{}", json),
        Err(e)   => eprintln!("  serialization error: {}", e),
    }
}

/// JS: `console.log("ID of last element in history " + outputs[outputs.length-1].ID)`
fn print_box_last_output_id(outputs: &[Box<dyn Any>]) {
    println!("\n=== ID of last element in history ===");
    match outputs.last() {
        None       => println!("  (no outputs)"),
        Some(last) => match last.downcast_ref::<UnpackingCompositeOutputs>() {
            Some(o) => println!("  {:?}", o.i_d),
            None    => println!("  (unexpected type for last output)"),
        },
    }
}

// ---------------------------------------------------------------------------
// BoxHistoryProcessor — Rc<RefCell<TransportBeanHistory<T>>> adapter
// ---------------------------------------------------------------------------

struct BoxHistoryProcessor<T: TransportInputOutputProcessor>(Rc<RefCell<TransportBeanHistory<T>>>);

impl<T: TransportInputOutputProcessor> TransportInputOutputProcessor for BoxHistoryProcessor<T> {
    fn process_transporting_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::transporting_inputs::TransportingInputs) -> org::openprovenance::book::physical::client::integrator::transporting_outputs::TransportingOutputs {
        self.0.borrow_mut().process_transporting_inputs(bean)
    }
    fn process_handingover_inputs(&mut self, bean: &org::openprovenance::book::responsibility::client::integrator::handingover_inputs::HandingoverInputs) -> org::openprovenance::book::responsibility::client::integrator::handingover_outputs::HandingoverOutputs {
        self.0.borrow_mut().process_handingover_inputs(bean)
    }
    fn process_weighing_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::weighing_inputs::WeighingInputs) -> org::openprovenance::book::physical::client::integrator::weighing_outputs::WeighingOutputs {
        self.0.borrow_mut().process_weighing_inputs(bean)
    }
    fn process_agent_init_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::agent_init_inputs::AgentInitInputs) -> org::openprovenance::book::physical::client::integrator::agent_init_outputs::AgentInitOutputs {
        self.0.borrow_mut().process_agent_init_inputs(bean)
    }
    fn process_item_init_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::item_init_inputs::ItemInitInputs) -> org::openprovenance::book::physical::client::integrator::item_init_outputs::ItemInitOutputs {
        self.0.borrow_mut().process_item_init_inputs(bean)
    }
    fn process_packing_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::packing_inputs::PackingInputs) -> org::openprovenance::book::physical::client::integrator::packing_outputs::PackingOutputs {
        self.0.borrow_mut().process_packing_inputs(bean)
    }
    fn process_packing_composite_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::packing_composite_inputs::PackingCompositeInputs) -> org::openprovenance::book::physical::client::integrator::packing_composite_outputs::PackingCompositeOutputs {
        self.0.borrow_mut().process_packing_composite_inputs(bean)
    }
    fn process_unpacking_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::unpacking_inputs::UnpackingInputs) -> org::openprovenance::book::physical::client::integrator::unpacking_outputs::UnpackingOutputs {
        self.0.borrow_mut().process_unpacking_inputs(bean)
    }
    fn process_unpacking_composite_inputs(&mut self, bean: &org::openprovenance::book::physical::client::integrator::unpacking_composite_inputs::UnpackingCompositeInputs) -> org::openprovenance::book::physical::client::integrator::unpacking_composite_outputs::UnpackingCompositeOutputs {
        self.0.borrow_mut().process_unpacking_composite_inputs(bean)
    }
}

// ---------------------------------------------------------------------------
// run_box_workflow
//
// Mirrors JS (lines 87-147 of box_run_workflow.js):
//
//   if (mode === 'local')  templateInstantion2 = new LocalEnactor(false)
//   else                   templateInstantion2 = new WebTemplateInvoker(url, accessToken)
//
//   const boxWorkflow = new ThisWorkflow(templateInstantion2, inputs0, outputs0)
//   boxWorkflow.run()
//
//   console.log(templateInstantion2.getHistory())
//   console.log("ID of last element in history " + outputs[outputs.length-1].ID)
//   console.log(templateInstantion2.getCounterMap())    // local only
//   console.log(templateInstantion2.getRecordedValues()) // local only
// ---------------------------------------------------------------------------

fn run_box_workflow(mode: &str, debug: bool) {
    match mode {
        // -------------------------------------------------------------------
        // LOCAL mode
        // JS: templateInstantion2 = new LocalEnactor(false);
        // -------------------------------------------------------------------
        "local" => {
            let shared: Rc<RefCell<TransportBeanHistory<TransportBeanLocalEnactor3>>> =
                Rc::new(RefCell::new(
                    TransportBeanHistory::<TransportBeanLocalEnactor3>::new(
                        TransportBeanLocalEnactor3::new(HashMap::new(), HashMap::new(), false),
                        Vec::new(),
                    ),
                ));

            let mut workflow = BoxWorkflow::new(
                BoxHistoryProcessor(Rc::clone(&shared)),
                Vec::new(),
                Vec::new(),
            );

            // JS: boxWorkflow.run()
            workflow.run();

            // JS: console.log(templateInstantion2.getHistory())
            print_box_history(shared.borrow().get_history());

            // JS: console.log("ID of last element in history " + outputs[outputs.length-1].ID)
           // print_box_last_output_id(workflow.outputs.as_deref().unwrap_or_default());

            // JS: console.log(templateInstantion2.getCounterMap())
            println!("\n=== Counter map ===");
            {
                let enactor = shared.borrow();
                let counter_map = enactor.get_delegator().get_counter_map();
                if counter_map.is_empty() {
                    println!("  (empty)");
                } else {
                    let mut keys: Vec<&String> = counter_map.keys().collect();
                    keys.sort();
                    for k in keys {
                        println!("  {}: {}", k, counter_map[k].load(Ordering::Relaxed));
                    }
                }
            }

            // JS: console.log(templateInstantion2.getRecordedValues())
            println!("\n=== Recorded values ===");
            {
                let enactor = shared.borrow();
                let recorded = enactor.get_delegator().get_recorded_values();
                if recorded.is_empty() {
                    println!("  (empty)");
                } else {
                    let mut keys: Vec<&String> = recorded.keys().collect();
                    keys.sort();
                    for k in keys {
                        println!("  {}: {:?}", k, recorded[k]);
                    }
                }
            }
        }

        // -------------------------------------------------------------------
        // REMOTE mode
        // JS:
        //   var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
        //   templateInstantion2 = new WebTemplateInvoker(url, accessToken);
        // -------------------------------------------------------------------
        "remote" => {
            let token = std::fs::read_to_string("/Users/luc/.keycloak_token")
                .expect("Cannot read /Users/luc/.keycloak_token — is the Keycloak token file present?")
                .trim()
                .to_string();

            let url = "http://localhost:7075/book/provapi/statements";

            let shared: Rc<RefCell<TransportBeanHistory<BoxWebTemplateInvoker>>> =
                Rc::new(RefCell::new(new_box_remote_enactor(url, token, debug)));

            let mut workflow = BoxWorkflow::new(
                BoxHistoryProcessor(Rc::clone(&shared)),
                Vec::new(),
                Vec::new(),
            );

            // JS: boxWorkflow.run()
            workflow.run();

            // JS: console.log(templateInstantion2.getHistory())
            print_box_history(shared.borrow().get_history());

            // JS: console.log("ID of last element in history " + outputs[outputs.length-1].ID)
            print_box_last_output_id(workflow.outputs.as_deref().unwrap_or_default());

            // Counter map / recorded values only available for local BeanLocalEnactor3.
        }

        // -------------------------------------------------------------------
        // Unknown mode
        // -------------------------------------------------------------------
        other => {
            eprintln!("Unknown mode: {:?}", other);
            eprintln!("Usage: transport_template_library box [local|remote] [--debug]");
            std::process::exit(1);
        }
    }
}

