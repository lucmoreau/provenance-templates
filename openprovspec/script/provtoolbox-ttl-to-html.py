#!/usr/bin/env python3
"""
provtoolbox-ttl-to-html.py — The ProvToolbox vocabulary (provtoolbox.ttl) as HTML.

Usage:
    python3 provtoolbox-ttl-to-html.py <provtoolbox.ttl> <output.html>
    python3 provtoolbox-ttl-to-html.py <provtoolbox.ttl> --inject <page.html>

The first form writes a standalone page, the published one.  The second
puts the vocabulary, as a ReSpec fragment, into a hand-written page at
the placeholder comment "insert here auto generated html describing the
ontology" -- see inject.py; rerunning it refreshes the block in place.

provtoolbox.ttl differs from openprov.ttl and template.ttl in spanning
three namespaces under one shared prefix (NamespacePrefixMapper's
SHARED_PROV_TOOLBOX_PREFIX): box, dot and sum.  The page has one section
per namespace, in the order of NAMESPACES below, and a term's card names
it with its own prefix.  A term of a kind or namespace the tables do not
foresee is still shown -- under its namespace in name order, or under
"Other terms" -- so a new one is never lost.
"""

import importlib
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
base = importlib.import_module("ttl-to-html")   # the helpers of the openprov generator
from inject import inject, TERM_CSS

RDF, RDFS, OWL, URIRef = base.RDF, base.RDFS, base.OWL, base.URIRef
esc, local_name, get_label, get_comment, get_literal = (
    base.esc, base.local_name, base.get_label, base.get_comment, base.get_literal)

SHARED_PREFIX = "http://openprovenance.org/provtoolbox/"
ONTOLOGY = URIRef(SHARED_PREFIX)

# The namespaces, in page order: (section id, prefix, namespace, title, intro, local names in order).
NAMESPACES = [
    ("ns-box", "box", SHARED_PREFIX + "ns#", "The toolbox namespace",
     "ProvToolbox's own namespace (<code>NamespacePrefixMapper.TOOLBOX_NS</code>): what the "
     "toolbox names for itself rather than for a document.",
     ["UNKNOWN_TYPE"]),
    ("ns-dot", "dot", SHARED_PREFIX + "dot/ns#", "The dot namespace",
     "Rendering hints (<code>NamespacePrefixMapper.DOT_NS</code>): an attribute on any statement, "
     "element or relation, overriding the default the visualiser would pick for its node or edge. "
     "Values are Graphviz's; the Mermaid and SVG passes read them too, and the first value counts "
     "when several are given.",
     ["fillcolor", "color", "fontcolor", "style", "url", "size", "tooltip"]),
    ("ns-sum", "sum", SHARED_PREFIX + "summary/ns#", "The summary namespace",
     "What the summariser stamps on a summary graph (<code>NamespacePrefixMapper.SUMMARY_NS</code>): "
     "each node stands for the instance nodes sharing a provenance type at the chosen level, each "
     "edge for the instance edges between two such classes, and these figures say how many.",
     ["size", "nbr", "count", "level0"]),
]
PREFIX_OF = {ns: prefix for _, prefix, ns, _, _, _ in NAMESPACES}

# The rows of a term's table, in order; the kind is shown, since it varies between namespaces.
ROWS = [
    ("rdf:type",     RDF.type,     ()),
    ("rdfs:domain",  RDFS.domain,  ()),
    ("rdfs:range",   RDFS.range,   ()),
    ("rdfs:seeAlso", RDFS.seeAlso, ()),
]


def split(iri: str):
    """(prefix, local name) of an IRI in one of the three namespaces, else None."""
    for ns, prefix in PREFIX_OF.items():
        if iri.startswith(ns):
            return prefix, iri[len(ns):]
    return None


base_curie = base.curie


def curie(iri: str) -> str:
    """A prefixed name for the three namespaces, else the openprov generator's."""
    hit = split(iri)
    return f"{hit[0]}:{hit[1]}" if hit else base_curie(iri)


base.curie = curie    # class_expr() and values() read the module global


def anchor(text: str) -> str:
    """Wrap each curie of this vocabulary in a link to its card on the page."""
    out = []
    for part in text.split(", "):
        prefix, _, ln = part.partition(":")
        if prefix in PREFIX_OF.values() and ln:
            out.append(f'<a href="#{esc(prefix)}-{esc(ln)}">{esc(part)}</a>')
        else:
            out.append(esc(part))
    return ", ".join(out)


def term_div(g, term) -> str:
    prefix, ln = split(str(term))
    label, comment, iri = get_label(g, term), get_comment(g, term), str(term)
    rows = [f'      <tr><th>rdfs:label</th><td>{esc(label)}</td></tr>']
    if comment:
        rows.append(f'      <tr><th>rdfs:comment</th><td>{esc(comment)}</td></tr>')
    for heading, predicate, exclude in ROWS:
        v = base.values(g, term, predicate, exclude)
        if v:
            rows.append(f'      <tr><th>{heading}</th><td><code>{anchor(v)}</code></td></tr>')
    return (
        f'    <div class="term" id="{esc(prefix)}-{esc(ln)}">\n'
        f'      <div class="term-name"><dfn class="export">{esc(prefix)}:{esc(ln)}</dfn>'
        f'<span class="term-iri"><a href="{esc(iri)}">{esc(iri)}</a></span></div>\n'
        f'      <table class="facts">\n' + "\n".join(rows) + '\n      </table>\n'
        f'    </div>\n'
    )


