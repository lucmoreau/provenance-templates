package org.openprovenance.bookptm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openprovenance.book.fs.client.integrator.*;
import org.openprovenance.book.physical.client.integrator.Agent_initInputs;
import org.openprovenance.book.physical.client.integrator.Agent_initOutputs;
import org.openprovenance.templates.catalogue.transport.integrator.InputOutputProcessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class File2Workflow implements Workflow {

    public static int MARKER1=-1;
    public static int MARKER2=-2;
    public static int MARKER3=-3;
    public static int MARKER4=-4;

    final WorkflowUtils workflowUtils = new WorkflowUtils();

    enum MarkerMode { SAME_MARKER, DISTINCT_MARKERS }

    private final InputOutputProcessor templateInvoker;
    private final org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker;
    private final Function<String, List<List<Object>>> query;

    String agent1Time ="2024-09-01T10:00:00Z";


    String fileTime ="2024-09-14T10:00:00Z";


    final public MarkerMode outputMarkerMode;
    final public MarkerMode activityMarkerMode;



    public File2Workflow(InputOutputProcessor templateInvoker,
                         org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor fs_templateInvoker,
                         Function<String, List<List<Object>>> query,
                         MarkerMode outputMarkerMode,
                         MarkerMode activityMarkerMode) {
        this.templateInvoker=templateInvoker;
        this.fs_templateInvoker=fs_templateInvoker;
        this.query=query;
        this.outputMarkerMode = outputMarkerMode;
        this.activityMarkerMode = activityMarkerMode;
    }

    public List<TemplateConnection> connections=new LinkedList<>();
    public List<TemplateConnection> connectionsNoAgent;



    public List<Object> run()  {


        // new agent-init for box owner
        Agent_initInputs agent_initInputs0=new Agent_initInputs();
        agent_initInputs0.location="London";
        agent_initInputs0.type="Person";
        agent_initInputs0.time=agent1Time;
        Agent_initOutputs agent_initOutputs0=templateInvoker.process(agent_initInputs0);


        File_initInputs file_initInputs1=new File_initInputs();
        file_initInputs1.type="File";
        file_initInputs1.time= fileTime;
        File_initOutputs file_initOutputs1=fs_templateInvoker.process(file_initInputs1);


        File_initInputs file_initInputs2=new File_initInputs();
        file_initInputs1.type="File";
        file_initInputs1.time= fileTime;
        File_initOutputs file_initOutputs2=fs_templateInvoker.process(file_initInputs2);




        File_transformingInputs_1 fileTransformingInputs3=new File_transformingInputs_1();
        workflowUtils.flowToFrom(fileTransformingInputs3, "file", file_initOutputs1, "entity0");
        workflowUtils.flowToFrom(fileTransformingInputs3, "engineer", agent_initOutputs0, "agent0");



        File_transformingInputs_1 fileTransformingInputs4=new File_transformingInputs_1();
        workflowUtils.flowToFrom(fileTransformingInputs4, "file", file_initOutputs2, "entity0");
        workflowUtils.flowToFrom(fileTransformingInputs4, "engineer", agent_initOutputs0, "agent0");
        configureMarkers(fileTransformingInputs3, fileTransformingInputs4);


        File_transforming_compositeInputs fileTransformingCompositeInputs=new File_transforming_compositeInputs();
        fileTransformingCompositeInputs.__addElements(fileTransformingInputs3);
        fileTransformingCompositeInputs.__addElements(fileTransformingInputs4);
        fileTransformingCompositeInputs.count=2;

        File_transforming_compositeOutputs fileTransformingCompositeOutputs=fs_templateInvoker.process(fileTransformingCompositeInputs);
        connections.addAll(workflowUtils.generateConnections(fileTransformingCompositeOutputs.__elements.get(0), 2));
        connections.addAll(workflowUtils.generateConnections(fileTransformingCompositeOutputs.__elements.get(1)));



        connectionsNoAgent= workflowUtils.filterOutAgentInit(connections);

        // return all inputs and outputs

        return Arrays.asList(
                file_initInputs1, file_initOutputs1,
                file_initInputs2, file_initOutputs2,
                agent_initInputs0, agent_initOutputs0,
                fileTransformingCompositeInputs, fileTransformingCompositeOutputs,
                connections,
                connectionsNoAgent);

    }


    private void configureMarkers(File_transformingInputs_1 fileTransformingInputs3, File_transformingInputs_1 fileTransformingInputs4) {

        switch (outputMarkerMode) {
            case SAME_MARKER:
                fileTransformingInputs3.transformed_file=MARKER1;
                fileTransformingInputs4.transformed_file=MARKER1;
                break;
            case DISTINCT_MARKERS:
                fileTransformingInputs3.transformed_file=MARKER1;
                fileTransformingInputs4.transformed_file=MARKER2;
                break;
        }

        switch (activityMarkerMode) {
            case SAME_MARKER:
                fileTransformingInputs3.transforming=MARKER3;
                fileTransformingInputs4.transforming=MARKER3;
                break;
            case DISTINCT_MARKERS:
                fileTransformingInputs3.transforming=MARKER3;
                fileTransformingInputs4.transforming=MARKER4;
                break;
        }


    }

    @Override
    public List<TemplateConnection> getConnections() {
        return connections;
    }

    @Override
    public List<TemplateConnection> getConnectionsNoAgent() {
        return connectionsNoAgent;
    }




}