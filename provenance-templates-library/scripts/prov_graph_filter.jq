# Filter over the library's provenance graph in PROV-JSON (target/prov-graph/library.json):
#  1. one file per template: its .jsonld, or its .provn when no .jsonld was produced; the
#     renderings (.svg, .png, .qualified.*) and the provenance records (.prov-csv) are dropped,
#     with the relations that mention them (a file some activity used is always kept);
#  2. each file named by its directory and local name only
#     (file:org/openprovenance/templates/triangles/triangle1-ugd/triangle1-ugd.jsonld
#      becomes file:triangle1-ugd/triangle1-ugd.jsonld);
#  3. attributes dropped (types, creation times): provconvert draws each as a note beside its
#     node. They remain in library.json.
#
# Usage: jq -f prov_graph_filter.jq library.json > library-templates.json

def variant_suffix: "(\\.qualified)?\\.(provn|jsonld|png|svg)$";
def base: sub(variant_suffix; "");
def dropped: test("(\\.qualified\\.[a-z]+|\\.png|\\.svg|\\.prov-csv)$");
def shorten:
  if type == "string" and startswith("file:")
  then "file:" + (ltrimstr("file:") | split("/") | .[-2:] | join("/"))
  else . end;

([.used[]?["prov:entity"]] | unique) as $used
| ([.entity | keys[] | select(dropped | not)]
   | group_by(base)
   | map((map(select(endswith(".jsonld"))) | .[0])
         // (map(select(endswith(".provn"))) | .[0])
         // .[0])) as $chosen
| (($chosen + $used) | map({key: ., value: true}) | from_entries) as $keep

| .entity |= with_entries(select($keep[.key]))
| .wasGeneratedBy |= with_entries(select($keep[.value["prov:entity"]]))
| .used |= with_entries(select($keep[.value["prov:entity"]]))
| .wasDerivedFrom |= with_entries(select($keep[.value["prov:generatedEntity"]] and $keep[.value["prov:usedEntity"]]))

| .entity |= with_entries(.key |= shorten | .value = {})
| .activity |= map_values({})
| .used |= map_values(map_values(shorten))
| .wasGeneratedBy |= map_values(map_values(shorten))
| .wasDerivedFrom |= map_values(map_values(shorten))
