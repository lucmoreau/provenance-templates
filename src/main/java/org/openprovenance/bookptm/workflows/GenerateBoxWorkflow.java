package org.openprovenance.bookptm.workflows;

import org.apache.commons.lang3.tuple.Pair;
import org.openprovenance.prov.template.compiler.CompilerUtil;
import org.openprovenance.prov.template.compiler.GeneratorInvoker;
import org.openprovenance.prov.template.compiler.configuration.Locations;
import org.openprovenance.prov.template.compiler.configuration.TemplatesProjectConfiguration;
import org.openprovenance.prov.template.compiler.past.*;
import org.openprovenance.prov.template.compiler.past.Class;
import org.openprovenance.prov.template.compiler.past.annotations.NoSerialization;
import org.openprovenance.prov.template.compiler.past.type.ClassName;
import org.openprovenance.prov.template.compiler.past.type.ParameterizedType;
import org.openprovenance.prov.vanilla.ProvFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.openprovenance.prov.template.compiler.configuration.SpecificationFile.generateJava;
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
import static org.openprovenance.prov.template.compiler.past.Return.RETURN;
import static org.openprovenance.prov.template.compiler.past.Variable.VARIABLE;
import static org.openprovenance.prov.template.compiler.past.type.ClassName.*;

/**
 * Generates a PAST expression tree encoding the behaviour of the Box Workflow.
 *
 * <p>The entry point is {@link #generateBoxWorkflow()}, which returns a {@link Class}
 * PAST node whose structure mirrors BoxWorkflow exactly.  {@code flowToFrom(t1,p1,t2,p2)}
 * calls from the original are translated to direct field assignments
 * {@code ASSIGNMENT(METHOD_CALL(VARIABLE(t1), p1), METHOD_CALL(VARIABLE(t2), p2))}.</p>
 */
public class GenerateBoxWorkflow implements GeneratorInvoker {

    // -----------------------------------------------------------------------
    // Package constants
    // -----------------------------------------------------------------------

    private static final String PHYSICAL_INTEGRATOR_PACKAGE =
            "org.openprovenance.book.physical.client.integrator";

    private static final String RESPONSIBILITY_INTEGRATOR_PACKAGE =
            "org.openprovenance.book.responsibility.client.integrator";

    private static final String PROCESSOR_PACKAGE =
            "org.openprovenance.templates.catalogue.transport.integrator";

    /** Package of the class being generated. */
    private static final String GENERATED_PACKAGE = "org.openprovenance.book.workflows";

    private static final String BOOKPTM_PACKAGE = "org.openprovenance.bookptm";

    // -----------------------------------------------------------------------
    // Type constants – infrastructure
    // -----------------------------------------------------------------------

    /** {@code InputOutputProcessor} – the template-instantiation service. */
    private static final ClassName INPUT_OUTPUT_PROCESSOR =
            ClassName.get("InputOutputProcessor", PROCESSOR_PACKAGE);

    /** {@code java.util.LinkedList} – for list-field initialisers. */
    private static final ClassName LINKED_LIST =
            ClassName.get("LinkedList", "java.util");

    /** {@code java.util.Arrays} – for {@code Arrays.asList(...)} in the return statement. */
    private static final ClassName ARRAYS =
            ClassName.get("Arrays", "java.util");

    // -----------------------------------------------------------------------
    // Type constants – physical integrator beans
    // -----------------------------------------------------------------------

