
- **Name**: `Packing`
- **Fully Qualified Name**: `org.openprovenance.templates.physical.Packing`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/physical/Packing>
- **Purpose**: The `Packing` template describes how a container evolves as an item is added to it.
- **Context**: The template helps describe common situations in the physical world, involving containers such as boxes or pallets. 
- **Design considerations**: The ability to describe the container's state (before and after inserting an item) and the item's state (before packing in the container or after).
- **Automation**: [ttfs/config-packing.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-packing.json)


![org.openprovenance.templates.physical.Packing](project/template-intro1/target/generated-templates/org/openprovenance/templates/physical/packing/packing.qualified.svg){#fig:org.openprovenance.templates.physical.Packing}



- **Details**:

    This template refines `org.openprovenance.templates.collections.InsertingIntoCollection`, in which the collection is a physical container and the item is physical. At the start, there is a container `container0` and an item `item0`. After this operation, the container `container1` contains item `item1`.

    The template results from merging four instantiated templates. 

    - InsertingIntoCollection describes the container and item before and after the operation

    - Triangle3-AGA to capture the agent (`packer`) responsible for packing the item into the container.

    - Triangle2-Entity-SDS to indicate that the container before and the container after are both specializations of a single, more general container.

    - Triangle2-Entity-SDS to describe that the item before and the item after are both specializations of a single, more general item.


    As this is a physical operation, the container and item specializations have a single existence: the container's previous and new versions cannot coexist, and the same applies to the item. The creation of `item1` and `container1` invalidates (in the sense of provenance) `item0` and `container0`, though we did not make the invalidation edges explicit to avoid overloading the visualisation.




