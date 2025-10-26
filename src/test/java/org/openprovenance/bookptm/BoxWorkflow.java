package org.openprovenance.bookptm;


import org.openprovenance.bk.physical.client.integrator.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BoxWorkflow {

    private final InputOutputProcessor templateInvoker;
    private final Function<String, List<List<Object>>> query;

    String agent1Time ="2024-09-01T10:00:00Z";
    String agent2Time ="2024-09-01T10:00:00Z";
    String agent3Time ="2024-08-01T10:00:00Z";
    String agent4Time ="2024-09-01T10:00:00Z";

    String scale1Time ="2022-09-01T10:00:00Z";
    String scale2Time ="2023-06-01T10:00:00Z";
    String scale3Time ="2024-01-01T10:00:00Z";

    String boxTime ="2024-09-14T10:00:00Z";

    String weighing1Time ="2024-09-15T10:00:00Z";
    String pickupTime ="2024-10-01T10:00:00Z";
    String drop1Time ="2024-10-01T17:00:00Z";
    String handoverTime ="2024-10-01T17:15:00Z";
    String weighing2Time ="2024-10-01T18:12:00Z";
    String handoverTime2 ="2024-10-01T05:20:00Z";
    String deliveryTime ="2024-10-01T15:14:00Z";

    public BoxWorkflow(InputOutputProcessor templateInvoker, Function<String, List<List<Object>>> query) {
        this.templateInvoker=templateInvoker;
        this.query=query;
    }

    public List<TemplateConnection> connections=new LinkedList<>();
    public List<TemplateConnection> connectionsNoAgent;

    public List<Object> run() {



        // new agent-init for box owner
        Agent_initInputs agent_initInputs0=new Agent_initInputs();
        agent_initInputs0.location="London";
        agent_initInputs0.type="Person";
        agent_initInputs0.time=agent1Time;
        Agent_initOutputs agent_initOutputs0=templateInvoker.process(agent_initInputs0);


        Item_initInputs box_initInputs=new Item_initInputs();
        box_initInputs.type="Box";
        box_initInputs.time=boxTime;
        Item_initOutputs box_initOutputs=templateInvoker.process(box_initInputs);

        Item_initInputs book1_initInputs=new Item_initInputs();
        book1_initInputs.type="Book";
        Item_initOutputs book1_initOutputs=templateInvoker.process(book1_initInputs);

        // second book
        Item_initInputs book2_initInputs=new Item_initInputs();
        book2_initInputs.type="Book";
        Item_initOutputs book2_initOutputs=templateInvoker.process(book2_initInputs);

        // composite pack
        PackingInputs_1 packingInputs_1=new PackingInputs_1();
        //packingInputs_1.item=book1_initOutputs.entity0;
        flowToFrom(packingInputs_1, "item", book1_initOutputs, "entity0");
        packingInputs_1.sealed=true;
        //packingInputs_1.packer=agent_initOutputs0.agent0;
        flowToFrom(packingInputs_1, "packer", agent_initOutputs0, "agent0");
        packingInputs_1.adding=-1;
        //packingInputs_1.container0=box_initOutputs.entity0;
        flowToFrom(packingInputs_1, "container0", box_initOutputs, "entity0");
        packingInputs_1.container1=-2;
        //packingInputs_1.container=box_initOutputs.entity;
        flowToFrom(packingInputs_1, "container", box_initOutputs, "entity");


        PackingInputs_1 packingInputs_2=new PackingInputs_1();
        //packingInputs_2.item=book2_initOutputs.entity0;
        flowToFrom(packingInputs_2, "item", book2_initOutputs, "entity0");
        packingInputs_2.sealed=true;
        //packingInputs_2.packer=agent_initOutputs0.agent0;
        flowToFrom(packingInputs_2, "packer", agent_initOutputs0, "agent0");
        packingInputs_2.adding=-1;
        //packingInputs_2.container0=box_initOutputs.entity0;
        flowToFrom(packingInputs_2, "container0", box_initOutputs, "entity0");
        packingInputs_2.container1=-2;
        //packingInputs_2.container=box_initOutputs.entity;
        flowToFrom(packingInputs_2, "container", box_initOutputs, "entity");


        Packing_compositeInputs packing_compositeInputs=new Packing_compositeInputs();
        packing_compositeInputs.__addElements(packingInputs_1);
        packing_compositeInputs.__addElements(packingInputs_2);
        packing_compositeInputs.count=2;
        Packing_compositeOutputs packing_compositeOutputs=templateInvoker.process(packing_compositeInputs);
        generateConnections(packing_compositeOutputs.__elements.get(0),4);  // note, process first 4 only

        generateConnections(packing_compositeOutputs.__elements.get(1));




        // new agent-init for scale
        Agent_initInputs agent_initInputsS1=new Agent_initInputs();
        agent_initInputsS1.location="London";
        agent_initInputsS1.type="Scale";
        agent_initInputsS1.time=scale1Time;
        Agent_initOutputs agent_initOutputsS1=templateInvoker.process(agent_initInputsS1);



        WeighingInputs weighingInputs1=new WeighingInputs();
        //weighingInputs1.item0=item0;
        flowToFrom(weighingInputs1,"item0", packing_compositeOutputs.__elements.get(0), "container1");
        flowToFrom(weighingInputs1,"item0", packing_compositeOutputs.__elements.get(1), "container1");
        //weighingInputs1.item=item;
        flowToFrom(weighingInputs1,"item", box_initOutputs, "entity");
        //weighingInputs1.agent=agent_initOutputs0.agent0;
        flowToFrom(weighingInputs1,"agent", agent_initOutputs0, "agent0");
        //weighingInputs1.scale=agent_initOutputsS1.agent0;
        flowToFrom(weighingInputs1,"scale", agent_initOutputsS1, "agent0");
        weighingInputs1.weight=10.0d;
        weighingInputs1.time=weighing1Time;
        WeighingOutputs weighingOutputs1=templateInvoker.process(weighingInputs1);
        generateConnections(weighingOutputs1);


        //  new agent-init for first transporter
        Agent_initInputs agent_initInputs1=new Agent_initInputs();
        agent_initInputs1.location="Oxford";
        agent_initInputs1.type="Person";
        agent_initInputs1.time=agent2Time;
        Agent_initOutputs agent_initOutputs1=templateInvoker.process(agent_initInputs1);


        HandoverInputs handoverInputs=new HandoverInputs();
        flowToFrom(handoverInputs, "item0", weighingOutputs1, "item1");
        //handoverInputs.item0=weighingOutputs1.item1;
        flowToFrom(handoverInputs, "item", box_initOutputs, "entity");
        //handoverInputs.item=item;
        flowToFrom(handoverInputs, "receiver", agent_initOutputs1, "agent0");
        //handoverInputs.receiver=agent_initOutputs1.agent0;
        flowToFrom(handoverInputs, "giver", agent_initOutputs0, "agent0");
        //handoverInputs.giver=agent_initOutputs0.agent0;
        handoverInputs.time=pickupTime;
        HandoverOutputs handoverOutputs=templateInvoker.process(handoverInputs);
        generateConnections(handoverOutputs);



        TransportingInputs transportingInputs=new TransportingInputs();
        flowToFrom(transportingInputs, "item0", handoverOutputs, "item1");
        //transportingInputs.item0=handoverOutputs.item1;
        flowToFrom(transportingInputs, "item", box_initOutputs, "entity");
        //transportingInputs.item=item;
        flowToFrom(transportingInputs, "transporter", agent_initOutputs1, "agent0");
        transportingInputs.transporter=agent_initOutputs1.agent0;
        transportingInputs.time=drop1Time;
        TransportingOutputs transportingOutputs=templateInvoker.process(transportingInputs);
        generateConnections(transportingOutputs);


        //  new agent-init for depot manager
        Agent_initInputs agent_initInputs2=new Agent_initInputs();
        agent_initInputs2.location="London";
        agent_initInputs2.type="Person";
        agent_initInputs2.time=agent2Time;
        Agent_initOutputs agent_initOutputs2=templateInvoker.process(agent_initInputs2);

        HandoverInputs handoverInputs2=new HandoverInputs();
        //handoverInputs2.item0=transportingOutputs.item1;
        flowToFrom(handoverInputs2, "item0", transportingOutputs, "item1");
        //handoverInputs2.item=item;
        flowToFrom(handoverInputs2, "item", box_initOutputs, "entity");
        //handoverInputs2.receiver=agent_initOutputs2.agent0;
        flowToFrom(handoverInputs2, "receiver", agent_initOutputs2, "agent0");
        //handoverInputs2.giver=agent_initOutputs1.agent0;
        flowToFrom(handoverInputs2, "giver", agent_initOutputs1, "agent0");
        handoverInputs2.time= handoverTime;
        HandoverOutputs handoverOutputs2=templateInvoker.process(handoverInputs2);
        generateConnections(handoverOutputs2);


        // new agent-init for box owner
        Agent_initInputs agent_initInputsS2=new Agent_initInputs();
        agent_initInputsS2.location="London-Depot";
        agent_initInputsS2.type="Scale";
        agent_initInputsS2.time=scale2Time;
        Agent_initOutputs agent_initOutputsS2=templateInvoker.process(agent_initInputsS2);



        WeighingInputs weighingInputs2=new WeighingInputs();
        //weighingInputs2.item0=handoverOutputs2.item1;
        flowToFrom(weighingInputs2, "item0", handoverOutputs2, "item1");
        //weighingInputs2.item=item;
        flowToFrom(weighingInputs2, "item", box_initOutputs, "entity");
        //weighingInputs2.agent=agent_initOutputs2.agent0;
        flowToFrom(weighingInputs2, "agent", agent_initOutputs2, "agent0");
        //weighingInputs2.scale=agent_initOutputsS2.agent0;
        flowToFrom(weighingInputs2, "scale", agent_initOutputsS2, "agent0");
        weighingInputs2.weight=10.0d;
        weighingInputs2.time=weighing2Time;
        WeighingOutputs weighingOutputs2=templateInvoker.process(weighingInputs2);
        generateConnections(weighingOutputs2);

        //  new agent-init for first transporter
        Agent_initInputs agent_initInputs3=new Agent_initInputs();
        agent_initInputs3.location="Oxford";
        agent_initInputs3.type="Person";
        agent_initInputs3.time=agent3Time;
        Agent_initOutputs agent_initOutputs3=templateInvoker.process(agent_initInputs3);

        HandoverInputs handoverInputs3=new HandoverInputs();
        //handoverInputs3.item0=weighingOutputs2.item1;
        flowToFrom(handoverInputs3, "item0", weighingOutputs2, "item1");
        //handoverInputs3.item=item;
        flowToFrom(handoverInputs3, "item", box_initOutputs, "entity");
        //handoverInputs3.receiver=agent_initOutputs3.agent0;
        flowToFrom(handoverInputs3, "receiver", agent_initOutputs3, "agent0");
        //handoverInputs3.giver=agent_initOutputs2.agent0;
        flowToFrom(handoverInputs3, "giver", agent_initOutputs2, "agent0");
        handoverInputs3.time=handoverTime2;
        HandoverOutputs handoverOutputs3=templateInvoker.process(handoverInputs3);
        generateConnections(handoverOutputs3);



        TransportingInputs transportingInputs2=new TransportingInputs();
        //transportingInputs2.item0=handoverOutputs3.item1;
        flowToFrom(transportingInputs2, "item0", handoverOutputs3, "item1");
        //transportingInputs2.item=item;
        flowToFrom(transportingInputs2, "item", box_initOutputs, "entity");
        //transportingInputs2.transporter=agent_initOutputs3.agent0;
        flowToFrom(transportingInputs2, "transporter", agent_initOutputs3, "agent0");
        transportingInputs2.time=deliveryTime;
        TransportingOutputs transportingOutputs2=templateInvoker.process(transportingInputs2);
        generateConnections(transportingOutputs2);
        // at this point, the item is with the transporter


        Agent_initInputs agent_initInputs4=new Agent_initInputs();
        agent_initInputs4.location="Oxford";
        agent_initInputs4.type="Person";
        agent_initInputs4.time=agent4Time;
        Agent_initOutputs agent_initOutputs4=templateInvoker.process(agent_initInputs4);



        HandoverInputs handoverInputs4=new HandoverInputs();
        //handoverInputs4.item0=transportingOutputs2.item1;
        flowToFrom(handoverInputs4, "item0", transportingOutputs2, "item1");
        //handoverInputs4.item=item;
        flowToFrom(handoverInputs4, "item", box_initOutputs, "entity");
        //handoverInputs4.receiver= agent_initOutputs4.agent0;
        flowToFrom(handoverInputs4, "receiver", agent_initOutputs4, "agent0");
        //handoverInputs4.giver=agent_initOutputs3.agent0;
        flowToFrom(handoverInputs4, "giver", agent_initOutputs3, "agent0");
        handoverInputs4.time=deliveryTime;
        HandoverOutputs handoverOutputs4=templateInvoker.process(handoverInputs4);
        generateConnections(handoverOutputs4);
        // at this point, the item is with the recipient

        // new agent-init for box owner
        Agent_initInputs agent_initInputsS3=new Agent_initInputs();
        agent_initInputsS3.location="Brighton";
        agent_initInputsS3.type="Scale";
        agent_initInputsS3.time=scale3Time;
        Agent_initOutputs agent_initOutputsS3=templateInvoker.process(agent_initInputsS3);



        // recipient weighing item
        WeighingInputs weighingInputs3=new WeighingInputs();
        //weighingInputs3.item0=handoverOutputs4.item1;
        flowToFrom(weighingInputs3, "item0", handoverOutputs4, "item1");
        //weighingInputs3.item=item;
        flowToFrom(weighingInputs3, "item", box_initOutputs, "entity");
        //weighingInputs3.agent=agent_initOutputs4.agent0;
        flowToFrom(weighingInputs3, "agent", agent_initOutputs4, "agent0");
        //weighingInputs3.scale=agent_initOutputsS3.agent0;
        flowToFrom(weighingInputs3, "scale", agent_initOutputsS3, "agent0");
        weighingInputs3.weight=15.0d;
        weighingInputs3.time=deliveryTime;
        WeighingOutputs weighingOutputs3=templateInvoker.process(weighingInputs3);
        generateConnections(weighingOutputs3);
        // item should weigh 10.0, so this is a discrepancy

        // unpack book1 and book2

        UnpackingInputs_1 unpackingInputs1=new UnpackingInputs_1();
        //unpackingInputs1.container=box_initOutputs.entity;
        flowToFrom(unpackingInputs1,"container", box_initOutputs, "entity");
        //unpackingInputs1.container0=weighingOutputs3.item1;
        flowToFrom(unpackingInputs1,"container0", weighingOutputs3, "item1");
        unpackingInputs1.container1=-1;
        //unpackingInputs1.item=book1_initOutputs.entity;
        flowToFrom(unpackingInputs1,"item", book1_initOutputs, "entity");

        UnpackingInputs_1 unpackingInputs2=new UnpackingInputs_1();
        //unpackingInputs2.container=box_initOutputs.entity;
        flowToFrom(unpackingInputs2,"container", box_initOutputs, "entity");
        //unpackingInputs2.container0=weighingOutputs3.item1;
        flowToFrom(unpackingInputs2,"container0", weighingOutputs3, "item1");

        unpackingInputs2.container1=-1;
        //unpackingInputs2.item=book2_initOutputs.entity;
        flowToFrom(unpackingInputs2,"item", book2_initOutputs, "entity");


        Unpacking_compositeInputs unpacking_compositeInputs=new Unpacking_compositeInputs();
        unpacking_compositeInputs.__addElements(unpackingInputs1);
        unpacking_compositeInputs.__addElements(unpackingInputs2);
        unpacking_compositeInputs.count=2;
        Unpacking_compositeOutputs unpacking_compositeOutputs=templateInvoker.process(unpacking_compositeInputs);
        generateConnections(unpacking_compositeOutputs.__elements.get(0),3);  // note, process first 3 only
        generateConnections(unpacking_compositeOutputs.__elements.get(1));



        //connections.addAll(
          //      createConnections(box_initOutputs,book1_initOutputs, book2_initOutputs, packing_compositeOutputs, unpacking_compositeOutputs, agent_initOutputs0, weighingOutputs1, handoverOutputs, agent_initOutputs1, transportingOutputs, handoverOutputs2, agent_initOutputs2, weighingOutputs2, handoverOutputs3, agent_initOutputs3, transportingOutputs2, handoverOutputs4, weighingOutputs3, agent_initOutputs4, agent_initOutputsS1, agent_initOutputsS2, agent_initOutputsS3, true));
        connectionsNoAgent=filterOutAgentInit(connections);
//                createConnections(box_initOutputs,book1_initOutputs, book2_initOutputs, packing_compositeOutputs, unpacking_compositeOutputs, agent_initOutputs0, weighingOutputs1, handoverOutputs, agent_initOutputs1, transportingOutputs, handoverOutputs2, agent_initOutputs2, weighingOutputs2, handoverOutputs3, agent_initOutputs3, transportingOutputs2, handoverOutputs4, weighingOutputs3, agent_initOutputs4, agent_initOutputsS1, agent_initOutputsS2, agent_initOutputsS3, false);


        // return all inputs and outputs

        return Arrays.asList(
                box_initInputs, box_initOutputs,
                book1_initInputs, book1_initOutputs,
                book2_initInputs, book2_initOutputs,
                packing_compositeInputs, packing_compositeOutputs,

                agent_initInputs0, agent_initOutputs0,
                agent_initInputs1, agent_initOutputs1,
                agent_initInputs2, agent_initOutputs2,
                agent_initInputs3, agent_initOutputs3,
                agent_initInputs4, agent_initOutputs4,
                agent_initInputsS1, agent_initOutputsS1,
                agent_initInputsS2, agent_initOutputsS2,
                agent_initInputsS3, agent_initOutputsS3,

                handoverInputs, handoverOutputs,
                transportingInputs, transportingOutputs,
                handoverInputs2, handoverOutputs2,
                handoverInputs3, handoverOutputs3,
                transportingInputs2, transportingOutputs2,
                handoverInputs4, handoverOutputs4,
                weighingInputs3, weighingOutputs3,

                unpacking_compositeInputs, unpacking_compositeOutputs,
                connections,
                connectionsNoAgent);

    }

    // filter list of connections to remove those with an out_template of agent_init
    List<TemplateConnection> filterOutAgentInit(List<TemplateConnection> connections) {
        return connections.stream()
                .filter(tc -> !tc.out_template.equals("agent_init"))
                .collect(Collectors.toList());
    }

    List<Function<Object,TemplateConnection>> connectionFuns=new LinkedList<>();
    void newTemplate() {
        connectionFuns=new LinkedList<>();
    }


    List<TemplateConnection> generateConnections(Object toTemplate,int count) {
        // select first count elements of connectionFuns and remove them from the list
        List<Function<Object,TemplateConnection>> selectedFuns=new LinkedList<>();
        for (int i=0;i<count;i++) {
            selectedFuns.add(connectionFuns.remove(0));
        }
        List<TemplateConnection> conns=selectedFuns.stream().map(f -> f.apply(toTemplate)).collect(Collectors.toList());
        connections.addAll(conns);
        return conns;
    }

    List<TemplateConnection> generateConnections(Object toTemplate) {
        List<TemplateConnection> conns=connectionFuns.stream().map(f -> f.apply(toTemplate)).collect(Collectors.toList());
        newTemplate();
        connections.addAll(conns);
        return conns;
    }




    private Function<Object,TemplateConnection> flowToFrom(Object toInputBean, String toProperty, Object fromTemplate, String fromProperty) {
        //tc0.out_id= fromTemplate.ID;
        //tc0.out_template= fromTemplate.isA;
        //tc0.out_property= fromProperty;
        //tc0.in_id= toTemplate.ID;
        //tc0.in_template= toTemplate.isA;
        //tc0.in_property= toProperty;

        // using reflection, assign toProperty of toTemplate object with the value of fromProperty of fromTemplate object

        try {

            java.lang.reflect.Field fromPropertyField = fromTemplate.getClass().getDeclaredField(fromProperty);
            fromPropertyField.setAccessible(true);
            Object fromPropertyFieldValue = fromPropertyField.get(fromTemplate);

            java.lang.reflect.Field toPropertyField = toInputBean.getClass().getDeclaredField(toProperty);
            toPropertyField.setAccessible(true);
            toPropertyField.set(toInputBean, fromPropertyFieldValue);

            Function<Object,TemplateConnection> fun= (Object toTemplate) -> {
                TemplateConnection tc0=new TemplateConnection();

                try {
                    java.lang.reflect.Field fromID = fromTemplate.getClass().getDeclaredField("ID");
                    fromID.setAccessible(true);
                    tc0.out_id = (Integer) fromID.get(fromTemplate);
                    java.lang.reflect.Field fromIsA = fromTemplate.getClass().getDeclaredField("isA");
                    fromIsA.setAccessible(true);
                    tc0.out_template = (String) fromIsA.get(fromTemplate);
                    tc0.out_property = fromProperty;

                    java.lang.reflect.Field toID = toTemplate.getClass().getDeclaredField("ID");
                    toID.setAccessible(true);
                    tc0.in_id = (Integer) toID.get(toTemplate);
                    java.lang.reflect.Field toIsa = toTemplate.getClass().getDeclaredField("isA");
                    toIsa.setAccessible(true);
                    tc0.in_template = (String) toIsa.get(toTemplate);
                    tc0.in_property = toProperty;

                    return tc0;
                }  catch (NoSuchFieldException | IllegalAccessException e1) {
                    throw new RuntimeException(e1);
                }
            };

            connectionFuns.add(fun);

            return fun;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}