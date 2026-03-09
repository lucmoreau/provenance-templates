from org.openprovenance.book.physical.client.integrator.Agent_initInputs import Agent_initInputs
from org.openprovenance.book.physical.client.integrator.Agent_initOutputs import Agent_initOutputs
from org.openprovenance.book.physical.client.integrator.Item_initInputs import Item_initInputs
from org.openprovenance.book.physical.client.integrator.Item_initOutputs import Item_initOutputs
from org.openprovenance.book.physical.client.integrator.PackingInputs_1 import PackingInputs_1
from org.openprovenance.book.physical.client.integrator.Packing_compositeInputs import Packing_compositeInputs
from org.openprovenance.book.physical.client.integrator.Packing_compositeOutputs import Packing_compositeOutputs
from org.openprovenance.book.physical.client.integrator.WeighingInputs import WeighingInputs
from org.openprovenance.book.physical.client.integrator.WeighingOutputs import WeighingOutputs
from org.openprovenance.book.physical.client.integrator.TransportingInputs import TransportingInputs
from org.openprovenance.book.physical.client.integrator.TransportingOutputs import TransportingOutputs
from org.openprovenance.book.physical.client.integrator.UnpackingInputs_1 import UnpackingInputs_1
from org.openprovenance.book.physical.client.integrator.Unpacking_compositeInputs import Unpacking_compositeInputs
from org.openprovenance.book.physical.client.integrator.Unpacking_compositeOutputs import Unpacking_compositeOutputs
from org.openprovenance.book.responsibility.client.integrator.HandingoverInputs import HandingoverInputs
from org.openprovenance.book.responsibility.client.integrator.HandingoverOutputs import HandingoverOutputs


class TemplateConnection:
    def __init__(self):
        self.in_id = None
        self.in_template = None
        self.in_property = None
        self.out_id = None
        self.out_template = None
        self.out_property = None


