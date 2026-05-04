


- **Name**: `Forking2`
- **Fully Qualified Name**: `org.openprovenance.templates.generic.Forking2`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/generic/Forking2>
- **Purpose**: The template `Forking2` describes the transformation of an input resulting in two distinct outputs.
- **Context**: It is a general transformation applicable to many different contexts.
- **Design considerations**:  Relations rigorously linking nodes using pre-defined triangles.
- **Automation**: [ttfs/config-generic.json](project/template-intro1/src/main/resources/ttfs/config-generic.json)

![org.openprovenance.templates.generic.Forking2](project/template-intro1/target/generated-templates/org/openprovenance/templates/generic/forking2.qualified.png){#fig:org.openprovenance.templates.generic.Forking2}


- **Details**:

    
   There are three entities directly related to the transformation: `input`, `output1` and `output2`; an activity `transforming`; an agent `agent`; and an associated plan `plan`. 

   The template is constructed by instantiating `org.openprovenance.templates.generic.Transformation1`. Instead of a single variable `output`, we now have two variables `output1` and `output2`.  The pattern involves each output derived from the single input. Variables allow specific types to be specified for the activity and edges; distinct roles can also be specified for the two outputs used by the activity.


   The pattern has multiple applications, including creating two subprocesses, splitting a file, cutting a cake in two, or sharing the profits of a business operation.
