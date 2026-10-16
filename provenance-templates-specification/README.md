# PROV-TEMPLATE specifications

The PROV-TEMPLATE family of documents, written with [ReSpec](https://respec.org/), successor of
[PROV-TEMPLATE 2014-06-07](https://openprovenance.org/prov-template-2014-06-07/).

| Document | Directory | Normative |
|---|---|---|
| PROV-TEMPLATE-OVERVIEW — the approach and the guide to the family | [`prov-template-overview/`](prov-template-overview/index.html) | no |
| PROV-TEMPLATE-PRIMER — a nightly file processing pipeline, step by step | [`prov-template-primer/`](prov-template-primer/index.html) | no |
| PROV-TEMPLATE — templates, `tmpl:`, bindings, bindings schemas, indexes, TTFs; tools (non-normative) | [`prov-template/`](prov-template/index.html) | yes |
| PROV-TEMPLATE-ALGORITHMS — instantiation and merge | [`prov-template-algorithms/`](prov-template-algorithms/index.html) | yes |
| PROV-TEMPLATE-LIBRARY — the library of templates | [`prov-template-library/`](prov-template-library/index.html) | no |

- `common/config.js` — the shared ReSpec configuration and bibliography; `common/style.css`.
- `prov-template-primer/examples/` — the primer's templates, bindings, bindings schema and TTF; `run/` and `figures/`
  are generated.
- `prov-template-algorithms/examples/` — the algorithm document's examples and their instantiations.
- `prov-template/schemas/` — copies of ProvToolbox's JSON Schemas and of the library's index schema.

```
make serve      # http://127.0.0.1:8765/ — data-include needs http, not file:
make examples   # regenerate the primer's outputs with ProvToolbox (PROVTOOLBOX=~/IdeaProjects/ProvToolbox)
make validate   # check the primer's files against the JSON Schemas (needs ajv)
```

Work is tracked in [`TODO.md`](TODO.md) and [`DONE.md`](DONE.md).
