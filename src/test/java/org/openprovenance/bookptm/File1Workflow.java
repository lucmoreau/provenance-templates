package org.openprovenance.bookptm;


import org.openprovenance.book.fs.client.integrator.*;
import org.openprovenance.book.physical.client.integrator.*;

import org.openprovenance.templates.catalogue.transport.integrator.InputOutputProcessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class File1Workflow implements Workflow {


    final WorkflowUtils workflowUtils = new WorkflowUtils();

    private final InputOutputProcessor templateInvoker;
    private final org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker;
    private final Function<String, List<List<Object>>> query;

    String agent1Time ="2024-09-01T10:00:00Z";
    String fileTime ="2024-09-14T10:00:00Z";



    public File1Workflow(InputOutputProcessor templateInvoker, org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker, Function<String, List<List<Object>>> query) {
        this.templateInvoker=templateInvoker;
        this.fs_templateInvoker=fs_templateInvoker;
        this.query=query;
    }

    private List<TemplateConnection> connections=new LinkedList<>();
    private List<TemplateConnection> connectionsNoAgent;

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
        workflowUtils.flowToFrom(fileTransformingInputs, "file", file_initOutputs, "entity0");
        workflowUtils.flowToFrom(fileTransformingInputs, "engineer", agent_initOutputs0, "agent0");


        File_transformingOutputs fileTransformingOutputs=fs_templateInvoker.process(fileTransformingInputs);
        connections.addAll(workflowUtils.generateConnections(fileTransformingOutputs));


        connectionsNoAgent=workflowUtils.filterOutAgentInit(connections);

        // return all inputs and outputs

        return Arrays.asList(
                file_initInputs, file_initOutputs,
                fileTransformingInputs, fileTransformingOutputs,
                agent_initInputs0, agent_initOutputs0,
                connections,
                connectionsNoAgent);

    }

    public List<TemplateConnection> getConnections() {
        return connections;
    }

    public List<TemplateConnection> getConnectionsNoAgent() {
        return connectionsNoAgent;
    }
}