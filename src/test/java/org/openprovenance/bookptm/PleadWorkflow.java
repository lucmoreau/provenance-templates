package org.openprovenance.bookptm;

import org.openprovenance.book.fs.client.integrator.*;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor;

import java.util.List;

public class PleadWorkflow {
    private final List<Object> inputs;
    private final List<Object> outputs;
    Integer organization = 111;
    Integer engineer=222;
    Integer manager=333;

    private final InputOutputProcessor templateInstantiation;

    private final ProvFactory pf = ProvFactory.getFactory();

    public PleadWorkflow(InputOutputProcessor templateInstantiation, List<Object> inputs, List<Object> outputs) {
        this.templateInstantiation = templateInstantiation;
        this.inputs = inputs;
        this.outputs = outputs;
    }



    public void workflow(String filenameRoot, Integer oldFileId, Integer tmethod, Integer fmethod, Integer n_rows, Integer n_cols, String path, String start, String end) {

        File_transformingInputs transformingInputs = new File_transformingInputs();
        transformingInputs.filename = filenameRoot + "-transformed.csv";
        transformingInputs.file = oldFileId;
        transformingInputs.method = tmethod;
        transformingInputs.engineer = engineer;
        transformingInputs.path = path;
        transformingInputs.time = pf.newTimeNow().toString();
        transformingInputs.start = start;
        transformingInputs.end = end;

        if (inputs!=null) inputs.add(transformingInputs);
        File_transformingOutputs transformingOutputs = templateInstantiation.process(transformingInputs);
        if (outputs!=null) outputs.add(transformingOutputs);

        File_filteringInputs filteringInputs = new File_filteringInputs();
        filteringInputs.filename = filenameRoot + "-filtered.csv";
        filteringInputs.file = transformingOutputs.transformed_file;
        filteringInputs.method = fmethod;
        filteringInputs.engineer = engineer;
        filteringInputs.n_rows = n_rows;
        filteringInputs.n_cols = n_cols;
        filteringInputs.path = path;
        filteringInputs.time = pf.newTimeNow().toString();
        filteringInputs.start = start;
        filteringInputs.end = end;

        if (inputs!=null) inputs.add(filteringInputs);
        File_filteringOutputs filteringOutputs = templateInstantiation.process(filteringInputs);
        if (outputs!=null) outputs.add(filteringOutputs);

        File_splittingInputs splittingInputs = new File_splittingInputs();
        splittingInputs.filename1 = filenameRoot + "-training.csv";
        splittingInputs.filename2 = filenameRoot + "-validation.csv";
        splittingInputs.file = filteringOutputs.filtered_file;
        splittingInputs.engineer = engineer;
        splittingInputs.path1 = path;
        splittingInputs.time = pf.newTimeNow().toString();

        if (inputs!=null) inputs.add(splittingInputs);
        File_splittingOutputs splittingOutputs = templateInstantiation.process(splittingInputs);
        if (outputs!=null) outputs.add(splittingOutputs);

        File_trainingInputs trainingInputs = new File_trainingInputs();
        trainingInputs.filename = filenameRoot + ".pipeline";
        trainingInputs.training_dataset = splittingOutputs.split_file1;
        trainingInputs.engineer = engineer;
        trainingInputs.path = path;
        trainingInputs.time = pf.newTimeNow().toString();

        if (inputs!=null) inputs.add(trainingInputs);
        File_trainingOutputs trainingOutputs = templateInstantiation.process(trainingInputs);
        if (outputs!=null) outputs.add(trainingOutputs);

        File_validatingInputs validatingInputs = new File_validatingInputs();
        //random value between 0 and 1
        validatingInputs.score_value = Math.random();
        validatingInputs.testing_dataset = splittingOutputs.split_file2;
        validatingInputs.engineer = engineer;
        validatingInputs.path = path;
        validatingInputs.time = pf.newTimeNow().toString();

        if (inputs!=null) inputs.add(validatingInputs);
        File_validatingOutputs validatingOutputs = templateInstantiation.process(validatingInputs);
        if (outputs!=null) outputs.add(validatingOutputs);

        File_approvingInputs approvingInputs = new File_approvingInputs();
        approvingInputs.pipeline = trainingOutputs.pipeline;
        approvingInputs.filename = filenameRoot + ".approved-pipeline";
        approvingInputs.score = validatingOutputs.score;
        approvingInputs.signature="signature";
        approvingInputs.manager = manager;
        approvingInputs.path = path;
        approvingInputs.time = pf.newTimeNow().toString();

        if (inputs!=null) inputs.add(approvingInputs);
        File_approvingOutputs approvingOutputs = templateInstantiation.process(approvingInputs);
        if (outputs!=null) outputs.add(approvingOutputs);

    }
}
