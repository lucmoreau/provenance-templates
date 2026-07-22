
- **Name**: `Merging`
- **Fully Qualified Name**: `org.openprovenance.templates.ptm.Merging`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/ptm/Merging>
- **Purpose**: The template `Merging` describes the merging of two templates. 
- **Context**: The template describes one of the two key operations in provenance template management.
- **Design considerations**: The ability to trace the creation of provenance templates.
- **Automation**: [ttfs/config-ptm.json](https://github.com/lucmoreau/provenance-templates/blob/main/src/main/resources/ttfs/config-ptm.json)


![org.openprovenance.templates.ptm.Merging](project/template-intro1/target/generated-templates/org/openprovenance/templates/ptm/ptm-merging.png){#fig:org.openprovenance.templates.ptm.Merging}


- **Details**:

   
   In this template, there are four entities: two serve as inputs, `template1` and `template2`, while the other two serve as outputs: a document and provenance.  The activity `merging` combines the two input templates and generates a new provenance document, which is a provenance template.

   To document the execution of the merging algorithm, the activity also generates a provenance document, in the form of compact bindings (see [Section @sec:alternate.external.bindings.representation]).



   