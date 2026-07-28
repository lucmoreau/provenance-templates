


- **Name**: `Parallel2`
- **Fully Qualified Name**: `org.openprovenance.templates.generic.Parallel2`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/generic/Parallel2>
- **Purpose**: The template `Parallel2` describes the parallel transformation of two inputs by a single activity.
- **Context**: The template demonstrates parallel processing in which multiplicity for inputs and outputs is introduced, but each output remains derived from a single input.
- **Design considerations**: Ensuring a given output is derived from a single input.
- **Automation**: [ttfs/config-generic.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-generic.json)

![org.openprovenance.templates.generic.Parallel2](project/template-intro1/target/generated-templates/org/openprovenance/templates/generic/parallel2.qualified.png){#fig:org.openprovenance.templates.generic.Parallel2}


- **Details**:

   This template has two inputs: `input1` and `input2`; two outputs: `output1` and `output2`; an activity: `transforming`; an agent; and a plan. This template is a direct instantiation of `org.openprovenance.templates.generic.Transformation1`, using multiple values for `input` and `output`.


   In this template, we note the presence of the "linked" property in the bindings file; it is fundamental, as it links the variable `output` to `input`. This results in `output1` being derived from `input1` and `output2` from `input2`, in a similar way.

   On the contrary, `Product2-2` (see next Section) does not link the input with the output variables, implying that each output is derived from all inputs.

   Application of Parallel2 includes running two compression operations in parallel and sending two couriers to two distinct destinations.