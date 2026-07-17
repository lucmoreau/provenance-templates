


- **Name**: `Triangle2-Agent-SDS`
- **Fully Qualified Name**: `org.openprovenance.templates.triangles.Triangle2-Agent-SDS`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/triangles/Triangle2-Agent-SDS>
- **Purpose**: This template describes how an agent evolves through derivation, while remaining a specialisation of a more general agent.
- **Context**: The template describes a situation in which an agent's aspects change (e.g., new location, new qualification), resulting in a new agent instance; the agent instance before the derivation and the agent instance after the derivation are both specialisations of a more general agent. 
- **Design considerations**: The ability to assert changing attribute-value pairs (either updated or new) in the new agent.
- **Automation**:   [ttfs/config-triangle2-agent-sds.json](project/template-intro1/src/main/resources/ttfs/config-triangle2-agent-sds.json)

![org.openprovenance.templates.triangles.Triangle-Agent-SDS](project/template-intro1/target/generated-templates/org/openprovenance/templates/triangles/triangle2-agent-sds/triangle2-agent-sds.svg){#fig:org.openprovenance.templates.triangles.Triangle2-Agent-SDS}



- **Details**:

    
    In PROV, agents can be entities, meaning that they can be generated, used, and derived from other agents, and also be specialisations of other agents. This pattern is a variant of `Triangle2-Entity-SDS` ([Section @sec:triangle2.entity.sds]), with `ag`, `ag0` and `ag1` replacing `e`, `e0` and `e1`, respectively. All other considerations apply here.

    The template `Triangle2-Agent-SDS` is a convenience pattern that preserves precise typing information (agent rather than entity).
