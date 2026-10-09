# Filter over the library's filtered provenance graph in PROV-JSON
# (target/prov-graph/library-templates.json, see prov_graph_filter.jq): the focus file and all its
# ancestors, i.e. the activity that generated it, the files that activity used, the activities that
# generated those, and so on, with the relations between them.
#
# Usage: jq --arg focus packing/packing.jsonld -f prov_graph_ancestors.jq library-templates.json

(if ($focus | startswith("file:")) then $focus else "file:" + $focus end) as $f
| if .entity[$f] == null then error("no entity " + $f) else . end

# edges from a node to its immediate ancestors: file -> generating activity, activity -> used file
| ([.wasGeneratedBy[] | [.["prov:entity"], .["prov:activity"]]]
   + [.used[] | [.["prov:activity"], .["prov:entity"]]]) as $edges

| def ancestors:
    . as $seen
    | ($seen + ([$edges[] | select($seen[.[0]]) | {key: .[1], value: true}] | from_entries)) as $next
    | if ($next | length) == ($seen | length) then $seen else $next | ancestors end;

  ({($f): true} | ancestors) as $keep

| .entity |= with_entries(select($keep[.key]))
| .activity |= with_entries(select($keep[.key]))
| .used |= with_entries(select($keep[.value["prov:activity"]] and $keep[.value["prov:entity"]]))
| .wasGeneratedBy |= with_entries(select($keep[.value["prov:entity"]] and $keep[.value["prov:activity"]]))
| .wasDerivedFrom |= with_entries(select($keep[.value["prov:generatedEntity"]] and $keep[.value["prov:usedEntity"]]))
