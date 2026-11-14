


- **Name**: `Unpacking`
- **Fully Qualified Name**: `org.openprovenance.templates.physical.Unpacking`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/physical/Unpacking>
- **Purpose**: The `Unpacking` template describes how a container evolves as an item is removed from it.
- **Context**: The template helps describe common situations in the physical world involving containers such as boxes and pallets. 
- **Design considerations**: The ability to describe the container's state (before and after an item is removed) and the item's state (before it is removed from the container or after).
- **Automation**: [ttfs/config-unpacking.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-unpacking.json)

![org.openprovenance.templates.physical.Unpacking](project/template-intro1/target/generated-templates/org/openprovenance/templates/physical/unpacking/unpacking.qualified.svg){#fig:org.openprovenance.templates.physical.Unpacking}



- **Details**:

    This template is a refinement of `org.openprovenance.templates.collections.RemovingFromCollection`, where the collection is a physical container and the item is physical. At the start, there is a container `container0` containing an item `item0`. An activity `removing`, performed by the agent `unpacker`, takes `item0` out of `container0`, resulting in the container `container1`, which no longer contains the item, and in `item1`, the item as it is once out of the container.

    The template results from merging five instantiated templates. 

    - RemovingFromCollection describes the container and the item, before and after the operation.

    - Triangle3-AGA describes the agent `unpacker`, associated with `removing` and responsible for the item `item1`.

    - Triangle3-AGA describes the same agent `unpacker` as responsible for the container `container1`.

    - Triangle2-Entity-SDS states that the container before and the container after are both specializations of a single, more general container, `container`.

    - Triangle2-Entity-SDS states that the item before and the item after are both specializations of a single, more general item, `item`.


    As this is a physical operation, the container and item specializations have a single instance. The creation of `item1` and `container1` invalidates (in the sense of provenance) `item0` and `container0`, though we did not make invalidation edges explicit to avoid overloading the visualisation.




