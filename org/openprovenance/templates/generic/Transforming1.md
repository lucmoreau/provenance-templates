

- **Name**: `Transforming1`
- **Fully Qualified Name**: `org.openprovenance.templates.generic.Transforming1`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/generic/Transforming1>
- **Purpose**: The template `Transforming1` describes the transformation of an input into an output.
- **Context**: It is intended to be a general form of transformation, with a single input and a single output.
- **Design considerations**: Relations rigorously linking nodes using pre-defined triangles.

- **Automation**: [ttfs/config-generic-transforming.json](project/template-intro1/src/main/resources/ttfs/config-generic-transforming.json)


![org.openprovenance.templates.generic.Transforming1](project/template-intro1/target/generated-templates/org/openprovenance/templates/generic/transforming/transforming1.qualified.png){#fig:org.openprovenance.templates.generic.Transforming1}


- **Details**:

   There are two entities directly related to the transformation: `input` and `output`; an activity `transforming`; an agent `agent`; and an associated plan `plan`. The template is constructed by instantiating two templates:

   - The template Triangle1-Entity-UGD links the output to the input by means of a derivation, underpinned by the activity `transforming`, which used the input and generated the output.

   - The template Triangle2-Entity-AGA introduces an agent (and a plan), which is associated with the activity and bears responsibility for the generated output.

  While simple, this template is overly general. It can easily be instantiated for different applications and domain types. Using multiplicities, it can also be instantiated for different topologies. Extensibility variables `var:iprops`, `var:oprops`, `var:uprops`, and `var:gprops` (respectively, for the `input`, `output`, usage `usd`, and generation `gen`), allow types or roles to be defined for different situations.
   