def load(ttl_path: str):
    """The graph, the ontology node, and the terms of the vocabulary by namespace."""
    g = base.Graph()
    g.parse(ttl_path, format="turtle")
    ontology = ONTOLOGY if (ONTOLOGY, RDF.type, OWL.Ontology) in g else next(
        (o for o in g.subjects(RDF.type, OWL.Ontology) if str(o).startswith(SHARED_PREFIX)), ONTOLOGY)
    # Every typed subject under the shared prefix, whatever its kind: property, datatype, class.
    terms = {str(t): t for t in g.subjects(RDF.type, None)
             if isinstance(t, URIRef) and str(t).startswith(SHARED_PREFIX) and t != ontology}
    grouped = []
    for sid, prefix, ns, title, intro, names in NAMESPACES:
        found = [terms.pop(ns + n) for n in names if ns + n in terms]
        rest = sorted((k for k in terms if k.startswith(ns)), key=lambda k: local_name(k).lower())
        found += [terms.pop(k) for k in rest]
        if found:
            grouped.append((sid, prefix, ns, title, intro, found))
    if terms:   # a namespace under the shared prefix the table does not name
        grouped.append(("ns-other", "", "", "Other terms",
                        "Terms under the shared prefix in a namespace the sections above do not name.",
                        [terms[k] for k in sorted(terms, key=str.lower)]))
    return g, ontology, grouped


def card(g, term) -> str:
    """A term's card; one outside the three namespaces is named by its IRI."""
    if split(str(term)):
        return term_div(g, term)
    iri = str(term)
    return (f'    <div class="term" id="{esc(local_name(iri))}">\n'
            f'      <div class="term-name"><dfn class="export">{esc(iri)}</dfn></div>\n'
            f'      <table class="facts">\n'
            f'      <tr><th>rdfs:label</th><td>{esc(get_label(g, term))}</td></tr>\n'
            f'      <tr><th>rdfs:comment</th><td>{esc(get_comment(g, term))}</td></tr>\n'
            f'      </table>\n    </div>\n')


def fragment(ttl_path: str) -> str:
    """The vocabulary as ReSpec subsections: one per namespace."""
    g, ontology, grouped = load(ttl_path)
    desc = get_literal(g, ontology, RDFS.comment)
    prefixes = "".join(
        f'  <tr><td><code>{esc(prefix)}</code></td><td><code>{esc(ns)}</code></td></tr>\n'
        for _, prefix, ns, _, _, _ in NAMESPACES)
    sections = "".join(
        f'<section id="{sid}">\n  <h3>{esc(title)}'
        + (f' <code class="ns-prefix">{esc(prefix)}</code>' if prefix else '')
        + f'</h3>\n'
        + (f'  <p>Namespace <code>{esc(ns)}</code>, prefix <code>{esc(prefix)}</code>. {intro}</p>\n'
           if ns else f'  <p>{intro}</p>\n')
        + "".join(card(g, t) for t in terms) + '</section>\n\n'
        for sid, prefix, ns, title, intro, terms in grouped)
    return f"""\
<style>
{TERM_CSS}h3 code.ns-prefix {{ font-size: 0.7em; font-weight: normal; color: #555; margin-left: 0.6em; }}
</style>
<p>The vocabulary is identified by <code>{esc(str(ontology))}</code>, the prefix its three
namespaces share.
{esc(desc)}</p>
<table class="prefixes">
{prefixes}</table>
<p>The kind of each term is shown on its card: an attribute is an <code>rdf:Property</code>,
paired with a value on a statement; the one datatype is an <code>rdfs:Datatype</code>.</p>

{sections}"""


def standalone(ttl_path: str, html_path: str) -> None:
    g, ontology, grouped = load(ttl_path)
    title = get_literal(g, ontology, RDFS.label) or "ProvToolbox vocabulary"
    toc = "".join(
        f'      <li><a href="#{sid}">{esc(t)}</a><ul>\n'
        + "".join(f'        <li><a href="#{esc(prefix)}-{esc(local_name(x))}">{esc(prefix)}:{esc(local_name(x))}</a></li>\n'
                  if prefix else
                  f'        <li><a href="#{esc(local_name(x))}">{esc(str(x))}</a></li>\n'
                  for x in terms)
        + '      </ul></li>\n'
        for sid, prefix, _, t, _, terms in grouped)
    body = fragment(ttl_path).replace("<h3>", "<h2>").replace("</h3>", "</h2>")
    Path(html_path).write_text(f"""\
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>{esc(title)}</title>
  <style>
{base.PAGE_CSS}  </style>
</head>
<body>
  <h1>{esc(title)}</h1>
  <p class="subtitle">Shared prefix: <code>{esc(SHARED_PREFIX)}</code> &nbsp; Prefixes: {", ".join(f"<code>{esc(p)}</code>" for _, p, *_ in NAMESPACES)}</p>
  <nav id="toc">
    <h2>Table of Contents</h2>
    <ul>
{toc}    </ul>
  </nav>
{body}</body>
</html>
""", encoding="utf-8")
    print(f"Written: {html_path}")


if __name__ == "__main__":
    args = sys.argv[1:]
    injecting = "--inject" in args
    if injecting:
        args.remove("--inject")
    if len(args) != 2:
        print(f"Usage: python3 {sys.argv[0]} <provtoolbox.ttl> <output.html>|--inject <page.html>")
        sys.exit(1)
    if injecting:
        inject(args[1], "ontology", "insert here auto generated html describing the ontology",
               Path(args[0]).name, Path(sys.argv[0]).name, fragment(args[0]))
    else:
        standalone(args[0], args[1])
