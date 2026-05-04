
- **Name**: `Instantiating`
- **Fully Qualified Name**: `org.openprovenance.templates.ptm.Instantiating`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/ptm/Instantiating>
- **Purpose**: The template `Instantiating` describes the instantiation process of a template with a set of bindings.
- **Context**: The template describes one of the two key operations in provenance template management.
- **Design considerations**: The ability to trace the creation of provenance templates.
- **Automation**: [ttfs/config-ptm.json](project/template-intro1/src/main/resources/ttfs/config-ptm.json)


![org.openprovenance.templates.ptm.Instantiating](project/template-intro1/target/generated-templates/org/openprovenance/templates/ptm/ptm-instantiating.png){#fig:org.openprovenance.templates.ptm.Instantiating}



- **Details**:

   In this template, there are four entities: two serve as inputs (a template and a set of bindings), while the other two serve as outputs (a document and some provenance). The activity `instantiating` performs the instantiation process on its two inputs and generates a new provenance document, which may be a fully instantiated provenance document or a provenance template.

   To document the execution of the instantiation algorithm, the activity also generates a provenance document, in the form of compact bindings (see [Section @sec:alternate.external.bindings.representation]).



   