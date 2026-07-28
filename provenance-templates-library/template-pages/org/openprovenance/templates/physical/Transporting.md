
- **Name**: `Transporting`
- **Fully Qualified Name**: `org.openprovenance.templates.physical.Transporting`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/physical/Transporting>
- **Purpose**: The template `Transporting` describes the transportation of a physical object to a new location.
- **Context**: The template is useful to describe transportation in a logistics context.
- **Design considerations**: The ability to identify the item (before and after transport), the item's location after transport, the agent involved in the transport, and the dispatch schedule.
- **Automation**: [ttfs/config-transporting.json](https://github.com/lucmoreau/provenance-templates/blob/main/src/main/resources/ttfs/config-transporting.json)


![org.openprovenance.templates.physical.Transporting](project/template-intro1/target/generated-templates/org/openprovenance/templates/physical/transporting/transporting.qualified.png){#fig:org.openprovenance.templates.physical.Transporting}



- **Details**:

    At the start, there is an `item0`. After this operation, there is an item `item1` with a location.

    The template results from merging three instantiated templates. 


    - Triangle1-Entity-UGD describes the item `item0` before transportation
      and the item `item1` after transportation to a new location, with optionally
      other attributes.

    - Triangle3-AGA describes how an agent `transporter` is associated with the `transporting` activity that resulted in `item1`. At the destination, the `transporter` is responsible for `item1`, as captured by an attribution. Triangle3-AGA allows an optional plan, the dispatch `schedule`, to be specified, according to which the transporter delivers the item to its destination.

    - Triangle2-Entity-SDS links the two instances `item0` and `item1` to a more general `item`, whose aspects remain constant during transportation. As specialisations of `item`, the entities `item0` and `item1` may be assigned different locations.

    The transporter's nature is not specified. Domain-specific instantiations of the template need to consider whether it is human (e.g., a person driver) or a vehicle (e.g., a delivery van).  If required, multiple values can be used for `transporter` to provide both a driver and a vehicle.


    Given that `item` is expected to be a physical object, `item0` and `item1` cannot coexist. Therefore, `item0` is invalidated by the `transporting` activity, but we did not introduce an explicit edge for this in the template to avoid overloading the visualisation.

    Finally, to support a broad range of use cases, the template allows the item's role to be specified; for instance, this would allow a passenger to be distinguished from cargo.

    For applications that need to track entities' locations precisely, it might be preferable to consider two distinct templates, `departing` and `arriving`: the first captures that `item0` is no longer at its original location and becomes a new item 'in movement', whereas the second describes the arrival of the item 'in movement' at its destination. In between, there may be snapshots, all specialisations of `item`, with a new value for location wherever it has been observed or estimated.




