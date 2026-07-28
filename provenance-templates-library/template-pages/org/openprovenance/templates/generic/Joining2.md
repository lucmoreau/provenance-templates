






- **Name**: `Joining2`
- **Fully Qualified Name**: `org.openprovenance.templates.generic.Joining2`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/generic/Joining2>
- **Purpose**: The template `Joining2` describes the transformation of two inputs resulting in a single output.
- **Context**: It is a general transformation applicable to many different contexts.
- **Design considerations**: Relations rigorously linking nodes using pre-defined triangles.
- **Automation**: [ttfs/config-generic.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-generic.json)

![org.openprovenance.templates.generic.Joining2](project/template-intro1/target/generated-templates/org/openprovenance/templates/generic/joining2.qualified.png){#fig:org.openprovenance.templates.generic.Joining2}

- **Details**:
   
   There are three entities directly related to the transformation: `input1`, `input2` and `output`; an activity `transforming`; an agent `agent`; and an associated plan `plan`. 

   The template is constructed by instantiating `org.openprovenance.templates.generic.Transformation1`. Instead of a single variable `input`, we now have two variables, `input1` and `input2`. The pattern produces a single output derived from both inputs. Variables allow specific types to be specified for the activity and its edges. Extensibility variables also allow distinct roles to be specified for the two inputs used by the activity.

   The pattern has multiple applications, including binary arithmetic operations (e.g., adding two numbers), concatenating two files, and merging two provenance graphs.
