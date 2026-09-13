

- **Name**: `Triangle5-GGM`
- **Fully Qualified Name**: `org.openprovenance.templates.triangles.Triangle5-GGM`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/triangles/Triangle5-GGM>
- **Purpose**: This template describes the situation resulting from an activity that adds an element to a collection.
- **Context**: The template describes the membership link between a collection and an item after the item was added to the collection, with the potential (in combination with other patterns) to distinguish the item and the collection before and after the item was added. 
- **Design considerations**: The ability to assert new attributes for the membership, the collection and the item, as well as for the activity.
- **Automation**: [ttfs/config-triangle5-ggm.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-triangle5-ggm.json)


![org.openprovenance.templates.triangles.Triangle5-GGM](project/template-intro1/target/generated-templates/org/openprovenance/templates/triangles/triangle5-ggm/triangle5-ggm.qualified.svg){#fig:org.openprovenance.templates.triangles.Triangle5-GGM}

- **Details**:

    In PROV, membership is a relation between a collection and its members, without information about how the membership was constructed, i.e., it is a relation without a corresponding activity. There are many circumstances in which membership results from an action, such as adding a book to a shelf, copying a file into a folder, or selecting the five best movies of all time. We address this shortcoming of PROV by introducing the Triangle5-GGM template.


    The template Triangle5-GGM (Generation-Generation-Membership) involves a collection `collection`, the entity `item`, and an activity `adding`. The activity `adding` generates `item` and `collection`. The template links `collection` to `item` via the membership relation. 

    The template asserts a membership link from the collection to the item, which can be annotated with additional information using the attributes `openprov:activity`, `openprov:collectionGeneration`, and `openprov:itemGeneration`, allowing references to the activity and to the generations (collection generation with `openprov:collectionGeneration` and item generation with `openprov:itemGeneration`). These attributes are not predefined in PROV: they are the properties `openprov:hadActivity`, `openprov:hadCollectionGeneration` and `openprov:hadItemGeneration` of the [OpenProvenance vocabulary](https://openprovenance.org/ns/openprov), written without `had` as PROV-N writes `prov:type`.

    To ensure extensibility, the attributes `var:mprops` (for the membership), `var:gprops0` and `var:gprops1` (for the generations), `var:cprops` (for the collection), and `var:iprops` (for the item) allow application- or domain-specific typing to be added. 

    This template does not quite follow the pattern introduced in [Section @sec:triangular.patterns], which requires that the first two edges be composable and that the third be a relation that cannot be inferred from their composition. Here, the generations `gen0` and `gen1` are not composable because both point to the activity. However, we felt this triangle has some use, as shown in [Section @sec:templates.inserting.into.collection], where we demonstrate how it is composed with other templates, allowing us to describe the situations before an item is added to a collection and after.

