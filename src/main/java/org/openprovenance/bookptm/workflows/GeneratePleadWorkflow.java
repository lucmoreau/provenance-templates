package org.openprovenance.bookptm.workflows;


import org.openprovenance.prov.template.compiler.CompilerUtil;
import org.openprovenance.prov.template.compiler.configuration.SpecificationFile;
import org.openprovenance.prov.template.compiler.configuration.TemplatesProjectConfiguration;
import org.openprovenance.prov.template.compiler.past.*;
import org.openprovenance.prov.template.compiler.past.Class;
import org.openprovenance.prov.template.compiler.past.type.ClassName;
import org.openprovenance.prov.template.compiler.past.type.ParameterizedType;
import org.openprovenance.prov.vanilla.ProvFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static org.openprovenance.prov.template.compiler.configuration.SpecificationFile.generateJava;
import static org.openprovenance.prov.template.compiler.configuration.SpecificationFile.generatePython;
import static org.openprovenance.prov.template.compiler.past.Assignment.ASSIGNMENT;
import static org.openprovenance.prov.template.compiler.past.BinaryOp.BINARY_OP;
import static org.openprovenance.prov.template.compiler.past.Constant.CONSTANT;
import static org.openprovenance.prov.template.compiler.past.Constant.getNull;
import static org.openprovenance.prov.template.compiler.past.Constructor.CONSTRUCTOR;
import static org.openprovenance.prov.template.compiler.past.Definition.DEFINITION;
import static org.openprovenance.prov.template.compiler.past.Field.FIELD;
import static org.openprovenance.prov.template.compiler.past.IfStatement.IF;
import static org.openprovenance.prov.template.compiler.past.Method.METHOD;
import static org.openprovenance.prov.template.compiler.past.MethodCall.CONSTRUCTOR_CALL;
import static org.openprovenance.prov.template.compiler.past.MethodCall.METHOD_CALL;
import static org.openprovenance.prov.template.compiler.past.Variable.VARIABLE;
import static org.openprovenance.prov.template.compiler.past.type.ClassName.*;

/**
 * Generates a PAST expression tree encoding the behaviour of the Plead Workflow.
 *
 * <p>The entry point is {@link #generatePleadWorkflow()}, which returns a {@link Class}
 * PAST node whose structure mirrors PleadWorkflow exactly.  The node can then be emitted
 * to Java (or another target language) via the PAST emitter infrastructure.</p>
 */
public class GeneratePleadWorkflow {

    // -----------------------------------------------------------------------
    // Type constants
    // -----------------------------------------------------------------------

    private static final String INTEGRATOR_PACKAGE =
            "org.openprovenance.book.fs.client.integrator";

    private static final String PROCESSOR_PACKAGE =
            "org.openprovenance.templates.catalogue.fs.integrator";

    /** {@code InputOutputProcessor} – the template-instantiation service. */
    private static final ClassName INPUT_OUTPUT_PROCESSOR =
            ClassName.get("InputOutputProcessor", PROCESSOR_PACKAGE);

    /** {@code java.lang.Math} – needed for {@code Math.random()}. */
    private static final ClassName MATH =
            ClassName.get("Math", "java.lang");

