package org.openprovenance.bookptm;


import org.openprovenance.book.fs.client.integrator.File_initInputs;
import org.openprovenance.book.fs.client.integrator.File_initOutputs;
import org.openprovenance.book.fs.client.integrator.File_transformingInputs;
import org.openprovenance.book.fs.client.integrator.File_transformingOutputs;
import org.openprovenance.book.physical.client.integrator.*;

import org.openprovenance.templates.catalogue.transport.integrator.InputOutputProcessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileWorkflow {

    public static int MARKER1=-1;
    public static int MARKER2=-2;


    private final InputOutputProcessor templateInvoker;
    private final org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker;
    private final Function<String, List<List<Object>>> query;

    String agent1Time ="2024-09-01T10:00:00Z";


    String fileTime ="2024-09-14T10:00:00Z";



    public FileWorkflow(InputOutputProcessor templateInvoker, org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker, Function<String, List<List<Object>>> query) {
        this.templateInvoker=templateInvoker;
        this.fs_templateInvoker=fs_templateInvoker;
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


        File_initInputs file_initInputs=new File_initInputs();
        file_initInputs.type="File";
        file_initInputs.time= fileTime;
        File_initOutputs file_initOutputs=fs_templateInvoker.process(file_initInputs);



        File_transformingInputs fileTransformingInputs=new File_transformingInputs();
        flowToFrom(fileTransformingInputs, "file", file_initOutputs, "entity0");
        flowToFrom(fileTransformingInputs, "engineer", agent_initOutputs0, "agent0");


        File_transformingOutputs fileTransformingOutputs=fs_templateInvoker.process(fileTransformingInputs);
        generateConnections(fileTransformingOutputs);





        connectionsNoAgent=filterOutAgentInit(connections);

        // return all inputs and outputs

        return Arrays.asList(
                file_initInputs, file_initOutputs,
                fileTransformingInputs, fileTransformingOutputs,
                agent_initInputs0, agent_initOutputs0,
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