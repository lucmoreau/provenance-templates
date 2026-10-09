

- **Name**: `Assigning`
- **Fully Qualified Name**: `org.openprovenance.templates.responsibility.Assigning`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/responsibility/Assigning>
- **Purpose**: This template describes how an entity is attributed to an agent.
- **Context**: The template consists of an 'assigning' activity that results in a generated entity linked to an agent via an attribution link; before the 'assigning' activity, no attribution link existed. 
- **Design considerations**: The template allows new attributes for this entity and agent to be expressed after assignment.
- **Automation**: [ttfs/config-responsibility-assigning.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-responsibility-assigning.json)

![org.openprovenance.templates.responsibility.Assigning](project/template-intro1/target/generated-templates/org/openprovenance/templates/responsibility/assigning/assigning.qualified.svg){#fig:org.openprovenance.templates.responsibility.Assigning}



- **Details**:

    Initially, there is an entity `e` and its specialization `e0`, with no attributes specified in this template. An activity `assigning` uses entity `e0`. After this operation, entity `e1` has a new attribute `eprops1` with value `evalues1`, determined by the activity `assigning`. All other attributes of `e` and `e0` are expected to remain unchanged. The outcome `e1` is attributed to `ag1`.

    At the start, there is also an agent `ag` and its specialization `ag0`, with no attributes specified in the template. After the `assigning` activity, there is an agent `ag1` with a new attribute `agprops1` set to `agvalues1`. All other attributes of `ag` and `ag0` remain unchanged. 


    The template results from merging four instantiated templates and an additional attribution relation. 

    - Triangle1-Entity-UGD describing the entity `e0` evolving into `e1`, enriched with the attribute-value pair `eprops1`-`evalues1`. All other aspects of `e0` remain unchanged.

    - Triangle1-Agent-UGD describing the agent `ag0` evolving to `ag1`, enriched with the attribute-value pair `agprops1`-`agvalues1`. All other aspects of the entity `ag0` remain unchanged. Note that `ag0` is not associated with the activity but is 'used' by it, meaning it must exist and be available for assignment. Agent `ag0` is not associated with the activity because another agent (not modelled in the template) might be responsible for this assignment.

    - Triangle2-Entity-SDS describes how the entities `e0` and `e1` are specializations of a more general entity `e`.

    - Triangle2-Agent-SDS describes how the agents `ag0` and `ag1` are specializations of a more general agent `ag`.

    - Finally, an attribution relation links `e1` to `ag1`.

    Before the activity, `e0` and `ag0` are not linked by an attribution relation in the template; after the activity, `e1` has an attribution to `ag1`. Once `e1` is generated, `e0` reaches the end of its lifetime; likewise, once `ag1` is generated, `ag0` ceases to exist. The template's intent is to convey this information, but it does not explicitly encode it. If this information is critical for a given application context, it may be captured explicitly via an invalidation link.

    To recognise this activity `assigning`, we introduce a type `resp:Assigning`, which is used in the activity and attribution descriptions. To characterise the assignment of responsibility due to the activity `assigning`, the attribution carries the attributes `activity`, `entityDerivation` and `agentDerivation`, which link to the activity `assigning`, the entity derivation `der1` and the agent derivation `der2`. These are the properties `hadActivity`, `hadEntityDerivation` and `hadAgentDerivation` of the [OpenProvenance vocabulary](https://openprovenance.org/ns/openprov), which adopt the PROV-O convention of a `had` prefix. 


    This template does not specify the agent responsible for this assignment. It could be a self-assignment (by `ag0`) or a third-party assignment. In either case, the agent and its association with the activity would need to be explicitly modelled. It may be that no agent was responsible, or that no agent was known to be responsible for the item before the `assigning` activity. The template `HandingOver` (next) is a variant that transfers responsibility from one agent to another.

 

