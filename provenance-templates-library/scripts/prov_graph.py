#!/usr/bin/env python3
"""The library's provenance graph, from its templates' provenance records.

Input: the PROV-JSON that `provconvert -log2prov org.openprovenance.prov.template.library.ptm.Init`
makes of all the library's PROV-CSV records joined (the build's target/prov-graph/library.json).

It checks that every input an activity used is explained: either the output of another activity,
or a file that exists under one of the given roots (a hand-written template, a bindings file).
It prints a summary, writes the template-level view as DOT (one node per template, its format
variants .provn/.jsonld/.png/.svg folded together; records and bindings left out), and exits
with status 1 if any input is unexplained.

Usage: prov_graph.py LIBRARY_JSON OUTPUT_DOT ROOT [ROOT ...]
"""

import collections
import json
import os
import re
import sys

FORMAT_SUFFIX = re.compile(r'(\.qualified)?\.(provn|jsonld|png|svg)$')
TEMPLATES_PREFIX = 'org/openprovenance/templates/'


def as_list(value):
    if value is None:
        return []
    return value if isinstance(value, list) else [value]


def types_of(element):
    return {t['$'] if isinstance(t, dict) else str(t) for t in as_list(element.get('prov:type'))}


def path_of(qualified_name):
    return qualified_name.split(':', 1)[1]


def template_node(entity_id, entities):
    """The template an entity is a format variant of, or None for records and bindings."""
    types = types_of(entities.get(entity_id, {}))
    if 'ptm:CompactBindings' in types or 'ptm:Bindings' in types:
        return None
    return FORMAT_SUFFIX.sub('', path_of(entity_id)).replace(TEMPLATES_PREFIX, '', 1)


def components(nodes, edges):
    parent = {n: n for n in nodes}

    def find(x):
        while parent[x] != x:
            parent[x] = parent[parent[x]]
            x = parent[x]
        return x

    for a, b in edges:
        parent[find(a)] = find(b)
    return sorted(collections.Counter(find(n) for n in nodes).values(), reverse=True)


def main(library_json, output_dot, roots):
    doc = json.load(open(library_json))
    entities = doc.get('entity', {})
    activities = doc.get('activity', {})
    used = [(u['prov:activity'], u['prov:entity']) for u in doc.get('used', {}).values()]
    generated = [(g['prov:entity'], g['prov:activity']) for g in doc.get('wasGeneratedBy', {}).values()]

    outputs = {e for e, _ in generated}
    kinds = collections.Counter()
    unexplained = []
    for _, e in used:
        if e in outputs:
            kinds['output of another activity'] += 1
        elif any(os.path.exists(os.path.join(root, path_of(e))) for root in roots):
            kinds['bindings' if 'ptm:Bindings' in types_of(entities.get(e, {})) else 'hand-written template'] += 1
        else:
            unexplained.append(e)

    parts = components(set(entities) | set(activities), generated + used)
    print('prov graph: %d activities, %d entities, %d components %s' % (len(activities), len(entities), len(parts), parts))
    print('prov graph: inputs used: %s, unexplained: %d' % (dict(kinds), len(unexplained)))
    for e in sorted(set(unexplained)):
        print('prov graph: UNEXPLAINED input %s' % e)

    gen_edges = {(t, a) for e, a in generated if (t := template_node(e, entities))}
    used_edges = {(a, t) for a, e in used if (t := template_node(e, entities))}
    made = {t for t, _ in gen_edges}
    templates = made | {t for _, t in used_edges}

    lines = ['digraph library {',
             '  rankdir=LR; nodesep=0.08; ranksep=0.5;',
             '  node [fontname="Helvetica", fontsize=9]; edge [arrowsize=0.5];']
    for t in sorted(templates):
        fill = '#FFFC87' if t in made else '#E8E8E8'  # generated / hand-written
        lines.append('  "%s" [shape=ellipse, style=filled, fillcolor="%s", color="#808080"];' % (t, fill))
    for a, attributes in sorted(activities.items()):
        merging = any('Merging' in t for t in types_of(attributes))
        lines.append('  "%s" [shape=box, style=filled, fillcolor="%s", color="#0000FF", label="%s"];'
                     % (a, '#C9D3FD' if merging else '#9FB1FC', 'merge' if merging else 'expand'))
    lines += ['  "%s" -> "%s" [color="darkred"];' % edge for edge in sorted(gen_edges)]
    lines += ['  "%s" -> "%s" [color="#A0A0A0"];' % edge for edge in sorted(used_edges)]
    lines.append('}')
    with open(output_dot, 'w') as out:
        out.write('\n'.join(lines) + '\n')
    print('prov graph: %d templates (%d generated, %d hand-written) -> %s'
          % (len(templates), len(made), len(templates - made), output_dot))

    return 1 if unexplained else 0


if __name__ == '__main__':
    if len(sys.argv) < 4:
        sys.exit(__doc__)
    sys.exit(main(sys.argv[1], sys.argv[2], sys.argv[3:]))