    // Bean types (generated integrator beans)
    private static final ClassName FILE_TRANSFORMING_INPUTS  =
            ClassName.get("File_transformingInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_TRANSFORMING_OUTPUTS =
            ClassName.get("File_transformingOutputs", INTEGRATOR_PACKAGE);

    private static final ClassName FILE_FILTERING_INPUTS  =
            ClassName.get("File_filteringInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_FILTERING_OUTPUTS =
            ClassName.get("File_filteringOutputs", INTEGRATOR_PACKAGE);

    private static final ClassName FILE_SPLITTING_INPUTS  =
            ClassName.get("File_splittingInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_SPLITTING_OUTPUTS =
            ClassName.get("File_splittingOutputs", INTEGRATOR_PACKAGE);

    private static final ClassName FILE_TRAINING_INPUTS  =
            ClassName.get("File_trainingInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_TRAINING_OUTPUTS =
            ClassName.get("File_trainingOutputs", INTEGRATOR_PACKAGE);

    private static final ClassName FILE_VALIDATING_INPUTS  =
            ClassName.get("File_validatingInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_VALIDATING_OUTPUTS =
            ClassName.get("File_validatingOutputs", INTEGRATOR_PACKAGE);

    private static final ClassName FILE_APPROVING_INPUTS  =
            ClassName.get("File_approvingInputs",  INTEGRATOR_PACKAGE);
    private static final ClassName FILE_APPROVING_OUTPUTS =
            ClassName.get("File_approvingOutputs", INTEGRATOR_PACKAGE);

    /** {@code List<Object>} – type of the {@code inputs} / {@code outputs} fields. */
    private static final ParameterizedType LIST_OF_OBJECT =
            ParameterizedType.get(LIST, OBJECT);

    /** Package of the class being generated. */
    private static final String GENERATED_PACKAGE = "org.openprovenance.book.workflows";
    private final CompilerUtil compilerUtil;
    private final ProvFactory pFactory;

    public GeneratePleadWorkflow() {
        this.pFactory= ProvFactory.getFactory();
        this.compilerUtil = new CompilerUtil(pFactory);
    }


    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Builds and returns a PAST {@link Class} node that encodes the full
     * behaviour of  PleadWorkflow: its fields, constructor, and
     * {@code workflow} method.
     */
    public Class generatePleadWorkflow() {

        Class pastClass = new PastFactory()
                .CLASS("PleadWorkflow")
                .MODIFIERS(Modifier.PUBLIC, Modifier.ABSTRACT);

        addFields(pastClass);
        addConstructor(pastClass);
        addWorkflowMethod(pastClass);

        pastClass.METHOD(
                METHOD("time")
                        .MODIFIERS(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .RETURNS(STRING) );

        return pastClass;
    }

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    private void addFields(Class pastClass) {
        pastClass.FIELDS(
                FIELD("inputs", LIST_OF_OBJECT)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL),

                FIELD("outputs", LIST_OF_OBJECT)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL),

                FIELD("templateInstantiation", INPUT_OUTPUT_PROCESSOR)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL)
        );
    }

    private void addConstructor(Class pastClass) {
        Constructor constructor = CONSTRUCTOR()
                .MODIFIERS(Modifier.PUBLIC)
                .PARAMETER(INPUT_OUTPUT_PROCESSOR, "templateInstantiation")
                .PARAMETER(LIST_OF_OBJECT, "inputs")
                .PARAMETER(LIST_OF_OBJECT, "outputs")
                .BODY(
                        ASSIGNMENT(METHOD_CALL(VARIABLE("this"), "templateInstantiation"),
                                VARIABLE("templateInstantiation")),
                        ASSIGNMENT(
                                METHOD_CALL(VARIABLE("this"), "inputs"),
                                VARIABLE("inputs")),
                        ASSIGNMENT(
                                METHOD_CALL(VARIABLE("this"), "outputs"),
                                VARIABLE("outputs"))
                );
        pastClass.CONSTRUCTOR(constructor);
    }

    private void addWorkflowMethod(Class pastClass) {
        Method workflowMethod = METHOD("workflow")
                .MODIFIERS(Modifier.PUBLIC)
                .RETURNS(VOID)
                .PARAMETER(INTEGER, "engineer")
                .PARAMETER(INTEGER, "manager")
                .PARAMETER(STRING,  "filenameRoot")
                .PARAMETER(INTEGER, "oldFileId")
                .PARAMETER(INTEGER, "tmethod")
                .PARAMETER(INTEGER, "fmethod")
                .PARAMETER(INTEGER, "n_rows")
                .PARAMETER(INTEGER, "n_cols")
                .PARAMETER(STRING,  "path")
                .PARAMETER(STRING,  "start")
                .PARAMETER(STRING,  "end");
        addTransformingBlock(workflowMethod);
        addFilteringBlock(workflowMethod);
        addSplittingBlock(workflowMethod);
        addTrainingBlock(workflowMethod);
        addValidatingBlock(workflowMethod);
        addApprovingBlock(workflowMethod);

        pastClass.METHOD(workflowMethod);
    }


    // ---- transforming -------------------------------------------------------

    private void addTransformingBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_TRANSFORMING_INPUTS, VARIABLE("transformingInputs"),
                        CONSTRUCTOR_CALL(FILE_TRANSFORMING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "filename"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT("-transformed.csv"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "file"),
                        VARIABLE("oldFileId")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "method"),
                        VARIABLE("tmethod")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "engineer"),
                        VARIABLE("engineer")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "path"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "time"),
                        time()),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "start"),
                        VARIABLE("start")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transformingInputs"), "end"),
                        VARIABLE("end")),

                nullGuardedAdd("inputs", "transformingInputs"),

                process(FILE_TRANSFORMING_OUTPUTS, "transformingOutputs", "transformingInputs"),

                nullGuardedAdd("outputs", "transformingOutputs"));
    }

    // ---- filtering ----------------------------------------------------------

    private void addFilteringBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_FILTERING_INPUTS, VARIABLE("filteringInputs"),
                        CONSTRUCTOR_CALL(FILE_FILTERING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "filename"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT("-filtered.csv"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "file"),
                        METHOD_CALL(VARIABLE("transformingOutputs"), "transformed_file")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "method"),
                        VARIABLE("fmethod")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "engineer"),
                        VARIABLE("engineer")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "n_rows"),
                        VARIABLE("n_rows")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "n_cols"),
                        VARIABLE("n_cols")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "path"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "time"),
                        time()),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "start"),
                        VARIABLE("start")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("filteringInputs"), "end"),
                        VARIABLE("end")),

                nullGuardedAdd("inputs", "filteringInputs"),

                process(FILE_FILTERING_OUTPUTS, "filteringOutputs", "filteringInputs"),

                nullGuardedAdd("outputs", "filteringOutputs"));
    }

    // ---- splitting ----------------------------------------------------------

    private void addSplittingBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_SPLITTING_INPUTS, VARIABLE("splittingInputs"),
                        CONSTRUCTOR_CALL(FILE_SPLITTING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "filename1"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT("-training.csv"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "filename2"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT("-validation.csv"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "file"),
                        METHOD_CALL(VARIABLE("filteringOutputs"), "filtered_file")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "engineer"),
                        VARIABLE("engineer")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "path1"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("splittingInputs"), "time"),
                        time()),

                nullGuardedAdd("inputs", "splittingInputs"),

                process(FILE_SPLITTING_OUTPUTS, "splittingOutputs", "splittingInputs"),

                nullGuardedAdd("outputs", "splittingOutputs"));
    }

    // ---- training -----------------------------------------------------------

    private void addTrainingBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_TRAINING_INPUTS, VARIABLE("trainingInputs"),
                        CONSTRUCTOR_CALL(FILE_TRAINING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("trainingInputs"), "filename"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT(".pipeline"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("trainingInputs"), "training_dataset"),
                        METHOD_CALL(VARIABLE("splittingOutputs"), "split_file1")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("trainingInputs"), "engineer"),
                        VARIABLE("engineer")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("trainingInputs"), "path"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("trainingInputs"), "time"),
                        time()),

                nullGuardedAdd("inputs", "trainingInputs"),

                process(FILE_TRAINING_OUTPUTS, "trainingOutputs", "trainingInputs"),

                nullGuardedAdd("outputs", "trainingOutputs"));
    }

    // ---- validating ---------------------------------------------------------

    private void addValidatingBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_VALIDATING_INPUTS, VARIABLE("validatingInputs"),
                        CONSTRUCTOR_CALL(FILE_VALIDATING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("validatingInputs"), "score_value"),
                        METHOD_CALL(MATH, "random", List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("validatingInputs"), "testing_dataset"),
                        METHOD_CALL(VARIABLE("splittingOutputs"), "split_file2")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("validatingInputs"), "engineer"),
                        VARIABLE("engineer")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("validatingInputs"), "path"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("validatingInputs"), "time"),
                        time()),

                nullGuardedAdd("inputs", "validatingInputs"),

                process(FILE_VALIDATING_OUTPUTS, "validatingOutputs", "validatingInputs"),

                nullGuardedAdd("outputs", "validatingOutputs"));
    }

    // ---- approving ----------------------------------------------------------

    private void addApprovingBlock(Method method) {
        method.BODY(
                DEFINITION(FILE_APPROVING_INPUTS, VARIABLE("approvingInputs"),
                        CONSTRUCTOR_CALL(FILE_APPROVING_INPUTS, List.of())),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "pipeline"),
                        METHOD_CALL(VARIABLE("trainingOutputs"), "pipeline")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "filename"),
                        BINARY_OP(VARIABLE("filenameRoot"), "+", CONSTANT(".approved-pipeline"))),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "score"),
                        METHOD_CALL(VARIABLE("validatingOutputs"), "score")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "signature"),
                        CONSTANT("signature")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "manager"),
                        VARIABLE("manager")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "path"),
                        VARIABLE("path")),

                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("approvingInputs"), "time"),
                        time()),

                nullGuardedAdd("inputs", "approvingInputs"),

                process(FILE_APPROVING_OUTPUTS, "approvingOutputs", "approvingInputs"),

                nullGuardedAdd("outputs", "approvingOutputs"));
    }

    // -----------------------------------------------------------------------
    // Helper builders
    // -----------------------------------------------------------------------

    private Expression time() {
        return METHOD_CALL(VARIABLE("this"), "time", List.of());
    }

    /**
     * Builds {@code if (listVar != null) listVar.add(elementVar);}.
     *
     * @param listVar    name of the {@code List<Object>} field ({@code "inputs"} or {@code "outputs"})
     * @param elementVar name of the bean local variable to add
     */
    private IfStatement nullGuardedAdd(String listVar, String elementVar) {
        return IF(BINARY_OP(METHOD_CALL(VARIABLE("this"),listVar), "!=", getNull()))
                .THEN(METHOD_CALL(METHOD_CALL(VARIABLE("this"),listVar), "add", List.of(VARIABLE(elementVar))));
    }

    /**
     * Builds {@code OutputType resultVar = templateInstantiation.process(inputVar);}.
     *
     * @param outputType PAST type of the output bean
     * @param resultVar  name for the result local variable
     * @param inputVar   name of the input bean local variable
     */
    private Definition process(ClassName outputType, String resultVar, String inputVar) {
        return DEFINITION(
                outputType,
                VARIABLE(resultVar),
                METHOD_CALL(METHOD_CALL(VARIABLE("this"),"templateInstantiation"), "process", List.of(VARIABLE(inputVar))));
    }

    // -----------------------------------------------------------------------
    // Java generation via Poet
    // -----------------------------------------------------------------------

    /**
     * Generates the Java source file for {@code PleadWorkflow} and writes it
     * into {@code outputDirectory}.
     *
     * <p>JavaPoet places the file at the correct sub-path, so pass the root
     * source directory (e.g. {@code src/main/java}).  The result is:</p>
     * <pre>
     *   outputDirectory/org/openprovenance/bookptm/PleadWorkflow.java
     * </pre>
     *
     * @param outputDirectory root source directory to write into
     * @throws IOException if the file cannot be written
     */
    public void generateAndCompilePast(String filename, String javaRootDirectory, String pythonOutputDirectory) throws IOException {
        StackTraceElement stackTraceElement = compilerUtil.thisMethodAndLine();
        // Build the PAST tree.
        Class pastClass = generatePleadWorkflow();

        //
        TemplatesProjectConfiguration configs = new TemplatesProjectConfiguration();
        configs.name = "PleadWorkflow";

        String packageName = GENERATED_PACKAGE;

        String javaOutputDirectory= javaRootDirectory + packageName.replace(".", "/") + "/";

        Supplier<Boolean> pythonGenerator = () -> generatePython(pastClass, packageName, pythonOutputDirectory, stackTraceElement);
        //Supplier<Boolean> javaGenerator = () -> generateJava(pastClass, packageName, configs, filename + ".java", javaOutputDirectory, stackTraceElement, compilerUtil);
        Supplier<Boolean> javaGenerator = () -> {
            System.out.println("Generating Java code for " + pastClass.name + "...");
            return generateJava(pastClass, packageName, configs, filename + ".java", javaOutputDirectory, stackTraceElement, compilerUtil);
        };
        SpecificationFile specFile = new SpecificationFile(javaGenerator, pythonGenerator);
        specFile.save();

    }

    /**
     * Convenience entry point.  Accepts an optional output directory as the
     * first argument; defaults to {@code src/main/java}.
     *
     * <p>Example:</p>
     * <pre>
     *   java org.openprovenance.bookptm.GeneratePleadWorkflow target/generated-sources/past
     * </pre>
     */
    public static void main(String[] args) throws IOException {
        String javaOutputDirectory = (args.length > 0)
                ? args[0]
                : "target/generated-sources";
        if (!javaOutputDirectory.endsWith("/")) {
            javaOutputDirectory = javaOutputDirectory + "/";
        }
        new File(javaOutputDirectory).mkdirs();

        String pythonOutputDirectory = (args.length > 1)
                ? args[1]
                : "target/generated-python";
        if (!pythonOutputDirectory.endsWith("/")) {
            pythonOutputDirectory = pythonOutputDirectory + "/";
        }
        new File(pythonOutputDirectory).mkdirs();

        String filename = "PleadWorkflow";

        new GeneratePleadWorkflow().generateAndCompilePast(filename, javaOutputDirectory,pythonOutputDirectory);
        System.out.println("Generated PleadWorkflow.java → "
                + new File(javaOutputDirectory,
                "org/openprovenance/bookptm/PleadWorkflow.java")
                             .getAbsolutePath());
    }
}