    private static final ClassName AGENT_INIT_INPUTS =
            ClassName.get("Agent_initInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName AGENT_INIT_OUTPUTS =
            ClassName.get("Agent_initOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName ITEM_INIT_INPUTS =
            ClassName.get("Item_initInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName ITEM_INIT_OUTPUTS =
            ClassName.get("Item_initOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName PACKING_INPUTS_1 =
            ClassName.get("PackingInputs_1", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName PACKING_COMPOSITE_INPUTS =
            ClassName.get("Packing_compositeInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName PACKING_COMPOSITE_OUTPUTS =
            ClassName.get("Packing_compositeOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName WEIGHING_INPUTS =
            ClassName.get("WeighingInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName WEIGHING_OUTPUTS =
            ClassName.get("WeighingOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName TRANSPORTING_INPUTS =
            ClassName.get("TransportingInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName TRANSPORTING_OUTPUTS =
            ClassName.get("TransportingOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName UNPACKING_INPUTS_1 =
            ClassName.get("UnpackingInputs_1", PHYSICAL_INTEGRATOR_PACKAGE);

    private static final ClassName UNPACKING_COMPOSITE_INPUTS =
            ClassName.get("Unpacking_compositeInputs",  PHYSICAL_INTEGRATOR_PACKAGE);
    private static final ClassName UNPACKING_COMPOSITE_OUTPUTS =
            ClassName.get("Unpacking_compositeOutputs", PHYSICAL_INTEGRATOR_PACKAGE);

    // -----------------------------------------------------------------------
    // Type constants – responsibility integrator beans
    // -----------------------------------------------------------------------

    private static final ClassName HANDING_OVER_INPUTS =
            ClassName.get("HandingoverInputs",  RESPONSIBILITY_INTEGRATOR_PACKAGE);
    private static final ClassName HANDING_OVER_OUTPUTS =
            ClassName.get("HandingoverOutputs", RESPONSIBILITY_INTEGRATOR_PACKAGE);

    // -----------------------------------------------------------------------
    // Parameterised types
    // -----------------------------------------------------------------------

    /** {@code List<Object>} – return type of {@code run()}. */
    private static final ParameterizedType LIST_OF_OBJECT =
            ParameterizedType.get(LIST, OBJECT);

    // -----------------------------------------------------------------------
    // Generator infrastructure
    // -----------------------------------------------------------------------

    private final CompilerUtil compilerUtil;
    private final ProvFactory pFactory;

    public GenerateBoxWorkflow() {
        this.pFactory = ProvFactory.getFactory();
        this.compilerUtil = new CompilerUtil(pFactory);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Builds and returns a PAST {@link Class} node that encodes the full
     * behaviour of BoxWorkflow: its fields, constructor, and {@code run()} method.
     */
    public Class generateBoxWorkflow() {
        Class pastClass = new PastFactory()
                .CLASS("BoxWorkflow")
                .MODIFIERS(Modifier.PUBLIC)
                .ANNOTATION(NoSerialization.NAME);

        addInstanceFields(pastClass);
        addConstructor(pastClass);
        addRunMethod(pastClass);

        return pastClass;
    }

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------


    private void addInstanceFields(Class pastClass) {
        pastClass.FIELDS(
                FIELD("templateInvoker", INPUT_OUTPUT_PROCESSOR)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL),

                FIELD("inputs", LIST_OF_OBJECT)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL),

                FIELD("outputs", LIST_OF_OBJECT)
                        .MODIFIERS(Modifier.PRIVATE, Modifier.FINAL),

                FIELD("agent1Time", STRING).INITIALIZER(CONSTANT("2024-09-01T10:00:00Z")),
                FIELD("agent2Time", STRING).INITIALIZER(CONSTANT("2024-09-01T10:00:00Z")),
                FIELD("agent3Time", STRING).INITIALIZER(CONSTANT("2024-08-01T10:00:00Z")),
                FIELD("agent4Time", STRING).INITIALIZER(CONSTANT("2024-09-01T10:00:00Z")),

                FIELD("scale1Time", STRING).INITIALIZER(CONSTANT("2022-09-01T10:00:00Z")),
                FIELD("scale2Time", STRING).INITIALIZER(CONSTANT("2023-06-01T10:00:00Z")),
                FIELD("scale3Time", STRING).INITIALIZER(CONSTANT("2024-01-01T10:00:00Z")),

                FIELD("boxTime",       STRING).INITIALIZER(CONSTANT("2024-09-14T10:00:00Z")),
                FIELD("weighing1Time", STRING).INITIALIZER(CONSTANT("2024-09-15T10:00:00Z")),
                FIELD("pickupTime",    STRING).INITIALIZER(CONSTANT("2024-10-01T10:00:00Z")),
                FIELD("drop1Time",     STRING).INITIALIZER(CONSTANT("2024-10-01T17:00:00Z")),
                FIELD("handoverTime",  STRING).INITIALIZER(CONSTANT("2024-10-01T17:15:00Z")),
                FIELD("weighing2Time", STRING).INITIALIZER(CONSTANT("2024-10-01T18:12:00Z")),
                FIELD("handoverTime2", STRING).INITIALIZER(CONSTANT("2024-10-01T05:20:00Z")),
                FIELD("deliveryTime",  STRING).INITIALIZER(CONSTANT("2024-10-01T15:14:00Z")),

                FIELD("MARKER1", INTEGER).MODIFIERS(Modifier.PUBLIC).INITIALIZER(CONSTANT(-1)),
                FIELD("MARKER2", INTEGER).MODIFIERS(Modifier.PUBLIC).INITIALIZER(CONSTANT(-2))

        );
    }

    private void addConstructor(Class pastClass) {
        Constructor constructor = CONSTRUCTOR()
                .MODIFIERS(Modifier.PUBLIC)
                .PARAMETER(INPUT_OUTPUT_PROCESSOR, "templateInvoker")
                .PARAMETER(LIST_OF_OBJECT, "inputs")
                .PARAMETER(LIST_OF_OBJECT, "outputs")
                .BODY(
                        ASSIGNMENT(
                                METHOD_CALL(VARIABLE("this"), "templateInvoker"),
                                VARIABLE("templateInvoker")),
                        ASSIGNMENT(
                                METHOD_CALL(VARIABLE("this"), "inputs"),
                                VARIABLE("inputs")),
                        ASSIGNMENT(
                                METHOD_CALL(VARIABLE("this"), "outputs"),
                                VARIABLE("outputs"))
                );

        pastClass.CONSTRUCTOR(constructor);
    }

    // -----------------------------------------------------------------------
    // run() method
    // -----------------------------------------------------------------------

    private void addRunMethod(Class pastClass) {
        Method runMethod = METHOD("run")
                .MODIFIERS(Modifier.PUBLIC)
                .RETURNS(VOID);

        addComment("AgentInit", runMethod);
        addAgentInit0Block(runMethod);

        addComment("Box and Books Init", runMethod);
        addBoxInitBlock(runMethod);
        addBook1InitBlock(runMethod);
        addBook2InitBlock(runMethod);

        addComment("Packing", runMethod);
        addPackingBlock(runMethod);

        addComment("Scale Agent1 Init and Weighing", runMethod);
        addScaleAgent1InitBlock(runMethod);
        addWeighing1Block(runMethod);

        addComment("Transporter Agent Init and Handing over", runMethod);
        addTransporterAgent1InitBlock(runMethod);
        addHandingover1Block(runMethod);

        addComment("Transporting", runMethod);
        addTransporting1Block(runMethod);

        addComment("Deport Agent Init and Handing over", runMethod);
        addDepotManagerAgentInitBlock(runMethod);
        addHandingover2Block(runMethod);

        addComment("Scale Agent Init2 and Weighing", runMethod);
        addScaleAgent2InitBlock(runMethod);
        addWeighing2Block(runMethod);

        addComment("Transporting", runMethod);
        addTransporterAgent3InitBlock(runMethod);

        addComment("Handing Over", runMethod);
        addHandingover3Block(runMethod);

        addComment("Transporting", runMethod);
        addTransporting2Block(runMethod);

        addComment("Recipient Agent Init and Handing Ove", runMethod);
        addRecipientAgentInitBlock(runMethod);
        addHandingover4Block(runMethod);

        addComment("Scale Agent Init3 and Weighing", runMethod);
        addScaleAgent3InitBlock(runMethod);
        addWeighing3Block(runMethod);

        addComment("Unpacking", runMethod);
        addUnpackingBlock(runMethod);
       // addReturnStatement(runMethod);

        pastClass.METHOD(runMethod);
    }


    private void addComment(String text, Method workflowMethod) {
        workflowMethod.BODY(new Comment(""));
        workflowMethod.BODY(new Comment(text));
        workflowMethod.BODY(new Comment(""));
    }

    // ---- agent_init for box owner (agent_initInputs0 / agent_initOutputs0) -----

    private void addAgentInit0Block(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputs0"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs0"), "location"), CONSTANT("London")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs0"), "type"),     CONSTANT("Person")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs0"), "time"),     thisField("agent1Time")),
                nullGuardedAdd("inputs", "agent_initInputs0"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputs0", "agent_initInputs0"),
                nullGuardedAdd("outputs", "agent_initOutputs0")

        );
    }

    // ---- box_init -----------------------------------------------------------

    private void addBoxInitBlock(Method method) {
        method.BODY(
                DEFINITION(ITEM_INIT_INPUTS, VARIABLE("box_initInputs"),
                        CONSTRUCTOR_CALL(ITEM_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("box_initInputs"), "type"), CONSTANT("Box")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("box_initInputs"), "time"), thisField("boxTime")),
                nullGuardedAdd("inputs", "box_initInputs"),
                process(ITEM_INIT_OUTPUTS, "box_initOutputs", "box_initInputs"),
                nullGuardedAdd("outputs", "box_initOutputs")

                );
    }

    // ---- book1_init ---------------------------------------------------------

    private void addBook1InitBlock(Method method) {
        method.BODY(
                DEFINITION(ITEM_INIT_INPUTS, VARIABLE("book1_initInputs"),
                        CONSTRUCTOR_CALL(ITEM_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("book1_initInputs"), "type"), CONSTANT("Book")),
                nullGuardedAdd("inputs", "book1_initInputs"),
                process(ITEM_INIT_OUTPUTS, "book1_initOutputs", "book1_initInputs"),
                nullGuardedAdd("outputs", "book1_initOutputs")

                );
    }

    // ---- book2_init ---------------------------------------------------------

    private void addBook2InitBlock(Method method) {
        method.BODY(
                DEFINITION(ITEM_INIT_INPUTS, VARIABLE("book2_initInputs"),
                        CONSTRUCTOR_CALL(ITEM_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("book2_initInputs"), "type"), CONSTANT("Book")),
                nullGuardedAdd("inputs", "book2_initInputs"),
                process(ITEM_INIT_OUTPUTS, "book2_initOutputs", "book2_initInputs"),
                nullGuardedAdd("outputs", "book2_initOutputs")
        );
    }

    // ---- composite packing (packingInputs_1, packingInputs_2, composite) -------

    private void addPackingBlock(Method method) {
        method.BODY(
                // --- book 1 packing inputs ---
                DEFINITION(PACKING_INPUTS_1, VARIABLE("packingInputs_1"),
                        CONSTRUCTOR_CALL(PACKING_INPUTS_1, List.of())),
                flowToFrom("packingInputs_1", "item0",      "book1_initOutputs",  "entity0"),
                flowToFrom("packingInputs_1", "item",       "book1_initOutputs",  "entity"),
                flowToFrom("packingInputs_1", "packer",     "agent_initOutputs0", "agent0"),
                flowToFrom("packingInputs_1", "container0", "box_initOutputs",    "entity0"),
                flowToFrom("packingInputs_1", "container",  "box_initOutputs",    "entity"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_1"), "sealed"),        CONSTANT(true)),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_1"), "containerType"), CONSTANT("Box")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_1"), "adding"),        thisField("MARKER1")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_1"), "container1"),    thisField("MARKER2")),

                // --- book 2 packing inputs ---
                DEFINITION(PACKING_INPUTS_1, VARIABLE("packingInputs_2"),
                        CONSTRUCTOR_CALL(PACKING_INPUTS_1, List.of())),
                flowToFrom("packingInputs_2", "item0",      "book2_initOutputs",  "entity0"),
                flowToFrom("packingInputs_2", "item",       "book2_initOutputs",  "entity"),
                flowToFrom("packingInputs_2", "packer",     "agent_initOutputs0", "agent0"),
                flowToFrom("packingInputs_2", "container0", "box_initOutputs",    "entity0"),
                flowToFrom("packingInputs_2", "container",  "box_initOutputs",    "entity"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_2"), "sealed"),        CONSTANT(true)),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_2"), "containerType"), CONSTANT("Box")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_2"), "adding"),        thisField("MARKER1")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packingInputs_2"), "container1"),    thisField("MARKER2")),

                // --- composite ---
                DEFINITION(PACKING_COMPOSITE_INPUTS, VARIABLE("packing_compositeInputs"),
                        CONSTRUCTOR_CALL(PACKING_COMPOSITE_INPUTS, List.of())),
                METHOD_CALL(VARIABLE("packing_compositeInputs"), "addElements",
                        List.of(VARIABLE("packingInputs_1"))),
                METHOD_CALL(VARIABLE("packing_compositeInputs"), "addElements",
                        List.of(VARIABLE("packingInputs_2"))),
                ASSIGNMENT(METHOD_CALL(VARIABLE("packing_compositeInputs"), "count"), CONSTANT(2)),
                nullGuardedAdd("inputs", "packing_compositeInputs"),
                process(PACKING_COMPOSITE_OUTPUTS, "packing_compositeOutputs", "packing_compositeInputs"),
                nullGuardedAdd("outputs", "packing_compositeOutputs")

                );
    }

    // ---- scale agent init 1 (agent_initInputsS1 / agent_initOutputsS1) ---------

    private void addScaleAgent1InitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputsS1"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS1"), "location"), CONSTANT("London")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS1"), "type"),     CONSTANT("Scale")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS1"), "time"),     thisField("scale1Time")),
                nullGuardedAdd("inputs", "agent_initInputsS1"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputsS1", "agent_initInputsS1"),
                nullGuardedAdd("outputs", "agent_initOutputsS1")

                );
    }

    // ---- weighing 1 (weighingInputs1 / weighingOutputs1) ----------------------

    private void addWeighing1Block(Method method) {
        method.BODY(
                DEFINITION(WEIGHING_INPUTS, VARIABLE("weighingInputs1"),
                        CONSTRUCTOR_CALL(WEIGHING_INPUTS, List.of())),
                // flowToFrom(weighingInputs1,"item0", packing_compositeOutputs.__elements.get(0), "container1")
                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("weighingInputs1"), "item0"),
                        METHOD_CALL(elementGet("packing_compositeOutputs", 0), "container1")),
                // flowToFrom(weighingInputs1,"item0", packing_compositeOutputs.__elements.get(1), "container1")
                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("weighingInputs1"), "item0"),
                        METHOD_CALL(elementGet("packing_compositeOutputs", 1), "container1")),
                flowToFrom("weighingInputs1", "item",  "box_initOutputs",     "entity"),
                flowToFrom("weighingInputs1", "agent", "agent_initOutputs0",  "agent0"),
                flowToFrom("weighingInputs1", "scale", "agent_initOutputsS1", "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs1"), "weight"), CONSTANT(10.0d)),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs1"), "time"),   thisField("weighing1Time")),
                nullGuardedAdd("inputs", "weighingInputs1"),
                process(WEIGHING_OUTPUTS, "weighingOutputs1", "weighingInputs1"),
                nullGuardedAdd("outputs", "weighingOutputs1")

                );
    }

    // ---- first transporter agent init (agent_initInputs1 / agent_initOutputs1) --

    private void addTransporterAgent1InitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputs1"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs1"), "location"), CONSTANT("Oxford")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs1"), "type"),     CONSTANT("Person")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs1"), "time"),     thisField("agent2Time")),
                nullGuardedAdd("inputs", "agent_initInputs1"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputs1", "agent_initInputs1"),
                nullGuardedAdd("outputs", "agent_initOutputs1")
                );
    }

    // ---- handingover 1 (handingoverInputs / handingoverOutputs) ----------------

    private void addHandingover1Block(Method method) {
        method.BODY(
                DEFINITION(HANDING_OVER_INPUTS, VARIABLE("handingoverInputs"),
                        CONSTRUCTOR_CALL(HANDING_OVER_INPUTS, List.of())),
                flowToFrom("handingoverInputs", "item0",    "weighingOutputs1",   "item1"),
                flowToFrom("handingoverInputs", "item",     "box_initOutputs",    "entity"),
                flowToFrom("handingoverInputs", "receiver", "agent_initOutputs1", "agent0"),
                flowToFrom("handingoverInputs", "giver",    "agent_initOutputs0", "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("handingoverInputs"), "time"), thisField("pickupTime")),
                nullGuardedAdd("inputs", "handingoverInputs"),
                process(HANDING_OVER_OUTPUTS, "handingoverOutputs", "handingoverInputs"),
                nullGuardedAdd("outputs", "handingoverOutputs")

                );
    }

    // ---- transporting 1 (transportingInputs / transportingOutputs) -------------

    private void addTransporting1Block(Method method) {
        method.BODY(
                DEFINITION(TRANSPORTING_INPUTS, VARIABLE("transportingInputs"),
                        CONSTRUCTOR_CALL(TRANSPORTING_INPUTS, List.of())),
                flowToFrom("transportingInputs", "item0",       "handingoverOutputs",   "item1"),
                flowToFrom("transportingInputs", "item",        "box_initOutputs",      "entity"),
                flowToFrom("transportingInputs", "transporter", "agent_initOutputs1",   "agent0"),
                // transportingInputs.transporter = agent_initOutputs1.agent0  (direct field assignment)
                ASSIGNMENT(
                        METHOD_CALL(VARIABLE("transportingInputs"), "transporter"),
                        METHOD_CALL(VARIABLE("agent_initOutputs1"), "agent0")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("transportingInputs"), "time"), thisField("drop1Time")),
                nullGuardedAdd("inputs", "transportingInputs"),
                process(TRANSPORTING_OUTPUTS, "transportingOutputs", "transportingInputs"),
                nullGuardedAdd("outputs", "transportingOutputs")

                );
    }

    // ---- depot manager agent init (agent_initInputs2 / agent_initOutputs2) -----

    private void addDepotManagerAgentInitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputs2"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs2"), "location"), CONSTANT("London")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs2"), "type"),     CONSTANT("Person")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs2"), "time"),     thisField("agent2Time")),
                nullGuardedAdd("inputs", "agent_initInputs2"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputs2", "agent_initInputs2"),
                nullGuardedAdd("outputs", "agent_initOutputs2")

                );
    }

    // ---- handingover 2 (handingoverInputs2 / handingoverOutputs2) --------------

    private void addHandingover2Block(Method method) {
        method.BODY(
                DEFINITION(HANDING_OVER_INPUTS, VARIABLE("handingoverInputs2"),
                        CONSTRUCTOR_CALL(HANDING_OVER_INPUTS, List.of())),
                flowToFrom("handingoverInputs2", "item0",    "transportingOutputs",  "item1"),
                flowToFrom("handingoverInputs2", "item",     "box_initOutputs",      "entity"),
                flowToFrom("handingoverInputs2", "receiver", "agent_initOutputs2",   "agent0"),
                flowToFrom("handingoverInputs2", "giver",    "agent_initOutputs1",   "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("handingoverInputs2"), "time"), thisField("handoverTime")),
                nullGuardedAdd("inputs", "handingoverInputs2"),
                process(HANDING_OVER_OUTPUTS, "handingoverOutputs2", "handingoverInputs2"),
                nullGuardedAdd("outputs", "handingoverOutputs2")

                );
    }

    // ---- scale agent init 2 (agent_initInputsS2 / agent_initOutputsS2) ---------

    private void addScaleAgent2InitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputsS2"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS2"), "location"), CONSTANT("London-Depot")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS2"), "type"),     CONSTANT("Scale")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS2"), "time"),     thisField("scale2Time")),
                nullGuardedAdd("inputs", "agent_initInputsS2"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputsS2", "agent_initInputsS2"),
                nullGuardedAdd("outputs", "agent_initOutputsS2")

                );
    }

    // ---- weighing 2 (weighingInputs2 / weighingOutputs2) ----------------------

    private void addWeighing2Block(Method method) {
        method.BODY(
                DEFINITION(WEIGHING_INPUTS, VARIABLE("weighingInputs2"),
                        CONSTRUCTOR_CALL(WEIGHING_INPUTS, List.of())),
                flowToFrom("weighingInputs2", "item0",  "handingoverOutputs2",  "item1"),
                flowToFrom("weighingInputs2", "item",   "box_initOutputs",      "entity"),
                flowToFrom("weighingInputs2", "agent",  "agent_initOutputs2",   "agent0"),
                flowToFrom("weighingInputs2", "scale",  "agent_initOutputsS2",  "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs2"), "weight"), CONSTANT(10.0d)),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs2"), "time"),   thisField("weighing2Time")),
                nullGuardedAdd("inputs", "weighingInputs2"),
                process(WEIGHING_OUTPUTS, "weighingOutputs2", "weighingInputs2"),
                nullGuardedAdd("outputs", "weighingOutputs2")
                );
    }

    // ---- second transporter agent init (agent_initInputs3 / agent_initOutputs3) -

    private void addTransporterAgent3InitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputs3"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs3"), "location"), CONSTANT("Oxford")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs3"), "type"),     CONSTANT("Person")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs3"), "time"),     thisField("agent3Time")),
                nullGuardedAdd("inputs", "agent_initInputs3"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputs3", "agent_initInputs3"),
                nullGuardedAdd("outputs", "agent_initOutputs3")

                );
    }

    // ---- handingover 3 (handingoverInputs3 / handingoverOutputs3) --------------

    private void addHandingover3Block(Method method) {
        method.BODY(
                DEFINITION(HANDING_OVER_INPUTS, VARIABLE("handingoverInputs3"),
                        CONSTRUCTOR_CALL(HANDING_OVER_INPUTS, List.of())),
                flowToFrom("handingoverInputs3", "item0",    "weighingOutputs2",    "item1"),
                flowToFrom("handingoverInputs3", "item",     "box_initOutputs",     "entity"),
                flowToFrom("handingoverInputs3", "receiver", "agent_initOutputs3",  "agent0"),
                flowToFrom("handingoverInputs3", "giver",    "agent_initOutputs2",  "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("handingoverInputs3"), "time"), thisField("handoverTime2")),
                nullGuardedAdd("inputs", "handingoverInputs3"),
                process(HANDING_OVER_OUTPUTS, "handingoverOutputs3", "handingoverInputs3"),
                nullGuardedAdd("outputs", "handingoverOutputs3")

                );
    }

    // ---- transporting 2 (transportingInputs2 / transportingOutputs2) -----------

    private void addTransporting2Block(Method method) {
        method.BODY(
                DEFINITION(TRANSPORTING_INPUTS, VARIABLE("transportingInputs2"),
                        CONSTRUCTOR_CALL(TRANSPORTING_INPUTS, List.of())),
                flowToFrom("transportingInputs2", "item0",       "handingoverOutputs3",  "item1"),
                flowToFrom("transportingInputs2", "item",        "box_initOutputs",      "entity"),
                flowToFrom("transportingInputs2", "transporter", "agent_initOutputs3",   "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("transportingInputs2"), "time"), thisField("deliveryTime")),
                nullGuardedAdd("inputs", "transportingInputs2"),
                process(TRANSPORTING_OUTPUTS, "transportingOutputs2", "transportingInputs2"),
                nullGuardedAdd("outputs", "transportingOutputs2")

                );
    }

    // ---- recipient agent init (agent_initInputs4 / agent_initOutputs4) ---------

    private void addRecipientAgentInitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputs4"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs4"), "location"), CONSTANT("Oxford")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs4"), "type"),     CONSTANT("Person")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputs4"), "time"),     thisField("agent4Time")),
                nullGuardedAdd("inputs", "agent_initInputs4"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputs4", "agent_initInputs4"),
                nullGuardedAdd("outputs", "agent_initOutputs4")
                );
    }

    // ---- handingover 4 (handingoverInputs4 / handingoverOutputs4) --------------

    private void addHandingover4Block(Method method) {
        method.BODY(
                DEFINITION(HANDING_OVER_INPUTS, VARIABLE("handingoverInputs4"),
                        CONSTRUCTOR_CALL(HANDING_OVER_INPUTS, List.of())),
                flowToFrom("handingoverInputs4", "item0",    "transportingOutputs2", "item1"),
                flowToFrom("handingoverInputs4", "item",     "box_initOutputs",      "entity"),
                flowToFrom("handingoverInputs4", "receiver", "agent_initOutputs4",   "agent0"),
                flowToFrom("handingoverInputs4", "giver",    "agent_initOutputs3",   "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("handingoverInputs4"), "time"), thisField("deliveryTime")),
                nullGuardedAdd("inputs", "handingoverInputs4"),
                process(HANDING_OVER_OUTPUTS, "handingoverOutputs4", "handingoverInputs4"),
                nullGuardedAdd("outputs", "handingoverOutputs4")

                );
    }

    // ---- scale agent init 3 (agent_initInputsS3 / agent_initOutputsS3) ---------

    private void addScaleAgent3InitBlock(Method method) {
        method.BODY(
                DEFINITION(AGENT_INIT_INPUTS, VARIABLE("agent_initInputsS3"),
                        CONSTRUCTOR_CALL(AGENT_INIT_INPUTS, List.of())),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS3"), "location"), CONSTANT("Brighton")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS3"), "type"),     CONSTANT("Scale")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("agent_initInputsS3"), "time"),     thisField("scale3Time")),
                nullGuardedAdd("inputs", "agent_initInputsS3"),
                process(AGENT_INIT_OUTPUTS, "agent_initOutputsS3", "agent_initInputsS3"),
                nullGuardedAdd("outputs", "agent_initOutputsS3")

                );
    }

    // ---- weighing 3 – recipient weighs item (weighingInputs3 / weighingOutputs3) -

    private void addWeighing3Block(Method method) {
        method.BODY(
                DEFINITION(WEIGHING_INPUTS, VARIABLE("weighingInputs3"),
                        CONSTRUCTOR_CALL(WEIGHING_INPUTS, List.of())),
                flowToFrom("weighingInputs3", "item0",  "handingoverOutputs4",  "item1"),
                flowToFrom("weighingInputs3", "item",   "box_initOutputs",      "entity"),
                flowToFrom("weighingInputs3", "agent",  "agent_initOutputs4",   "agent0"),
                flowToFrom("weighingInputs3", "scale",  "agent_initOutputsS3",  "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs3"), "weight"), CONSTANT(15.0d)),
                ASSIGNMENT(METHOD_CALL(VARIABLE("weighingInputs3"), "time"),   thisField("deliveryTime")),
                nullGuardedAdd("inputs", "weighingInputs3"),
                process(WEIGHING_OUTPUTS, "weighingOutputs3", "weighingInputs3"),
                nullGuardedAdd("outputs", "weighingOutputs3")

                );
    }

    // ---- composite unpacking (unpackingInputs1, unpackingInputs2, composite) ---

    private void addUnpackingBlock(Method method) {
        method.BODY(
                // --- book 1 unpacking inputs ---
                DEFINITION(UNPACKING_INPUTS_1, VARIABLE("unpackingInputs1"),
                        CONSTRUCTOR_CALL(UNPACKING_INPUTS_1, List.of())),
                flowToFrom("unpackingInputs1", "container",  "box_initOutputs",      "entity"),
                flowToFrom("unpackingInputs1", "container0", "weighingOutputs3",     "item1"),
                flowToFrom("unpackingInputs1", "item",       "book1_initOutputs",    "entity"),
                flowToFrom("unpackingInputs1", "unpacker",   "agent_initOutputs4",   "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("unpackingInputs1"), "container1"), thisField("MARKER1")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("unpackingInputs1"), "removing"),   thisField("MARKER2")),

                // --- book 2 unpacking inputs ---
                DEFINITION(UNPACKING_INPUTS_1, VARIABLE("unpackingInputs2"),
                        CONSTRUCTOR_CALL(UNPACKING_INPUTS_1, List.of())),
                flowToFrom("unpackingInputs2", "container",  "box_initOutputs",      "entity"),
                flowToFrom("unpackingInputs2", "container0", "weighingOutputs3",     "item1"),
                flowToFrom("unpackingInputs2", "item",       "book2_initOutputs",    "entity"),
                flowToFrom("unpackingInputs2", "unpacker",   "agent_initOutputs4",   "agent0"),
                ASSIGNMENT(METHOD_CALL(VARIABLE("unpackingInputs2"), "container1"), thisField("MARKER1")),
                ASSIGNMENT(METHOD_CALL(VARIABLE("unpackingInputs2"), "removing"),   thisField("MARKER2")),

                // --- composite ---
                DEFINITION(UNPACKING_COMPOSITE_INPUTS, VARIABLE("unpacking_compositeInputs"),
                        CONSTRUCTOR_CALL(UNPACKING_COMPOSITE_INPUTS, List.of())),
                METHOD_CALL(VARIABLE("unpacking_compositeInputs"), "addElements",
                        List.of(VARIABLE("unpackingInputs1"))),
                METHOD_CALL(VARIABLE("unpacking_compositeInputs"), "addElements",
                        List.of(VARIABLE("unpackingInputs2"))),
                ASSIGNMENT(METHOD_CALL(VARIABLE("unpacking_compositeInputs"), "count"), CONSTANT(2)),
                nullGuardedAdd("inputs", "unpacking_compositeInputs"),
                process(UNPACKING_COMPOSITE_OUTPUTS, "unpacking_compositeOutputs", "unpacking_compositeInputs"),
                nullGuardedAdd("outputs", "unpacking_compositeOutputs")

                );
    }

    // ---- return Arrays.asList(...) ------------------------------------------

    private void addReturnStatement(Method method) {
        method.BODY(
                RETURN(METHOD_CALL(ARRAYS, "asList", List.of(
                        VARIABLE("box_initInputs"),   VARIABLE("box_initOutputs"),
                        VARIABLE("book1_initInputs"), VARIABLE("book1_initOutputs"),
                        VARIABLE("book2_initInputs"), VARIABLE("book2_initOutputs"),
                        VARIABLE("packing_compositeInputs"), VARIABLE("packing_compositeOutputs"),

                        VARIABLE("agent_initInputs0"),  VARIABLE("agent_initOutputs0"),
                        VARIABLE("agent_initInputs1"),  VARIABLE("agent_initOutputs1"),
                        VARIABLE("agent_initInputs2"),  VARIABLE("agent_initOutputs2"),
                        VARIABLE("agent_initInputs3"),  VARIABLE("agent_initOutputs3"),
                        VARIABLE("agent_initInputs4"),  VARIABLE("agent_initOutputs4"),
                        VARIABLE("agent_initInputsS1"), VARIABLE("agent_initOutputsS1"),
                        VARIABLE("agent_initInputsS2"), VARIABLE("agent_initOutputsS2"),
                        VARIABLE("agent_initInputsS3"), VARIABLE("agent_initOutputsS3"),

                        VARIABLE("handingoverInputs"),  VARIABLE("handingoverOutputs"),
                        VARIABLE("transportingInputs"), VARIABLE("transportingOutputs"),
                        VARIABLE("handingoverInputs2"), VARIABLE("handingoverOutputs2"),
                        VARIABLE("handingoverInputs3"), VARIABLE("handingoverOutputs3"),
                        VARIABLE("transportingInputs2"),VARIABLE("transportingOutputs2"),
                        VARIABLE("handingoverInputs4"), VARIABLE("handingoverOutputs4"),
                        VARIABLE("weighingInputs3"),    VARIABLE("weighingOutputs3"),

                        VARIABLE("unpacking_compositeInputs"), VARIABLE("unpacking_compositeOutputs")
                )))
        );
    }

    // -----------------------------------------------------------------------
    // Helper builders
    // -----------------------------------------------------------------------

    /**
     * Builds the PAST assignment {@code toVar.toProperty = fromVar.fromProperty},
     * replacing a runtime {@code flowToFrom(toBean, toProperty, fromBean, fromProperty)} call.
     */
    private Assignment flowToFrom(String toVar, String toProperty,
                                  String fromVar, String fromProperty) {
        return ASSIGNMENT(
                METHOD_CALL(VARIABLE(toVar),   toProperty),
                METHOD_CALL(VARIABLE(fromVar), fromProperty));
    }

    /**
     * Builds {@code var.__elements.get(index)}, used when a source object
     * is an element retrieved from a composite output list.
     */
    private MethodCall elementGet(String var, int index) {
        return METHOD_CALL(
                METHOD_CALL(VARIABLE(var), "__elements"),
                "get",
                List.of(CONSTANT(index)));
    }

    /**
     * Builds the accessor {@code this.fieldName}.
     */
    private MethodCall thisField(String fieldName) {
        return METHOD_CALL(VARIABLE("this"), fieldName);
    }

    /**
     * Builds {@code OutputType resultVar = this.templateInvoker.process(inputVar);}.
     */
    private Definition process(ClassName outputType, String resultVar, String inputVar) {
        return DEFINITION(
                outputType,
                VARIABLE(resultVar),
                METHOD_CALL(
                        METHOD_CALL(VARIABLE("this"), "templateInvoker"),
                        "process",
                        List.of(VARIABLE(inputVar))));
    }

    // -----------------------------------------------------------------------
    // Java / Python generation via Poet
    // -----------------------------------------------------------------------

    /**
     * Generates the Java (and Python) source file for {@code BoxWorkflow} and writes it
     * into {@code javaRootDirectory} / {@code pythonOutputDirectory}.
     *
     * @return
     * @throws IOException if the file cannot be written
     */
    public Pair<Class, StackTraceElement> generateAndCompilePast(){
        StackTraceElement stackTraceElement = compilerUtil.thisMethodAndLine();

        // Build the PAST tree.
        Class pastClass = generateBoxWorkflow();
        return Pair.of(pastClass, stackTraceElement);
    }

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**
     * Convenience entry point. Accepts an optional Java output directory as the
     * first argument (defaults to {@code target/generated-sources}) and an optional
     * Python output directory as the second argument
     * (defaults to {@code target/generated-python}).
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

        String filename = "BoxWorkflow";

        new GenerateBoxWorkflow().generateAndCompilePast(
        );

        System.out.println("Generated BoxWorkflow.java → "
                + new File(javaOutputDirectory,
                        "org/openprovenance/book/workflows/BoxWorkflow.java")
                             .getAbsolutePath());

    }

    @Override
    public Pair<Class, StackTraceElement> generate(org.openprovenance.prov.model.ProvFactory provFactory, TemplatesProjectConfiguration configs, Locations locations, String s, Map<String, Object> map) {

        System.out.println("*** Generating BoxWorkflow ***");


        return new GenerateBoxWorkflow().generateAndCompilePast();


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

}
