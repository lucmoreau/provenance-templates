#!/usr/bin/env python3
"""Check of the library's provenance graph, from its templates' provenance records.

Input: the PROV-JSON that `provconvert -log2prov org.openprovenance.prov.template.library.ptm.Init`
makes of all the library's PROV-CSV records joined (the build's target/prov-graph/library.json).

It checks that every input an activity used is explained: either the output of another activity,
or a file that exists under one of the given roots (a hand-written template, a bindings file).
It prints a summary and exits with status 1 if any input is unexplained.

Usage: prov_graph.py LIBRARY_JSON ROOT [ROOT ...]
"""

import collections
import json
import os
import sys


def as_list(value):
    if value is None:
        return []
    return value if isinstance(value, list) else [value]


def types_of(element):
    return {t['$'] if isinstance(t, dict) else str(t) for t in as_list(element.get('prov:type'))}


def path_of(qualified_name):
    return qualified_name.split(':', 1)[1]


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


def main(library_json, roots):
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
            bindings = 'ptm:Bindings' in types_of(entities.get(e, {})) or e.endswith('.json')
            kinds['bindings' if bindings else 'hand-written template'] += 1
        else:
            unexplained.append(e)

    parts = components(set(entities) | set(activities), generated + used)
    print('prov graph: %d activities, %d entities, %d components %s' % (len(activities), len(entities), len(parts), parts))
    print('prov graph: inputs used: %s, unexplained: %d' % (dict(kinds), len(unexplained)))
    for e in sorted(set(unexplained)):
        print('prov graph: UNEXPLAINED input %s' % e)

    return 1 if unexplained else 0


if __name__ == '__main__':
    if len(sys.argv) < 3:
        sys.exit(__doc__)
    sys.exit(main(sys.argv[1], sys.argv[2:]))
