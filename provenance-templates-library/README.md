# provenance-templates
A library of provenance templates

## Provenance graph

Every template the build creates (each task of `src/main/resources/ttfs/config-*.json`) leaves a
PROV-CSV provenance record (`hasProvenance`). The build joins them into the library's provenance
graph in `target/prov-graph/`:

- `library.prov-csv`: all the records (process-resources);
- `library.provn`, `library.json`, `library.svg`: the records as PROV (`provconvert -log2prov
  org.openprovenance.prov.template.library.ptm.Init`, test phase); the SVG shows every format of
  every template;
- `library-templates.json`, `library-templates.svg`: the same graph through
  `scripts/prov_graph_filter.jq` (jq). It keeps one file per template (its `.jsonld`, else its
  `.provn`), drops the renderings and records, names each file by directory and local name, and
  drops the attributes. provconvert draws it, laid out right to left (prepare-package);
- `templates.dot`, `templates.svg`: one node per template (generated in yellow, hand-written in
  grey), written by `scripts/prov_graph.py`.

`scripts/prov_graph.py` also checks that every input an activity used is explained: either another
activity's output, or a hand-written template or bindings file. If one is not, it fails the build.
