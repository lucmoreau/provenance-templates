# Filter over the library's filtered provenance graph in PROV-JSON
# (target/prov-graph/library-templates.json, see prov_graph_filter.jq): the focus file and all its
# ancestors, i.e. the files it derives from, the files those derive from, and so on, with the
# derivations between them.
#
# Usage: jq --arg focus packing/packing.jsonld -f prov_graph_ancestors.jq library-templates.json

(if ($focus | startswith("file:")) then $focus else "file:" + $focus end) as $f
| if .entity[$f] == null then error("no entity " + $f) else . end

# edges from a file to the files it derives from
| [.wasDerivedFrom[] | [.["prov:generatedEntity"], .["prov:usedEntity"]]] as $edges

| def ancestors:
    . as $seen
    | ($seen + ([$edges[] | select($seen[.[0]]) | {key: .[1], value: true}] | from_entries)) as $next
    | if ($next | length) == ($seen | length) then $seen else $next | ancestors end;

  ({($f): true} | ancestors) as $keep

| .entity |= with_entries(select($keep[.key]))
| .wasDerivedFrom |= with_entries(select($keep[.value["prov:generatedEntity"]] and $keep[.value["prov:usedEntity"]]))
