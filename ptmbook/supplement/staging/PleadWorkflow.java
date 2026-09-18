// Generated automatically by ProvToolbox for template configuration 'fs_template_library'
// by class org.openprovenance.bookptm.workflows.GeneratePleadWorkflow, method generateAndCompilePast,
// in file GeneratePleadWorkflow.java, at line 496
package org.openprovenance.book.workflows;

import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.List;
import org.openprovenance.book.fs.client.integrator.File_approvingInputs;
import org.openprovenance.book.fs.client.integrator.File_approvingOutputs;
import org.openprovenance.book.fs.client.integrator.File_filteringInputs;
import org.openprovenance.book.fs.client.integrator.File_filteringOutputs;
import org.openprovenance.book.fs.client.integrator.File_splittingInputs;
import org.openprovenance.book.fs.client.integrator.File_splittingOutputs;
import org.openprovenance.book.fs.client.integrator.File_trainingInputs;
import org.openprovenance.book.fs.client.integrator.File_trainingOutputs;
import org.openprovenance.book.fs.client.integrator.File_transformingInputs;
import org.openprovenance.book.fs.client.integrator.File_transformingOutputs;
import org.openprovenance.book.fs.client.integrator.File_validatingInputs;
import org.openprovenance.book.fs.client.integrator.File_validatingOutputs;
import org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor;

public abstract class PleadWorkflow {
  private final List<Object> inputs;

  private final List<Object> outputs;

  private final InputOutputProcessor templateInstantiation;

  public PleadWorkflow(InputOutputProcessor templateInstantiation, List<Object> inputs,
      List<Object> outputs) {
    this.templateInstantiation=templateInstantiation;
    this.inputs=inputs;
    this.outputs=outputs;
  }

  public void workflow(Integer engineer, Integer manager, String filenameRoot, Integer oldFileId,
      Integer tmethod, Integer fmethod, Integer n_rows, Integer n_cols, String path, String start,
      String end) {
    // ;
    // Transforming;
    // ;
    File_transformingInputs transformingInputs=new File_transformingInputs();
    transformingInputs.filename=(filenameRoot + "-transformed.csv");
    transformingInputs.file=oldFileId;
    transformingInputs.method=tmethod;
    transformingInputs.engineer=engineer;
    transformingInputs.path=path;
    transformingInputs.time=this.time();
    transformingInputs.start=start;
    transformingInputs.end=end;
    if ((this.inputs != null)) {
      this.inputs.add(transformingInputs);
    }
    File_transformingOutputs transformingOutputs=this.templateInstantiation.process(transformingInputs);
    if ((this.outputs != null)) {
      this.outputs.add(transformingOutputs);
    }
    // ;
    // Filtering;
    // ;
    File_filteringInputs filteringInputs=new File_filteringInputs();
    filteringInputs.filename=(filenameRoot + "-filtered.csv");
    filteringInputs.file=transformingOutputs.transformed_file;
    filteringInputs.method=fmethod;
    filteringInputs.engineer=engineer;
    filteringInputs.n_rows=n_rows;
    filteringInputs.n_cols=n_cols;
    filteringInputs.path=path;
    filteringInputs.time=this.time();
    filteringInputs.start=start;
    filteringInputs.end=end;
    if ((this.inputs != null)) {
      this.inputs.add(filteringInputs);
    }
    File_filteringOutputs filteringOutputs=this.templateInstantiation.process(filteringInputs);
    if ((this.outputs != null)) {
      this.outputs.add(filteringOutputs);
    }
    // ;
    // Splitting;
    // ;
    File_splittingInputs splittingInputs=new File_splittingInputs();
    splittingInputs.filename1=(filenameRoot + "-training.csv");
    splittingInputs.filename2=(filenameRoot + "-validation.csv");
    splittingInputs.file=filteringOutputs.filtered_file;
    splittingInputs.engineer=engineer;
    splittingInputs.path1=path;
    splittingInputs.time=this.time();
    if ((this.inputs != null)) {
      this.inputs.add(splittingInputs);
    }
    File_splittingOutputs splittingOutputs=this.templateInstantiation.process(splittingInputs);
    if ((this.outputs != null)) {
      this.outputs.add(splittingOutputs);
    }
    // ;
    // Training;
    // ;
    File_trainingInputs trainingInputs=new File_trainingInputs();
    trainingInputs.filename=(filenameRoot + ".pipeline");
    trainingInputs.training_dataset=splittingOutputs.split_file1;
    trainingInputs.engineer=engineer;
    trainingInputs.path=path;
    trainingInputs.time=this.time();
    if ((this.inputs != null)) {
      this.inputs.add(trainingInputs);
    }
    File_trainingOutputs trainingOutputs=this.templateInstantiation.process(trainingInputs);
    if ((this.outputs != null)) {
      this.outputs.add(trainingOutputs);
    }
    // ;
    // Validating;
    // ;
    File_validatingInputs validatingInputs=new File_validatingInputs();
    validatingInputs.score_value=0.123d;
    validatingInputs.testing_dataset=splittingOutputs.split_file2;
    validatingInputs.engineer=engineer;
    validatingInputs.path=path;
    validatingInputs.time=this.time();
    if ((this.inputs != null)) {
      this.inputs.add(validatingInputs);
    }
    File_validatingOutputs validatingOutputs=this.templateInstantiation.process(validatingInputs);
    if ((this.outputs != null)) {
      this.outputs.add(validatingOutputs);
    }
    // ;
    // Approving;
    // ;
    File_approvingInputs approvingInputs=new File_approvingInputs();
    approvingInputs.pipeline=trainingOutputs.pipeline;
    approvingInputs.filename=(filenameRoot + ".approved-pipeline");
    approvingInputs.score=validatingOutputs.score;
    approvingInputs.signature="signature";
    approvingInputs.manager=manager;
    approvingInputs.path=path;
    approvingInputs.time=this.time();
    if ((this.inputs != null)) {
      this.inputs.add(approvingInputs);
    }
    File_approvingOutputs approvingOutputs=this.templateInstantiation.process(approvingInputs);
    if ((this.outputs != null)) {
      this.outputs.add(approvingOutputs);
    }
  }

  public abstract String time();
}
