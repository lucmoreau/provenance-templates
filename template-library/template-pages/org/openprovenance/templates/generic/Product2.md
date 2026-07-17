


- **Name**: `Product2-2`
- **Fully Qualified Name**: `org.openprovenance.templates.generic.Product2-2`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/generic/Product2-2>
- **Purpose**: The template `Product2-2` describes the transformation of two inputs resulting in two distinct outputs.
- **Context**: It is a general transformation applicable to many different contexts.
- **Design considerations**: Built on some triangles, using multiplicity to accommodate multiple inputs and outputs, with explicit dependencies. 
- **Automation**: [ttfs/config-generic.json](project/template-intro1/src/main/resources/ttfs/config-generic.json)

![org.openprovenance.templates.generic.Product2-2](project/template-intro1/target/generated-templates/org/openprovenance/templates/generic/product2-2.qualified.png){#fig:org.openprovenance.templates.generic.Product2-2}


- **Details**:

   
   This template has two inputs: `input1` and `input2`; two outputs: `output1` and `output2`; an activity: `transforming`; an agent; and a plan. This template is a direct instantiation of `org.openprovenance.templates.generic.Transformation1`, which uses multiple values for `input` and `output`.


   In this template, there is no "linked" property in the bindings file linking the variable `output` to `input` (as opposed to Parallel2). This results in `output1` being derived from both `input1` and `input2`, and `output2` is derived similarly.

   On the contrary, `Parallel2` links the input with the output variables, implying that each output is derived from a single input.

   An application of Product2-2 is integer division, which takes a dividend and a divisor and produces a quotient and a remainder. Each of the quotient and remainder is derived from the dividend and divisor. Likewise, a shuffle operation that takes two heaps of cards, shuffles them, and creates two new heaps of cards is an instance of Product2-2.


  