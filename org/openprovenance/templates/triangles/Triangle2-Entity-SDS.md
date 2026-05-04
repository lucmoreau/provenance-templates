

- **Name**: `Triangle-Entity-SDS`
- **Fully Qualified Name**: `org.openprovenance.templates.triangles.Triangle2-Entity-SDS`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/triangles/Triangle2-Agent-SDS>
- **Purpose**: This template describes how an entity evolves through derivation, while retaining its identity through specialisation of a more general entity.
- **Context**: The template describes a situation in which an entity's aspects change (e.g., a new location, a new weight, a new format), resulting in a new entity that captures the new aspects while remaining the same "thing"; the entity before the derivation and the one after the derivation are both specialisations of a more general entity. 
- **Design considerations**: The ability to assert new attributes for this entity.
- **Automation**: [ttfs/config-triangle2-entity-sds.json](file:project/template-intro1/src/main/resources/ttfs/config-triangle2-entity-sds.json)


![org.openprovenance.templates.triangles.Triangle-Entity-SDS](project/template-intro1/target/generated-templates/org/openprovenance/templates/triangles/triangle2-entity-sds/triangle2-entity-sds.qualified.png){#fig:org.openprovenance.templates.triangles.Triangle-Entity-SDS}

- **Details**:

    A PROV entity is a physical, digital, conceptual, or other kind of thing with some fixed aspects; entities may be real or imaginary. The fixed aspects of an entity can be described using attribute-value pairs. While some aspects of an entity are fixed for the duration of its lifetime, other characteristics may vary. For instance, a car may have a fixed make and registration during its lifetime, but its location or passengers may change.

    To model this in PROV, a vehicle with its fixed aspects can be described as an entity with attributes make and registration. However, to understand how often a taxi is used, we may need to model it as a vehicle entity with a passenger count that changes whenever a passenger is dropped off or picked up. The template Triangle-Entity-SDS allows us model the relationship between a vehicle with a fixed make and registration and a vehicle with a variable passenger count.


    The template consists of three entities: `e0`, `e1` and `e`. The entity `e1` was derived from `e0`. There are two links, connecting `e0` to `e` and `e1` to `e`, showing that while `e0` changes into `e1`, they retain some unchanging commonality in `e`. Each link is a specialisation, with respective ids `spec0` (for the specialisation of `e0` with respect to `e`) and `spec1` (for the specialisation of `e1` with respect to `e`).


    Another characteristic of this template is that it provides a "justification" for `spec1` by explicitly linking it to the derivation, the entity `e0`, and the previous specialisation `spec0`, via the attributes `provext:derivation`, `provext:entity`, and `provext:specialization`, respectively. These attributes are not defined in the PROV recommendations and therefore belong to the extensibility `provext` namespace.

    This template codifies and extends the "plan for revisions" recipe introduced in [@Moreau-Groth:MC2013]. To avoid any misunderstanding, the template does not imply that deriving an entity from another means the two entities always share a more general entity. For instance, a cake may be made from eggs (and other ingredients), which we would model as a derivation, but that does not mean the eggs and the cake share a common, more general entity. Ultimately, this is a modelling decision, and it is up to the provenance engineer to model changes for the circumstances and use cases they are addressing. This template is available to them when the derived entity shares a common, more general entity with its ancestor.

   
    To ensure extensibility, the attributes `var:eprops1` (for the entity `e1`), `var:dprops` (for the derivation `der`), and `var:sprops1` (for the specialisation `spec1`) support application- or domain-specific descriptions.


 


