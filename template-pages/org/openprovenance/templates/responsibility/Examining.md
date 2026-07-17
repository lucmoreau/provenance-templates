




- **Name**: `Examining`
- **Fully Qualified Name**: `org.openprovenance.templates.responsibility.Examining`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/responsibility/Examining>
- **Purpose**: This template describes how an entity is examined, observed or assessed, resulting in a new attribute for this entity.
- **Context**: The template describes a common operation that occurs in many circumstances, such as when the status of an object is checked, when its weight is determined, or when its content is validated; a person, an automated agent, or an instrument under the control of an agent can carry out the operation. 
- **Design considerations**: The ability to assert new attributes for this entity following some observation, measurement or analysis.
- **Automation**: [ttfs/config-responsibility-examining.json](project/template-intro1/src/main/resources/ttfs/config-responsibility-examining.json)

![org.openprovenance.templates.responsibility.Examining](project/template-intro1/target/generated-templates/org/openprovenance/templates/responsibility/examining/examining.qualified.png){#fig:org.openprovenance.templates.responsibility.Examining}

- **Details**:

    Initially, there is an entity `e0` that the activity `examining` uses. After this operation, there is an entity `e1` with a new attribute `eprops1` set to `evalues1`, determined by the activity performed by `instrument` under the control of `agent`. Any other attributes of `e0` (which might have been defined in other templates) are expected to remain unchanged.

    Using multiple bindings for `eprops1`-`evalues1`, this activity can create multiple attribute-value pairs.  While the presence of an instrument or an agent is optional, they allow responsibility for the new attribute to be ascribed precisely. 


    The template results from merging four instantiated templates and a further delegation relation. 

    - Triangle1-Entity-UGD describing the entity `e0` evolving to `e1` enriched with attribute-value pair `eprops1`-`evalues1`.
      All other aspects of the entity `e0` remain unchanged.

    - Triangle2-Entity-SDS describes how the entities `e0` and `e1` are specialisations of a more general entity `e`.

    - Triangle3-AGA describes how the activity `examining` was associated with `agent`, who is responsible for `e1` and its new attribute `eprops1`.

    - Likewise, a further Triangle3-AGA describes how the activity `examining` was associated with `instrument`, which agent is responsible for `e1`, and which new attribute `eprops1` holds.


    - Finally, a delegation relation indicates that `instrument` was operated under the control of `agent`.


    The `instrument` and `agent` are optional: either can be specified, or both can be specified. This allows rich descriptions of responsibility for `e1` to be provided.

    The template `Examining` covers many common situations, such as when an agent determines the weight of a box using a scale (the weight would become a new attribute of the box), when an officer checks that an administrative request is complete (the complete flag would become an attribute of the request), or when an autonomous robotic arm checks that a parcel is damaged (the damaged flag would become an attribute of the parcel). These three examples illustrate configurations with an instrument-only (robotic arm), a human-agent-only (officer), and a combination of both (scale and agent).


    

 


