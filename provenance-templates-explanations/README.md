# provenance-templates-explanations

Explanation plans (x-plans), dictionaries and profiles for the templates of
`provenance-templates-library`, enabling the template service to generate
natural language explanations from template instances.

Layout, following the conventions of the ProvToolbox archetype
(`xplain/nlg/<library>/<library>-xplan-library.json`):

```
src/main/resources/xplain/nlg/
```

Each x-plan library consists of a library index (listing the x-plan files),
the x-plan files themselves, and optional dictionaries and profiles. See the
Explanations guide in the ProvToolbox prov-template-archetype documentation
for the x-plan language, the dictionary-based dispatch on provenance types,
and authoring rules.

Resources are packaged both on the plain classpath and under
`META-INF/resources/webjars/template-explanations/0.1.0/`, so a deployed
template service can load them either way.