class BoxWorkflow:

    MARKER1 = -1
    MARKER2 = -2

    def __init__(self, template_invoker, query):
        self.template_invoker = template_invoker
        self.query = query

        self.agent1_time = "2024-09-01T10:00:00Z"
        self.agent2_time = "2024-09-01T10:00:00Z"
        self.agent3_time = "2024-08-01T10:00:00Z"
        self.agent4_time = "2024-09-01T10:00:00Z"

        self.scale1_time = "2022-09-01T10:00:00Z"
        self.scale2_time = "2023-06-01T10:00:00Z"
        self.scale3_time = "2024-01-01T10:00:00Z"

        self.box_time = "2024-09-14T10:00:00Z"

        self.weighing1_time = "2024-09-15T10:00:00Z"
        self.pickup_time = "2024-10-01T10:00:00Z"
        self.drop1_time = "2024-10-01T17:00:00Z"
        self.handover_time = "2024-10-01T17:15:00Z"
        self.weighing2_time = "2024-10-01T18:12:00Z"
        self.handover_time2 = "2024-10-01T05:20:00Z"
        self.delivery_time = "2024-10-01T15:14:00Z"

        self.connections = []
        self.connections_no_agent = None

        self.connection_funs = []

    def new_template(self):
        self.connection_funs = []

    def generate_connections(self, to_template, count=None):
        if count is not None:
            # Select first count elements of connection_funs and remove them from the list
            selected_funs = []
            for i in range(count):
                selected_funs.append(self.connection_funs.pop(0))
            conns = [f(to_template) for f in selected_funs]
        else:
            conns = [f(to_template) for f in self.connection_funs]
            self.new_template()

        self.connections.extend(conns)
        return conns

    def flow_to_from(self, to_input_bean, to_property, from_template, from_property):
        # Get the value from from_template using getattr
        print(from_template.toJSON(), from_property)
        from_property_value = getattr(from_template, from_property)

        # Set the value on to_input_bean
        setattr(to_input_bean, to_property, from_property_value)

        # Create a function that will generate a TemplateConnection
        def fun(to_template):
            tc0 = TemplateConnection()

            tc0.out_id = getattr(from_template, 'ID')
            tc0.out_template = getattr(from_template, 'isA')
            tc0.out_property = from_property

            tc0.in_id = getattr(to_template, 'ID')
            tc0.in_template = getattr(to_template, 'isA')
            tc0.in_property = to_property

            return tc0

        self.connection_funs.append(fun)

        return fun

    def filter_out_agent_init(self, connections):
        return [tc for tc in connections if tc.out_template != "agent_init"]

    def run(self):
        # New agent-init for box owner
        agent_init_inputs0 = Agent_initInputs()
        agent_init_inputs0.location = "London"
        agent_init_inputs0.type = "Person"
        agent_init_inputs0.time = self.agent1_time
        agent_init_outputs0 = self.template_invoker.process_agent_init_inputs(agent_init_inputs0)

        # Initialize box
        box_init_inputs = Item_initInputs()
        box_init_inputs.type = "Box"
        box_init_inputs.time = self.box_time
        box_init_outputs = self.template_invoker.process_item_init_inputs(box_init_inputs)

        # First book
        book1_init_inputs = Item_initInputs()
        book1_init_inputs.type = "Book"
        book1_init_outputs = self.template_invoker.process_item_init_inputs(book1_init_inputs)

        # Second book
        book2_init_inputs = Item_initInputs()
        book2_init_inputs.type = "Book"
        book2_init_outputs = self.template_invoker.process_item_init_inputs(book2_init_inputs)
        print(book2_init_outputs.toJSON())

        # Composite pack
        packing_inputs_1 = PackingInputs_1()
        self.flow_to_from(packing_inputs_1, "item0", book1_init_outputs, "entity0")
        self.flow_to_from(packing_inputs_1, "item", book1_init_outputs, "entity")
        self.flow_to_from(packing_inputs_1, "packer", agent_init_outputs0, "agent0")
        self.flow_to_from(packing_inputs_1, "container0", box_init_outputs, "entity0")
        self.flow_to_from(packing_inputs_1, "container", box_init_outputs, "entity")
        packing_inputs_1.sealed = True
        packing_inputs_1.containerType = "Box"
        packing_inputs_1.adding = self.MARKER1
        packing_inputs_1.container1 = self.MARKER2

        packing_inputs_2 = PackingInputs_1()
        self.flow_to_from(packing_inputs_2, "item0", book2_init_outputs, "entity0")
        self.flow_to_from(packing_inputs_2, "item", book2_init_outputs, "entity")
        self.flow_to_from(packing_inputs_2, "packer", agent_init_outputs0, "agent0")
        self.flow_to_from(packing_inputs_2, "container0", box_init_outputs, "entity0")
        self.flow_to_from(packing_inputs_2, "container", box_init_outputs, "entity")
        packing_inputs_2.sealed = True
        packing_inputs_2.containerType = "Box"
        packing_inputs_2.adding = self.MARKER1
        packing_inputs_2.container1 = self.MARKER2

        print("Packing composite inputs:")
        packing_composite_inputs = Packing_compositeInputs()
        packing_composite_inputs.addElements(packing_inputs_1)
        packing_composite_inputs.addElements(packing_inputs_2)
        packing_composite_inputs.count = 2
        print(packing_composite_inputs)

        packing_composite_outputs = self.template_invoker.process_packing_composite_inputs(packing_composite_inputs)
        print("Packing composite outputs:")
        print(packing_composite_outputs)
        print(packing_composite_outputs.elements)
        self.generate_connections(packing_composite_outputs.elements.get(0), 5)  # note, process first 5 only
        self.generate_connections(packing_composite_outputs.elements.get(1))

        # New agent-init for scale
        agent_init_inputs_s1 = Agent_initInputs()
        agent_init_inputs_s1.location = "London"
        agent_init_inputs_s1.type = "Scale"
        agent_init_inputs_s1.time = self.scale1_time
        agent_init_outputs_s1 = self.template_invoker.process_agent_init_inputs(agent_init_inputs_s1)

        # First weighing
        weighing_inputs1 = WeighingInputs()
        self.flow_to_from(weighing_inputs1, "item0", packing_composite_outputs.elements.get(0), "container1")
        self.flow_to_from(weighing_inputs1, "item0", packing_composite_outputs.elements.get(1), "container1")
        self.flow_to_from(weighing_inputs1, "item", box_init_outputs, "entity")
        self.flow_to_from(weighing_inputs1, "agent", agent_init_outputs0, "agent0")
        self.flow_to_from(weighing_inputs1, "scale", agent_init_outputs_s1, "agent0")
        weighing_inputs1.weight = 10.0
        weighing_inputs1.time = self.weighing1_time
        weighing_outputs1 = self.template_invoker.process_weighing_inputs(weighing_inputs1)
        self.generate_connections(weighing_outputs1)

        # New agent-init for first transporter
        agent_init_inputs1 = Agent_initInputs()
        agent_init_inputs1.location = "Oxford"
        agent_init_inputs1.type = "Person"
        agent_init_inputs1.time = self.agent2_time
        agent_init_outputs1 = self.template_invoker.process_agent_init_inputs(agent_init_inputs1)

        # First handover
        handingover_inputs = HandingoverInputs()
        self.flow_to_from(handingover_inputs, "item0", weighing_outputs1, "item1")
        self.flow_to_from(handingover_inputs, "item", box_init_outputs, "entity")
        self.flow_to_from(handingover_inputs, "receiver", agent_init_outputs1, "agent0")
        self.flow_to_from(handingover_inputs, "giver", agent_init_outputs0, "agent0")
        handingover_inputs.time = self.pickup_time
        handingover_outputs = self.template_invoker.process_handingover_inputs(handingover_inputs)
        self.generate_connections(handingover_outputs)

        # First transporting
        transporting_inputs = TransportingInputs()
        self.flow_to_from(transporting_inputs, "item0", handingover_outputs, "item1")
        self.flow_to_from(transporting_inputs, "item", box_init_outputs, "entity")
        self.flow_to_from(transporting_inputs, "transporter", agent_init_outputs1, "agent0")
        transporting_inputs.transporter = agent_init_outputs1.agent0
        transporting_inputs.time = self.drop1_time
        transporting_outputs = self.template_invoker.process_transporting_inputs(transporting_inputs)
        self.generate_connections(transporting_outputs)

        # New agent-init for depot manager
        agent_init_inputs2 = Agent_initInputs()
        agent_init_inputs2.location = "London"
        agent_init_inputs2.type = "Person"
        agent_init_inputs2.time = self.agent2_time
        agent_init_outputs2 = self.template_invoker.process_agent_init_inputs(agent_init_inputs2)

        # Second handover
        handingover_inputs2 = HandingoverInputs()
        self.flow_to_from(handingover_inputs2, "item0", transporting_outputs, "item1")
        self.flow_to_from(handingover_inputs2, "item", box_init_outputs, "entity")
        self.flow_to_from(handingover_inputs2, "receiver", agent_init_outputs2, "agent0")
        self.flow_to_from(handingover_inputs2, "giver", agent_init_outputs1, "agent0")
        handingover_inputs2.time = self.handover_time
        handingover_outputs2 = self.template_invoker.process_handingover_inputs(handingover_inputs2)
        self.generate_connections(handingover_outputs2)

        # New agent-init for scale 2
        agent_init_inputs_s2 = Agent_initInputs()
        agent_init_inputs_s2.location = "London-Depot"
        agent_init_inputs_s2.type = "Scale"
        agent_init_inputs_s2.time = self.scale2_time
        agent_init_outputs_s2 = self.template_invoker.process_agent_init_inputs(agent_init_inputs_s2)

        # Second weighing
        weighing_inputs2 = WeighingInputs()
        self.flow_to_from(weighing_inputs2, "item0", handingover_outputs2, "item1")
        self.flow_to_from(weighing_inputs2, "item", box_init_outputs, "entity")
        self.flow_to_from(weighing_inputs2, "agent", agent_init_outputs2, "agent0")
        self.flow_to_from(weighing_inputs2, "scale", agent_init_outputs_s2, "agent0")
        weighing_inputs2.weight = 10.0
        weighing_inputs2.time = self.weighing2_time
        weighing_outputs2 = self.template_invoker.process_weighing_inputs(weighing_inputs2)
        self.generate_connections(weighing_outputs2)

        # New agent-init for second transporter
        agent_init_inputs3 = Agent_initInputs()
        agent_init_inputs3.location = "Oxford"
        agent_init_inputs3.type = "Person"
        agent_init_inputs3.time = self.agent3_time
        agent_init_outputs3 = self.template_invoker.process_agent_init_inputs(agent_init_inputs3)

        # Third handover
        handingover_inputs3 = HandingoverInputs()
        self.flow_to_from(handingover_inputs3, "item0", weighing_outputs2, "item1")
        self.flow_to_from(handingover_inputs3, "item", box_init_outputs, "entity")
        self.flow_to_from(handingover_inputs3, "receiver", agent_init_outputs3, "agent0")
        self.flow_to_from(handingover_inputs3, "giver", agent_init_outputs2, "agent0")
        handingover_inputs3.time = self.handover_time2
        handingover_outputs3 = self.template_invoker.process_handingover_inputs(handingover_inputs3)
        self.generate_connections(handingover_outputs3)

        # Second transporting
        transporting_inputs2 = TransportingInputs()
        self.flow_to_from(transporting_inputs2, "item0", handingover_outputs3, "item1")
        self.flow_to_from(transporting_inputs2, "item", box_init_outputs, "entity")
        self.flow_to_from(transporting_inputs2, "transporter", agent_init_outputs3, "agent0")
        transporting_inputs2.time = self.delivery_time
        transporting_outputs2 = self.template_invoker.process_transporting_inputs(transporting_inputs2)
        self.generate_connections(transporting_outputs2)
        # at this point, the item is with the transporter

        # New agent-init for recipient
        agent_init_inputs4 = Agent_initInputs()
        agent_init_inputs4.location = "Oxford"
        agent_init_inputs4.type = "Person"
        agent_init_inputs4.time = self.agent4_time
        agent_init_outputs4 = self.template_invoker.process_agent_init_inputs(agent_init_inputs4)

        # Fourth handover
        handingover_inputs4 = HandingoverInputs()
        self.flow_to_from(handingover_inputs4, "item0", transporting_outputs2, "item1")
        self.flow_to_from(handingover_inputs4, "item", box_init_outputs, "entity")
        self.flow_to_from(handingover_inputs4, "receiver", agent_init_outputs4, "agent0")
        self.flow_to_from(handingover_inputs4, "giver", agent_init_outputs3, "agent0")
        handingover_inputs4.time = self.delivery_time
        handingover_outputs4 = self.template_invoker.process_handingover_inputs(handingover_inputs4)
        self.generate_connections(handingover_outputs4)
        # at this point, the item is with the recipient

        # New agent-init for scale 3
        agent_init_inputs_s3 = Agent_initInputs()
        agent_init_inputs_s3.location = "Brighton"
        agent_init_inputs_s3.type = "Scale"
        agent_init_inputs_s3.time = self.scale3_time
        agent_init_outputs_s3 = self.template_invoker.process_agent_init_inputs(agent_init_inputs_s3)

        # Recipient weighing item
        weighing_inputs3 = WeighingInputs()
        self.flow_to_from(weighing_inputs3, "item0", handingover_outputs4, "item1")
        self.flow_to_from(weighing_inputs3, "item", box_init_outputs, "entity")
        self.flow_to_from(weighing_inputs3, "agent", agent_init_outputs4, "agent0")
        self.flow_to_from(weighing_inputs3, "scale", agent_init_outputs_s3, "agent0")
        weighing_inputs3.weight = 15.0
        weighing_inputs3.time = self.delivery_time
        weighing_outputs3 = self.template_invoker.process_weighing_inputs(weighing_inputs3)
        self.generate_connections(weighing_outputs3)
        # item should weigh 10.0, so this is a discrepancy

        # Unpack book1 and book2
        unpacking_inputs1 = UnpackingInputs_1()
        self.flow_to_from(unpacking_inputs1, "container", box_init_outputs, "entity")
        self.flow_to_from(unpacking_inputs1, "container0", weighing_outputs3, "item1")
        self.flow_to_from(unpacking_inputs1, "item", book1_init_outputs, "entity")
        self.flow_to_from(unpacking_inputs1, "unpacker", agent_init_outputs4, "agent0")
        unpacking_inputs1.container1 = self.MARKER1
        unpacking_inputs1.removing = self.MARKER2

        unpacking_inputs2 = UnpackingInputs_1()
        self.flow_to_from(unpacking_inputs2, "container", box_init_outputs, "entity")
        self.flow_to_from(unpacking_inputs2, "container0", weighing_outputs3, "item1")
        self.flow_to_from(unpacking_inputs2, "item", book2_init_outputs, "entity")
        self.flow_to_from(unpacking_inputs2, "unpacker", agent_init_outputs4, "agent0")
        unpacking_inputs2.container1 = self.MARKER1
        unpacking_inputs2.removing = self.MARKER2

        unpacking_composite_inputs = Unpacking_compositeInputs()
        unpacking_composite_inputs.addElements(unpacking_inputs1)
        unpacking_composite_inputs.addElements(unpacking_inputs2)
        unpacking_composite_inputs.count = 2
        unpacking_composite_outputs = self.template_invoker.process_unpacking_composite_inputs(unpacking_composite_inputs)
        self.generate_connections(unpacking_composite_outputs.elements.get(0), 4)  # note, process first 4 only
        self.generate_connections(unpacking_composite_outputs.elements.get(1))

        self.connections_no_agent = self.filter_out_agent_init(self.connections)

        # Return all inputs and outputs
        return [
            box_init_inputs, box_init_outputs,
            book1_init_inputs, book1_init_outputs,
            book2_init_inputs, book2_init_outputs,
            packing_composite_inputs, packing_composite_outputs,

            agent_init_inputs0, agent_init_outputs0,
            agent_init_inputs1, agent_init_outputs1,
            agent_init_inputs2, agent_init_outputs2,
            agent_init_inputs3, agent_init_outputs3,
            agent_init_inputs4, agent_init_outputs4,
            agent_init_inputs_s1, agent_init_outputs_s1,
            agent_init_inputs_s2, agent_init_outputs_s2,
            agent_init_inputs_s3, agent_init_outputs_s3,

            handingover_inputs, handingover_outputs,
            transporting_inputs, transporting_outputs,
            handingover_inputs2, handingover_outputs2,
            handingover_inputs3, handingover_outputs3,
            transporting_inputs2, transporting_outputs2,
            handingover_inputs4, handingover_outputs4,
            weighing_inputs3, weighing_outputs3,

            unpacking_composite_inputs, unpacking_composite_outputs,
            self.connections,
            self.connections_no_agent
        ]
