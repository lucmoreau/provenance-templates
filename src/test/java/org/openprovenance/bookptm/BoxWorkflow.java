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
    Integer agent0=100; // box owner
    Integer agent1=200; // transporter1
    Integer agent2=300; // depot manager
    Integer agent3=400; // transporter2
    Integer agent4=500; // recipient


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

    List<TemplateConnection> connections= new LinkedList<>();

    public List<Object> run() {

        WeighingInputs weighingInputs1=new WeighingInputs();
        weighingInputs1.item0=item0;
        weighingInputs1.item=item;
        weighingInputs1.agent=agent0;
        weighingInputs1.weight=10.0f;
        weighingInputs1.time=weighing1Time;
        WeighingOutputs weighingOutputs1=templateInvoker.process(weighingInputs1);


        HandoverInputs handoverInputs=new HandoverInputs();
        handoverInputs.item0=weighingOutputs1.item1;
        handoverInputs.item=item;
        handoverInputs.agent1=agent1;
        handoverInputs.agent0=agent0;
        handoverInputs.time=pickupTime;
        HandoverOutputs handoverOutputs=templateInvoker.process(handoverInputs);


        TransportingInputs transportingInputs=new TransportingInputs();
        transportingInputs.item0=handoverOutputs.item1;
        transportingInputs.item=item;
        transportingInputs.transporter=agent1;
        transportingInputs.time=drop1Time;
        TransportingOutputs transportingOutputs=templateInvoker.process(transportingInputs);

        HandoverInputs handoverInputs2=new HandoverInputs();
        handoverInputs2.item0=transportingOutputs.item1;
        handoverInputs2.item=item;
        handoverInputs2.agent1=agent2;
        handoverInputs2.agent0=agent1;
        handoverInputs2.time= handoverTime;
        HandoverOutputs handoverOutputs2=templateInvoker.process(handoverInputs2);

        WeighingInputs weighingInputs2=new WeighingInputs();
        weighingInputs2.item0=handoverOutputs2.item1;
        weighingInputs2.item=item;
        weighingInputs2.agent=agent2;
        weighingInputs2.weight=10.0f;
        weighingInputs2.time=weighing2Time;
        WeighingOutputs weighingOutputs2=templateInvoker.process(weighingInputs2);

        HandoverInputs handoverInputs3=new HandoverInputs();
        handoverInputs3.item0=weighingOutputs2.item1;
        handoverInputs3.item=item;
        handoverInputs3.agent1=agent3;
        handoverInputs3.agent0=agent2;
        handoverInputs3.time=handoverTime2;
        HandoverOutputs handoverOutputs3=templateInvoker.process(handoverInputs3);


        TransportingInputs transportingInputs2=new TransportingInputs();
        transportingInputs2.item0=handoverOutputs3.item1;
        transportingInputs2.item=item;
        transportingInputs2.transporter=agent3;
        transportingInputs2.time=deliveryTime;
        TransportingOutputs transportingOutputs2=templateInvoker.process(transportingInputs2);
        // at this point, the item is with the transporter





        HandoverInputs handoverInputs4=new HandoverInputs();
        handoverInputs4.item0=transportingOutputs2.item1;
        handoverInputs4.item=item;
        handoverInputs4.agent1= agent4;
        handoverInputs4.agent0=agent3;
        handoverInputs4.time=deliveryTime;
        HandoverOutputs handoverOutputs4=templateInvoker.process(handoverInputs4);
        // at this point, the item is with the recipient

        // recipient weighing item
        WeighingInputs weighingInputs3=new WeighingInputs();
        weighingInputs3.item0=handoverOutputs4.item1;
        weighingInputs3.item=item;
        weighingInputs3.agent=agent4;
        weighingInputs3.weight=15.0f;
        weighingInputs3.time=deliveryTime;
        WeighingOutputs weighingOutputs3=templateInvoker.process(weighingInputs3);
        // item should weigh 10.0, so this is a discrepancy


        //
        TemplateConnection tc0=new TemplateConnection();
        tc0.out_id=weighingOutputs1.ID;
        tc0.out_template=weighingOutputs1.isA;
        tc0.out_property="item1";
        tc0.in_id=handoverOutputs.ID;
        tc0.in_template=handoverOutputs.isA;
        tc0.in_property="item0";
        connections.add(tc0);


        TemplateConnection tc1=new TemplateConnection();
        tc1.out_id=handoverOutputs.ID;
        tc1.out_template=handoverOutputs.isA;
        tc1.out_property="item1";
        tc1.in_id=transportingOutputs.ID;
        tc1.in_template=transportingOutputs.isA;
        tc1.in_property="item0";
        connections.add(tc1);

        TemplateConnection tc2=new TemplateConnection();
        tc2.out_id=transportingOutputs.ID;
        tc2.out_template=transportingOutputs.isA;
        tc2.out_property="item1";
        tc2.in_id=handoverOutputs2.ID;
        tc2.in_template=handoverOutputs2.isA;
        tc2.in_property="item0";
        connections.add(tc2);


        TemplateConnection tc3=new TemplateConnection();
        tc3.out_id=handoverOutputs2.ID;
        tc3.out_template=handoverOutputs2.isA;
        tc3.out_property="item1";
        tc3.in_id=weighingOutputs2.ID;
        tc3.in_template=weighingOutputs2.isA;
        tc3.in_property="item0";
        connections.add(tc3);

        TemplateConnection tc3b=new TemplateConnection();
        tc3b.out_id=weighingOutputs2.ID;
        tc3b.out_template=weighingOutputs2.isA;
        tc3b.out_property="item1";
        tc3b.in_id=handoverOutputs3.ID;
        tc3b.in_template=handoverOutputs3.isA;
        tc3b.in_property="item0";
        connections.add(tc3b);


        TemplateConnection tc4=new TemplateConnection();
        tc4.out_id=handoverOutputs3.ID;
        tc4.out_template=handoverOutputs3.isA;
        tc4.out_property="item1";
        tc4.in_id=transportingOutputs2.ID;
        tc4.in_template=transportingOutputs2.isA;
        tc4.in_property="item0";
        connections.add(tc4);

        TemplateConnection tc5=new TemplateConnection();
        tc5.out_id=transportingOutputs2.ID;
        tc5.out_template=transportingOutputs2.isA;
        tc5.out_property="item1";
        tc5.in_id=handoverOutputs4.ID;
        tc5.in_template=handoverOutputs4.isA;
        tc5.in_property="item0";
        connections.add(tc5);

        TemplateConnection tc6=new TemplateConnection();
        tc6.out_id=handoverOutputs4.ID;
        tc6.out_template=handoverOutputs4.isA;
        tc6.out_property="item1";
        tc6.in_id=weighingOutputs3.ID;
        tc6.in_template=weighingOutputs3.isA;
        tc6.in_property="item0";
        connections.add(tc6);



        // return all inputs and outputs

        return Arrays.asList(handoverInputs, handoverOutputs,
                transportingInputs, transportingOutputs,
                handoverInputs2, handoverOutputs2,
                handoverInputs3, handoverOutputs3,
                transportingInputs2, transportingOutputs2,
                handoverInputs4, handoverOutputs4,
                weighingInputs3, weighingOutputs3,
                connections);

    }

}