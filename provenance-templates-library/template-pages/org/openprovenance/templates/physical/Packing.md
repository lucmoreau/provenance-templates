
- **Name**: `Packing`
- **Fully Qualified Name**: `org.openprovenance.templates.physical.Packing`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/physical/Packing>
- **Purpose**: The template `Packing` describes the evolution of a container as an item is added to it.
- **Context**: The template helps describe common situations in the physical world, involving containers such as boxes or pallets. 
- **Design considerations**: The ability to describe the state of the container (before and after inserting an item) and the state of the item (before being packed in the container or after).
- **Automation**: [ttfs/config-packing.json](https://github.com/lucmoreau/provenance-templates/blob/main/src/main/resources/ttfs/config-packing.json)


![org.openprovenance.templates.physical.Packing](project/template-intro1/target/generated-templates/org/openprovenance/templates/physical/packing/packing.qualified.png){#fig:org.openprovenance.templates.physical.Packing}



- **Details**:

    This template is a refinement of `org.openprovenance.templates.collections.InsertingIntoCollection`, where the collection is a physical container and the item is physical. At the start, there is a container `container0` and an item `item0`. After this operation, the container `container1` contains item `item1`.

    The template is the result of merging four instantiated templates. 

    - InsertingIntoCollection describes the container and item, before and after the operation

    - Triangle3-AGA to capture the agent (`packer`) responsible for packing the item in the container.

    - Triangle2-Entity-SDS to describe that the container before and the container after are both specialisations of a single, more general container.

    - Triangle2-Entity-SDS to describe that the item before and the item after are both specialisations of a single, more general item.


    As this is a physical operation, the container and item specialisations have a single existence: the container's previous and new versions cannot coexist, and the same applies to the item. The creation of `item1` and `container1` invalidates (in the sense of provenance) `item0` and `container0`, though we did not make invalidation edges explicit to avoid overloading the visualisation.




