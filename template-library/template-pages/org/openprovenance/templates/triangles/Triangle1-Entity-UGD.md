

- **Name**: `Triangle1-Entity-UGD`
- **Fully Qualified Name**: `org.openprovenance.templates.triangles.Triangle1-Entity-UGD`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/triangles/Triangle1-Entity-UGD>
- **Purpose**: This template describes how an entity evolves into another entity through derivation.
- **Context**: The template explicitly links the derivation to the underpinning activity, usage and generation. 
- **Design considerations**: Extensibility through the ability to assert new attributes for the generated entity, as well as for the derivation and activity, possibly specifying their type.
- **Automation**: [ttfs/config-triangle1-ugd.json](project/template-intro1/src/main/resources/ttfs/config-triangle1-ugd.json)


![org.openprovenance.templates.triangles.Triangle1-Entity-UGD](project/template-intro1/target/generated-templates/org/openprovenance/templates/triangles/triangle1-ugd/triangle1-ugd.qualified.png){#fig:org.openprovenance.templates.triangles.Triangle1-Entity-UGD}

- **Details**:

    The research paper [@Moreau:TWEB15] and the W3C recommendation [@Moreau:prov-constraints:20130430] introduce this triangular pattern, which is encoded as a template in the library. There are two entities, `e0` and `e1`, and an activity `activity`. The activity used `e0` and generated `e1`, with `e1` derived from `e0`. As opposed to a common mistake by early users of PROV, this relation cannot be inferred automatically: it must be asserted.

    There are two key aspects in this pattern. First, it explicitly links the derivation to the underlying activity, enabling the provision of additional details, such as time or domain-specific information. Second, the pattern precisely specifies how the derivation refers to the `activity`'s use of `e0` and its contribution to the generation of `e1`.   

    In effect, the pattern `Triangle1-Entity-UGD` relates the flow perspective of provenance, embodied by derivations (see [Section @sec:provenance.flow.perspective]), to the process perspective, captured by activities, generations, and usages (see [Section @sec:process.perspective]).

    To ensure extensibility, the attributes `var:aprops` (for the `activity`), `var:dprops` (for the derivation `der`), `var:eprops1` (for the entity `e1`), `var:uprops` (for the usage `usd`), and `var:gprops` (for the generation `gen`) allow application- or domain-specific information to be added.

    [Figure @fig:org.openprovenance.templates.triangles.Triangle1-Entity-UGD] with explicit qualified relations visualises the link between the derivation and the extra attributes.
    

 


