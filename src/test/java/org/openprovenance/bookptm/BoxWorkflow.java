package org.openprovenance.bookptm;


import org.openprovenance.bk.physical.client.integrator.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class BoxWorkflow {

    private final InputOutputProcessor templateInvoker;
    private final Function<String, List<List<Object>>> query;
    Integer item0=11;
    Integer item=10;

    String agent1Time ="2024-09-01T10:00:00Z";
    String agent2Time ="2024-09-01T10:00:00Z";
    String agent3Time ="2024-08-01T10:00:00Z";
    String agent4Time ="2024-09-01T10:00:00Z";

    String scale1Time ="2022-09-01T10:00:00Z";
    String scale2Time ="2023-06-01T10:00:00Z";
    String scale3Time ="2024-01-01T10:00:00Z";


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

    public List<TemplateConnection> connections;
    public List<TemplateConnection> connectionsNoAgent;

    public List<Object> run() {

        // new agent-init for box owner
        Agent_initInputs agent_initInputs0=new Agent_initInputs();
        agent_initInputs0.location="London";
        agent_initInputs0.type="Person";
        agent_initInputs0.time=agent1Time;
        Agent_initOutputs agent_initOutputs0=templateInvoker.process(agent_initInputs0);


        // new agent-init for box owner
        Agent_initInputs agent_initInputsS1=new Agent_initInputs();
        agent_initInputsS1.location="London";
        agent_initInputsS1.type="Scale";
        agent_initInputsS1.time=scale1Time;
        Agent_initOutputs agent_initOutputsS1=templateInvoker.process(agent_initInputsS1);



        WeighingInputs weighingInputs1=new WeighingInputs();
        weighingInputs1.item0=item0;
        weighingInputs1.item=item;
        weighingInputs1.agent=agent_initOutputs0.agent0;
        weighingInputs1.scale=agent_initOutputsS1.agent0;
        weighingInputs1.weight=10.0d;
        weighingInputs1.time=weighing1Time;
        WeighingOutputs weighingOutputs1=templateInvoker.process(weighingInputs1);


        //  new agent-init for first transporter
        Agent_initInputs agent_initInputs1=new Agent_initInputs();
        agent_initInputs1.location="Oxford";
        agent_initInputs1.type="Person";
        agent_initInputs1.time=agent2Time;
        Agent_initOutputs agent_initOutputs1=templateInvoker.process(agent_initInputs1);


        HandoverInputs handoverInputs=new HandoverInputs();
        handoverInputs.item0=weighingOutputs1.item1;
        handoverInputs.item=item;
        handoverInputs.receiver=agent_initOutputs1.agent0;
        handoverInputs.giver=agent_initOutputs0.agent0;
        handoverInputs.time=pickupTime;
        HandoverOutputs handoverOutputs=templateInvoker.process(handoverInputs);


        TransportingInputs transportingInputs=new TransportingInputs();
        transportingInputs.item0=handoverOutputs.item1;
        transportingInputs.item=item;
        transportingInputs.transporter=agent_initOutputs1.agent0;
        transportingInputs.time=drop1Time;
        TransportingOutputs transportingOutputs=templateInvoker.process(transportingInputs);

        //  new agent-init for depot manager
        Agent_initInputs agent_initInputs2=new Agent_initInputs();
        agent_initInputs2.location="London";
        agent_initInputs2.type="Person";
        agent_initInputs2.time=agent2Time;
        Agent_initOutputs agent_initOutputs2=templateInvoker.process(agent_initInputs2);


        HandoverInputs handoverInputs2=new HandoverInputs();
        handoverInputs2.item0=transportingOutputs.item1;
        handoverInputs2.item=item;
        handoverInputs2.receiver=agent_initOutputs2.agent0;
        handoverInputs2.giver=agent_initOutputs1.agent0;
        handoverInputs2.time= handoverTime;
        HandoverOutputs handoverOutputs2=templateInvoker.process(handoverInputs2);


        // new agent-init for box owner
        Agent_initInputs agent_initInputsS2=new Agent_initInputs();
        agent_initInputsS2.location="London-Depot";
        agent_initInputsS2.type="Scale";
        agent_initInputsS2.time=scale2Time;
        Agent_initOutputs agent_initOutputsS2=templateInvoker.process(agent_initInputsS2);



        WeighingInputs weighingInputs2=new WeighingInputs();
        weighingInputs2.item0=handoverOutputs2.item1;
        weighingInputs2.item=item;
        weighingInputs2.agent=agent_initOutputs2.agent0;
        weighingInputs2.scale=agent_initOutputsS2.agent0;
        weighingInputs2.weight=10.0d;
        weighingInputs2.time=weighing2Time;
        WeighingOutputs weighingOutputs2=templateInvoker.process(weighingInputs2);
        //  new agent-init for first transporter
        Agent_initInputs agent_initInputs3=new Agent_initInputs();
        agent_initInputs3.location="Oxford";
        agent_initInputs3.type="Person";
        agent_initInputs3.time=agent3Time;
        Agent_initOutputs agent_initOutputs3=templateInvoker.process(agent_initInputs3);

        HandoverInputs handoverInputs3=new HandoverInputs();
        handoverInputs3.item0=weighingOutputs2.item1;
        handoverInputs3.item=item;
        handoverInputs3.receiver=agent_initOutputs3.agent0;
        handoverInputs3.giver=agent_initOutputs2.agent0;
        handoverInputs3.time=handoverTime2;
        HandoverOutputs handoverOutputs3=templateInvoker.process(handoverInputs3);



        TransportingInputs transportingInputs2=new TransportingInputs();
        transportingInputs2.item0=handoverOutputs3.item1;
        transportingInputs2.item=item;
        transportingInputs2.transporter=agent_initOutputs3.agent0;
        transportingInputs2.time=deliveryTime;
        TransportingOutputs transportingOutputs2=templateInvoker.process(transportingInputs2);
        // at this point, the item is with the transporter


        Agent_initInputs agent_initInputs4=new Agent_initInputs();
        agent_initInputs4.location="Oxford";
        agent_initInputs4.type="Person";
        agent_initInputs4.time=agent4Time;
        Agent_initOutputs agent_initOutputs4=templateInvoker.process(agent_initInputs4);



        HandoverInputs handoverInputs4=new HandoverInputs();
        handoverInputs4.item0=transportingOutputs2.item1;
        handoverInputs4.item=item;
        handoverInputs4.receiver= agent_initOutputs4.agent0;
        handoverInputs4.giver=agent_initOutputs3.agent0;
        handoverInputs4.time=deliveryTime;
        HandoverOutputs handoverOutputs4=templateInvoker.process(handoverInputs4);
        // at this point, the item is with the recipient

        // new agent-init for box owner
        Agent_initInputs agent_initInputsS3=new Agent_initInputs();
        agent_initInputsS3.location="Brighton";
        agent_initInputsS3.type="Scale";
        agent_initInputsS3.time=scale3Time;
        Agent_initOutputs agent_initOutputsS3=templateInvoker.process(agent_initInputsS3);



        // recipient weighing item
        WeighingInputs weighingInputs3=new WeighingInputs();
        weighingInputs3.item0=handoverOutputs4.item1;
        weighingInputs3.item=item;
        weighingInputs3.agent=agent_initOutputs4.agent0;
        weighingInputs3.scale=agent_initOutputsS3.agent0;
        weighingInputs3.weight=15.0d;
        weighingInputs3.time=deliveryTime;
        WeighingOutputs weighingOutputs3=templateInvoker.process(weighingInputs3);
        // item should weigh 10.0, so this is a discrepancy


        connections=
                createConnections(agent_initOutputs0, weighingOutputs1, handoverOutputs, agent_initOutputs1, transportingOutputs, handoverOutputs2, agent_initOutputs2, weighingOutputs2, handoverOutputs3, agent_initOutputs3, transportingOutputs2, handoverOutputs4, weighingOutputs3, agent_initOutputs4, agent_initOutputsS1, agent_initOutputsS2, agent_initOutputsS3, true);
        connectionsNoAgent=
                createConnections(agent_initOutputs0, weighingOutputs1, handoverOutputs, agent_initOutputs1, transportingOutputs, handoverOutputs2, agent_initOutputs2, weighingOutputs2, handoverOutputs3, agent_initOutputs3, transportingOutputs2, handoverOutputs4, weighingOutputs3, agent_initOutputs4, agent_initOutputsS1, agent_initOutputsS2, agent_initOutputsS3, false);


        // return all inputs and outputs

        return Arrays.asList(
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
                connections,
                connectionsNoAgent);

    }

    private List<TemplateConnection> createConnections(
            Agent_initOutputs agent_initOutputs0,
            WeighingOutputs weighingOutputs1,
            HandoverOutputs handoverOutputs,
            Agent_initOutputs agent_initOutputs1,
            TransportingOutputs transportingOutputs,
            HandoverOutputs handoverOutputs2,
            Agent_initOutputs agent_initOutputs2,
            WeighingOutputs weighingOutputs2,
            HandoverOutputs handoverOutputs3,
            Agent_initOutputs agent_initOutputs3,
            TransportingOutputs transportingOutputs2,
            HandoverOutputs handoverOutputs4,
            WeighingOutputs weighingOutputs3,
            Agent_initOutputs agent_initOutputs4,
            Agent_initOutputs agent_initOutputsS1,
            Agent_initOutputs agent_initOutputsS2,
            Agent_initOutputs agent_initOutputsS3, boolean withAgent) {

        List<TemplateConnection> connections=new LinkedList<>();

        if (withAgent) {
            TemplateConnection tcag1 = new TemplateConnection();
            tcag1.out_id = agent_initOutputs0.ID;
            tcag1.out_template = agent_initOutputs0.isA;
            tcag1.out_property = "agent0";
            tcag1.in_id = weighingOutputs1.ID;
            tcag1.in_template = weighingOutputs1.isA;
            tcag1.in_property = "agent";
            connections.add(tcag1);

            TemplateConnection tcag1b = new TemplateConnection();
            tcag1b.out_id = agent_initOutputs0.ID;
            tcag1b.out_template = agent_initOutputs0.isA;
            tcag1b.out_property = "agent0";
            tcag1b.in_id = handoverOutputs.ID;
            tcag1b.in_template = handoverOutputs.isA;
            tcag1b.in_property = "giver";
            connections.add(tcag1b);

        }

        //
        TemplateConnection tc0=new TemplateConnection();
        tc0.out_id= weighingOutputs1.ID;
        tc0.out_template= weighingOutputs1.isA;
        tc0.out_property="item1";
        tc0.in_id= handoverOutputs.ID;
        tc0.in_template= handoverOutputs.isA;
        tc0.in_property="giver";
        connections.add(tc0);

        if (withAgent) {

            TemplateConnection tcag2 = new TemplateConnection();
            tcag2.out_id = agent_initOutputs1.ID;
            tcag2.out_template = agent_initOutputs1.isA;
            tcag2.out_property = "agent0";
            tcag2.in_id = handoverOutputs.ID;
            tcag2.in_template = handoverOutputs.isA;
            tcag2.in_property = "receiver";
            connections.add(tcag2);

            TemplateConnection tcag2b = new TemplateConnection();
            tcag2b.out_id = agent_initOutputs1.ID;
            tcag2b.out_template = agent_initOutputs1.isA;
            tcag2b.out_property = "agent0";
            tcag2b.in_id = transportingOutputs.ID;
            tcag2b.in_template = transportingOutputs.isA;
            tcag2b.in_property = "transporter";
            connections.add(tcag2b);

            TemplateConnection tcag2c = new TemplateConnection();
            tcag2c.out_id = agent_initOutputs1.ID;
            tcag2c.out_template = agent_initOutputs1.isA;
            tcag2c.out_property = "agent0";
            tcag2c.in_id = handoverOutputs2.ID;
            tcag2c.in_template = handoverOutputs2.isA;
            tcag2c.in_property = "giver";
            connections.add(tcag2c);
        }

        TemplateConnection tc1=new TemplateConnection();
        tc1.out_id= handoverOutputs.ID;
        tc1.out_template= handoverOutputs.isA;
        tc1.out_property="item1";
        tc1.in_id= transportingOutputs.ID;
        tc1.in_template= transportingOutputs.isA;
        tc1.in_property="item0";
        connections.add(tc1);

        TemplateConnection tc2=new TemplateConnection();
        tc2.out_id= transportingOutputs.ID;
        tc2.out_template= transportingOutputs.isA;
        tc2.out_property="item1";
        tc2.in_id= handoverOutputs2.ID;
        tc2.in_template= handoverOutputs2.isA;
        tc2.in_property="item0";
        connections.add(tc2);


        if (withAgent) {
            TemplateConnection tcag3 = new TemplateConnection();
            tcag3.out_id = agent_initOutputs2.ID;
            tcag3.out_template = agent_initOutputs2.isA;
            tcag3.out_property = "agent0";
            tcag3.in_id = handoverOutputs2.ID;
            tcag3.in_template = handoverOutputs2.isA;
            tcag3.in_property = "receiver";
            connections.add(tcag3);

            TemplateConnection tcag3b = new TemplateConnection();
            tcag3b.out_id = agent_initOutputs2.ID;
            tcag3b.out_template = agent_initOutputs2.isA;
            tcag3b.out_property = "agent0";
            tcag3b.in_id = weighingOutputs2.ID;
            tcag3b.in_template = weighingOutputs2.isA;
            tcag3b.in_property = "agent";
            connections.add(tcag3b);

            TemplateConnection tcag3c = new TemplateConnection();
            tcag3c.out_id = agent_initOutputs2.ID;
            tcag3c.out_template = agent_initOutputs2.isA;
            tcag3c.out_property = "agent0";
            tcag3c.in_id = handoverOutputs3.ID;
            tcag3c.in_template = handoverOutputs3.isA;
            tcag3c.in_property = "giver";
            connections.add(tcag3c);


            TemplateConnection tcag4 = new TemplateConnection();
            tcag4.out_id = agent_initOutputs3.ID;
            tcag4.out_template = agent_initOutputs3.isA;
            tcag4.out_property = "agent0";
            tcag4.in_id = handoverOutputs3.ID;
            tcag4.in_template = handoverOutputs3.isA;
            tcag4.in_property = "receiver";
            connections.add(tcag4);

            TemplateConnection tcag4b = new TemplateConnection();
            tcag4b.out_id = agent_initOutputs3.ID;
            tcag4b.out_template = agent_initOutputs3.isA;
            tcag4b.out_property = "agent0";
            tcag4b.in_id = transportingOutputs2.ID;
            tcag4b.in_template = transportingOutputs2.isA;
            tcag4b.in_property = "transporter";
            connections.add(tcag4b);

            TemplateConnection tcag4c = new TemplateConnection();
            tcag4c.out_id = agent_initOutputs3.ID;
            tcag4c.out_template = agent_initOutputs3.isA;
            tcag4c.out_property = "agent0";
            tcag4c.in_id = handoverOutputs4.ID;
            tcag4c.in_template = handoverOutputs4.isA;
            tcag4c.in_property = "giver";
            connections.add(tcag4c);

        }


        TemplateConnection tc3=new TemplateConnection();
        tc3.out_id= handoverOutputs2.ID;
        tc3.out_template= handoverOutputs2.isA;
        tc3.out_property="item1";
        tc3.in_id= weighingOutputs2.ID;
        tc3.in_template= weighingOutputs2.isA;
        tc3.in_property="item0";
        connections.add(tc3);

        TemplateConnection tc3b=new TemplateConnection();
        tc3b.out_id= weighingOutputs2.ID;
        tc3b.out_template= weighingOutputs2.isA;
        tc3b.out_property="item1";
        tc3b.in_id= handoverOutputs3.ID;
        tc3b.in_template= handoverOutputs3.isA;
        tc3b.in_property="item0";
        connections.add(tc3b);


        TemplateConnection tc4=new TemplateConnection();
        tc4.out_id= handoverOutputs3.ID;
        tc4.out_template= handoverOutputs3.isA;
        tc4.out_property="item1";
        tc4.in_id= transportingOutputs2.ID;
        tc4.in_template= transportingOutputs2.isA;
        tc4.in_property="item0";
        connections.add(tc4);

        TemplateConnection tc5=new TemplateConnection();
        tc5.out_id= transportingOutputs2.ID;
        tc5.out_template= transportingOutputs2.isA;
        tc5.out_property="item1";
        tc5.in_id= handoverOutputs4.ID;
        tc5.in_template= handoverOutputs4.isA;
        tc5.in_property="item0";
        connections.add(tc5);

        TemplateConnection tc6=new TemplateConnection();
        tc6.out_id= handoverOutputs4.ID;
        tc6.out_template= handoverOutputs4.isA;
        tc6.out_property="item1";
        tc6.in_id= weighingOutputs3.ID;
        tc6.in_template= weighingOutputs3.isA;
        tc6.in_property="item0";
        connections.add(tc6);

        if (withAgent) {
            TemplateConnection tcag5 = new TemplateConnection();
            tcag5.out_id = agent_initOutputs4.ID;
            tcag5.out_template = agent_initOutputs4.isA;
            tcag5.out_property = "agent0";
            tcag5.in_id = handoverOutputs4.ID;
            tcag5.in_template = handoverOutputs4.isA;
            tcag5.in_property = "receiver";
            connections.add(tcag5);


            TemplateConnection tcag5b = new TemplateConnection();
            tcag5b.out_id = agent_initOutputs4.ID;
            tcag5b.out_template = agent_initOutputs4.isA;
            tcag5b.out_property = "agent0";
            tcag5b.in_id = weighingOutputs3.ID;
            tcag5b.in_template = weighingOutputs3.isA;
            tcag5b.in_property = "agent";
            connections.add(tcag5b);

            TemplateConnection tcsc1 = new TemplateConnection();
            tcsc1.out_id = agent_initOutputsS1.ID;
            tcsc1.out_template = agent_initOutputsS1.isA;
            tcsc1.out_property = "agent0";
            tcsc1.in_id = weighingOutputs1.ID;
            tcsc1.in_template = weighingOutputs1.isA;
            tcsc1.in_property = "scale";
            connections.add(tcsc1);


            TemplateConnection tcsc2 = new TemplateConnection();
            tcsc2.out_id = agent_initOutputsS2.ID;
            tcsc2.out_template = agent_initOutputsS2.isA;
            tcsc2.out_property = "agent0";
            tcsc2.in_id = weighingOutputs2.ID;
            tcsc2.in_template = weighingOutputs2.isA;
            tcsc2.in_property = "scale";
            connections.add(tcsc2);


            TemplateConnection tcsc3 = new TemplateConnection();
            tcsc3.out_id = agent_initOutputsS3.ID;
            tcsc3.out_template = agent_initOutputsS3.isA;
            tcsc3.out_property = "agent0";
            tcsc3.in_id = weighingOutputs3.ID;
            tcsc3.in_template = weighingOutputs3.isA;
            tcsc3.in_property = "scale";
            connections.add(tcsc3);

        }

        return connections;
    }

}