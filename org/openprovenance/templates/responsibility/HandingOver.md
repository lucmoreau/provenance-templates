


- **Name**: `HandingOver`
- **Fully Qualified Name**: `org.openprovenance.templates.responsibility.HandingOver`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/responsibility/HandingOver>
- **Purpose**: This template describes how responsibility over an entity is passed from one agent to another.
- **Context**: The template describes the operation in which an agent hands over an item to another agent, in effect passing on responsibility, or custody, of this item to the other agent. 
- **Design considerations**: The ability to assert new attributes for this entity following the handover.
- **Automation**: [ttfs/config-responsibility-handingover.json](project/template-intro1/src/main/resources/ttfs/config-responsibility-handingover.json)

![org.openprovenance.templates.responsibility.HandingOver](project/template-intro1/target/generated-templates/org/openprovenance/templates/responsibility/handingover/handingover.qualified.png){#fig:org.openprovenance.templates.responsibility.HandingOver}


- **Details**:

    At the start, there is an entity `item` and its specialisation `item0`, with no attributes specified in this template. An activity `handingover` uses entity `item0`.  After this operation, an entity `item1` is created with a new attribute `iprops1` and a value `ivalues1`, determined by the activity `handingover`.  All other attributes of `item` and `item0` are expected to remain unchanged.

    Attribution changes during the course of this operation: before the `handingover` activity, `item0` is attributed to the agent `giver`; after the activity, an agent `receiver` is associated with `item1`.


    The template results from merging four instantiated templates. 

    - Triangle1-Entity-UGD describing the entity `item0` evolving to `item1` enriched with attribute-value pair `iprops1`-`ivalues1`. All other aspects of the entity `item0` remain unchanged.


    - Triangle2-Entity-SDS describes how the entities `item0` and `item1` are specialisations of a more general entity `item`.

    - Triangle3-AGA describes how the `receiver` agent is involved in the activity, resulting in the agent being linked to `item1` by means of an attribution link.

    - Triangle4-AIA describes how the `giver` agent is responsible for the invalidation of `item0` by the very fact of handing it over to the `receiver` agent.  

    Before the activity, `item0` and `receiver` were not linked by an attribution relation; after the activity, `item1` has an attribution to `receiver`, but has lost its attribution link to `giver`. Once `item1` is generated, `item0` reaches the end of its lifetime, marked by the invalidation relation.

    The template also allows the activity type, attribution type, and agents' roles (`role0` and `role1`) to be specified. 

    We note how different this template is from Assigning ([Section @sec:templates.responsibility.assigning]). The Assigning template emphasises how the agent evolved before and after responsibility assignment. Here, this is not made explicit. The provenance requirements will drive template modelling. The approach in Assigning is more focused on the agent and its responsibility, whereas in HandingOver, the focus is on the entity and its attribution.
Both approaches could be combined, but it adds some complexity to the template.

