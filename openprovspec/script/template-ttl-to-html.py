#!/usr/bin/env python3
"""
template-ttl-to-html.py — The provenance template vocabulary (template.ttl) as HTML.

Usage:
    python3 template-ttl-to-html.py <template.ttl> --inject <template.html>
    python3 template-ttl-to-html.py <template.ttl> <output.html>

The first form puts the vocabulary, as a ReSpec fragment, into the
specification's ontology section at the placeholder comment "insert here
auto generated html describing the ontology" -- see inject.py; rerunning it
refreshes the block in place.  The second writes a standalone page, to check
the generator.

template.ttl differs from openprov.ttl in what it declares: every term is an
rdf:Property, an attribute a template pairs with a variable and the
instantiation consumes, and there are no roles or classes.  The terms are
presented in the groups the file itself comments them under; a term the
groups do not name goes under "Other attributes", so a new one is never lost.
"""

import importlib
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
base = importlib.import_module("ttl-to-html")   # the helpers of the openprov generator
from inject import inject, TERM_CSS

RDF, RDFS, OWL, URIRef = base.RDF, base.RDFS, base.OWL, base.URIRef
esc, local_name, get_label, get_comment, get_literal, values, anchor = (
    base.esc, base.local_name, base.get_label, base.get_comment, base.get_literal, base.values, base.anchor)

VOCAB_NS = "http://openprovenance.org/ns/tmpl#"
VOCAB_PREFIX = "tmpl"
base.VOCAB_NS, base.VOCAB_PREFIX = VOCAB_NS, VOCAB_PREFIX   # curie() and anchor() read these

# The groups, in the order of template.ttl: (section id, title, intro, local names).
GROUPS = [
    ("tmpl-syntax", "Syntax-constrained attributes",
     "Paired with a variable in the template; the instantiation writes the PROV attribute — "
     "<code>prov:label</code>, <code>prov:time</code>, <code>prov:startTime</code>, "
     "<code>prov:endTime</code> — with the bound value in its place, since PROV admits no "
     "qualified name there.",
     ["label", "time", "startTime", "endTime"]),
    ("tmpl-conditional", "Conditional instantiation",
     "Whether the statement is instantiated at all depends on the variable being bound.",
     ["if", "notif"]),
    ("tmpl-expansion", "Identifiers and expansion",
     "An identifier for a relation written without one, and the pairing of variables that "
     "expand in step.",
     ["ID", "linked"]),
]

# The rows of a term's table, in order; rdf:Property goes without saying.
ROWS = [
    ("rdf:type",     RDF.type,     ("rdf:Property",)),
    ("rdfs:domain",  RDFS.domain,  ()),
    ("rdfs:range",   RDFS.range,   ()),
    ("rdfs:seeAlso", RDFS.seeAlso, ()),
]


def term_div(g, term) -> str:
    ln, label, comment, iri = local_name(term), get_label(g, term), get_comment(g, term), str(term)
    rows = [f'      <tr><th>rdfs:label</th><td>{esc(label)}</td></tr>']
    if comment:
        rows.append(f'      <tr><th>rdfs:comment</th><td>{esc(comment)}</td></tr>')
    for heading, predicate, exclude in ROWS:
        v = values(g, term, predicate, exclude)
        if v:
            rows.append(f'      <tr><th>{heading}</th><td><code>{anchor(g, v)}</code></td></tr>')
    return (
        f'    <div class="term" id="{esc(ln)}">\n'
        f'      <div class="term-name"><dfn class="export">{VOCAB_PREFIX}:{esc(ln)}</dfn>'
        f'<span class="term-iri"><a href="{esc(iri)}">{esc(iri)}</a></span></div>\n'
        f'      <table class="facts">\n' + "\n".join(rows) + '\n      </table>\n'
        f'    </div>\n'
    )


def load(ttl_path: str):
    """The graph, the ontology node, and the terms of the vocabulary in their groups."""
    g = base.Graph()
    g.parse(ttl_path, format="turtle")
    ontology = next((o for o in (URIRef(VOCAB_NS), URIRef(VOCAB_NS.rstrip("#")))
                     if (o, RDF.type, OWL.Ontology) in g), URIRef(VOCAB_NS))
    terms = {local_name(t): t for t in g.subjects(RDF.type, RDF.Property) if str(t).startswith(VOCAB_NS)}
    grouped = []
    for sid, title, intro, names in GROUPS:
        found = [terms.pop(n) for n in names if n in terms]
        if found:
            grouped.append((sid, title, intro, found))
    if terms:
        grouped.append(("tmpl-other", "Other attributes", "Attributes the groups above do not name.",
                        [terms[n] for n in sorted(terms, key=str.lower)]))
    return g, ontology, grouped


def fragment(ttl_path: str) -> str:
    """The vocabulary as ReSpec subsections, for template.html's ontology section."""
    g, ontology, grouped = load(ttl_path)
    desc = get_literal(g, ontology, RDFS.comment)
    sections = "".join(
        f'<section id="{sid}">\n  <h3>{esc(title)}</h3>\n  <p>{intro}</p>\n'
        + "".join(term_div(g, t) for t in terms) + '</section>\n\n'
        for sid, title, intro, terms in grouped)
    return f"""\
<style>
{TERM_CSS}</style>
<p>The vocabulary is identified by <code>{esc(str(ontology))}</code>; its terms are in the
namespace <code>{esc(VOCAB_NS)}</code>, prefix <code>{esc(VOCAB_PREFIX)}</code>.
{esc(desc)}</p>
<p>Each term is declared as <code>rdf:Property</code>: an attribute a template pairs with a variable,
and which the instantiation consumes.</p>

{sections}"""


def standalone(ttl_path: str, html_path: str) -> None:
    g, ontology, grouped = load(ttl_path)
    title = get_literal(g, ontology, RDFS.label) or "Provenance template vocabulary"
    toc = "".join(
        f'      <li><a href="#{sid}">{esc(t)}</a><ul>\n'
        + "".join(f'        <li><a href="#{esc(local_name(x))}">{VOCAB_PREFIX}:{esc(local_name(x))}</a></li>\n' for x in terms)
        + '      </ul></li>\n'
        for sid, t, _, terms in grouped)
    body = fragment(ttl_path).replace("<h3>", "<h2>").replace("</h3>", "</h2>")
    out = Path(html_path)
    out.write_text(f"""\
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
  <p class="subtitle">Namespace: <code>{esc(VOCAB_NS)}</code> &nbsp; Prefix: <code>{VOCAB_PREFIX}</code></p>
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
        print(f"Usage: python3 {sys.argv[0]} <template.ttl> <output.html>|--inject <template.html>")
        sys.exit(1)
    if injecting:
        inject(args[1], "ontology", "insert here auto generated html describing the ontology",
               Path(args[0]).name, Path(sys.argv[0]).name, fragment(args[0]))
    else:
        standalone(args[0], args[1])
