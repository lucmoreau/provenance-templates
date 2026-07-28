from org.openprovenance.templates.catalogue.fs.integrator.TemplateInvoker import TemplateInvoker
from org.openprovenance.book.physical.client.integrator.PackingInputs_1 import PackingInputs_1
from org.openprovenance.book.physical.client.integrator.PackingInputs import PackingInputs
from org.openprovenance.book.physical.client.integrator.Packing_compositeInputs import Packing_compositeInputs
from org.openprovenance.book.physical.client.integrator.UnpackingInputs_1 import UnpackingInputs_1
from org.openprovenance.book.physical.client.integrator.UnpackingInputs import UnpackingInputs
from org.openprovenance.book.physical.client.integrator.Unpacking_compositeInputs import Unpacking_compositeInputs
from org.openprovenance.book.physical.client.integrator.TransportingInputs import TransportingInputs
from org.openprovenance.book.physical.client.integrator.WeighingInputs import WeighingInputs
from org.openprovenance.book.physical.client.integrator.Agent_initInputs import Agent_initInputs
from org.openprovenance.book.physical.client.integrator.Item_initInputs import Item_initInputs
from org.openprovenance.book.responsibility.client.integrator.HandingoverInputs import HandingoverInputs
from past.util.Map import Map

import json
from urllib import request


def _to_jsonable(value):
    """Convert generated bean-like objects into plain JSON-compatible structures."""
    if value is None or isinstance(value, (str, int, float, bool)):
        return value
    if isinstance(value, (list, tuple)):
        return [_to_jsonable(v) for v in value]
    if isinstance(value, dict):
        return {k: _to_jsonable(v) for k, v in value.items()}
    if hasattr(value, "toJSON"):
        return value.toJSON()
    if hasattr(value, "__dict__"):
        return {
            k: _to_jsonable(v)
            for k, v in value.__dict__.items()
            if not k.startswith("_")
        }
    return str(value)


def postInstructionsInOut(url, inputs0, access_token):
    """
    POST serialized `inputs0` to `url` with optional bearer authorization.
    Returns a list so callers can safely consume index 0.
    """
    payload = _to_jsonable(inputs0)

    payload = ("[" + payload + "]").encode("utf-8")  # Wrap in list and encode for POST

    headers = {
        "Content-Type": "application/vnd.kcl.prov-template+json",
        "Accept": "application/vnd.kcl.prov-template+json",
        "Accept-PROV-Hash": "SHA3-512",
    }
    if access_token:
        token = access_token.strip()
        headers["Authorization"] = token if token.lower().startswith("bearer ") else f"Bearer {token}"

    req = request.Request(url=url, data=payload, headers=headers, method="POST")
    with request.urlopen(req) as resp:
        body = resp.read().decode("utf-8")

    if not body:
        return []

    parsed = json.loads(body)
    return parsed if isinstance(parsed, list) else [parsed]


class WebTemplateInvoker(TemplateInvoker):
    """
    A remote enactor that extends BeanLocalEnactor2 to call the web service for template instantion.
    """

    def __init__(self, url=None, token=None):
        super().__init__()
        self.negative = False
        self.recorded_values = Map()
        self.history = []
        self.accessToken=token
        self.url=url
        self.id2object = {}

    def generic_post_and_return(self, outbean, body, completer):
        result0=postInstructionsInOut(self.url, body, self.accessToken)
        result1 = result0[0]
        aMap=Map()
        for key in result1:
            aMap.put(key,result1[key])
        val=completer(aMap, outbean)
        return val


    def sign(self):
        return -1 if self.negative else 1

    def newIdentifier(self, field, counter):
        """
        Generate a new integer identifier for the given field and counter.
        """
        if counter not in self.counter_map:
            self.counter_initial_value = self.counter_initial_value + self.sign() * 10000
            self.counter_map[counter] = self.counter_initial_value

        if self.negative:
            new_value = self.counter_map[counter]
            self.counter_map[counter] -= 1
        else:
            new_value = self.counter_map[counter]
            self.counter_map[counter] += 1

        if field not in self.recorded_values:
            self.recorded_values[field] = []
        self.recorded_values[field].append(new_value)

        return new_value

    def newSIdentifier(self, field, counter):
        """
        Generate a new string identifier for the given field and counter.
        """
        if counter not in self.counter_map:
            self.counter_initial_value = self.counter_initial_value + self.sign() * 10000
            self.counter_map[counter] = self.counter_initial_value

        if self.negative:
            new_value = self.counter_map[counter]
            self.counter_map[counter] -= 1
        else:
            new_value = self.counter_map[counter]
            self.counter_map[counter] += 1

        if field not in self.recorded_values:
            self.recorded_values[field] = []
        self.recorded_values[field].append(new_value)

        return str(new_value)

    def get_counter_map(self):
        return self.counter_map

    def get_recorded_values(self):
        return self.recorded_values

    def get_history(self):
        return self.history

    def get_id2object(self):
        return self.id2object

    def get_id2array(self):
        return self.id2array

    def get_csv(self):
        return self.csv

    def get_counter_initial_value(self):
        return self.counter_initial_value

    def get_csv_inputs(self):
        return self.csv_inputs

    def is_negative(self):
        return self.negative

    def process_file_filtering_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_filtering_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_transforming_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_transforming_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_approving_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_approving_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_splitting_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_splitting_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_validating_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_validating_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_training_inputs(self, bean):
        # Call the parent's process method to get the output
        out = super(WebTemplateInvoker,self).process_file_training_inputs(bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass


    def get_history(self):
        return self.history