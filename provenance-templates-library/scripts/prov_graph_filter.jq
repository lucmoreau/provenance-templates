# Filter over the library's provenance graph in PROV-JSON (target/prov-graph/library.json):
#  1. one file per template: its .jsonld, or its .provn when no .jsonld was produced; the
#     renderings (.svg, .png, .qualified.*), the provenance records (.prov-csv) and the bindings
#     (typed ptm:Bindings, or .json files: the library's own ptm templates leave inputs untyped) are
#     dropped, with the relations that mention them (any other file some activity used is kept);
#  2. each file named by its directory and local name only
#     (file:org/openprovenance/templates/triangles/triangle1-ugd/triangle1-ugd.jsonld
#      becomes file:triangle1-ugd/triangle1-ugd.jsonld);
#  3. attributes dropped (types, creation times): provconvert draws each as a note beside its
#     node;
#  4. activities dropped, with their generations and usages, and the activity of each derivation:
#     every file an activity used is a file its outputs derive from (ProvToolbox's ptm templates
#     and the library's own, from generic/product2-2), so the derivations alone carry the lineage.
#  All of it remains in library.json.
#
# Usage: jq -f prov_graph_filter.jq library.json > library-templates.json

def variant_suffix: "(\\.qualified)?\\.(provn|jsonld|png|svg)$";
def base: sub(variant_suffix; "");
def dropped: test("(\\.qualified\\.[a-z]+|\\.png|\\.svg|\\.prov-csv)$");
def shorten:
  if type == "string" and startswith("file:")
  then "file:" + (ltrimstr("file:") | split("/") | .[-2:] | join("/"))
  else . end;

([.entity | to_entries[]
  | select(([.value["prov:type"] | if type == "array" then .[] else . end | .["$"]?] | index("ptm:Bindings"))
           or (.key | endswith(".json")))
  | .key]) as $bindings
| ([.used[]?["prov:entity"]] | unique) as $used
| ([.entity | keys[] | select(dropped | not)]
   | group_by(base)
   | map((map(select(endswith(".jsonld"))) | .[0])
         // (map(select(endswith(".provn"))) | .[0])
         // .[0])) as $chosen
| (($chosen + $used - $bindings) | map({key: ., value: true}) | from_entries) as $keep

| .entity |= with_entries(select($keep[.key]))
| .wasGeneratedBy |= with_entries(select($keep[.value["prov:entity"]]))
| .used |= with_entries(select($keep[.value["prov:entity"]]))
| .wasDerivedFrom |= with_entries(select($keep[.value["prov:generatedEntity"]] and $keep[.value["prov:usedEntity"]]))

| .entity |= with_entries(.key |= shorten | .value = {})
| .wasDerivedFrom |= map_values(del(.["prov:activity"]) | map_values(shorten))
| del(.activity, .used, .wasGeneratedBy)
