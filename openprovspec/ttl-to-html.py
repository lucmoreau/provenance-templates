#!/usr/bin/env python3
"""
ttl-to-html.py — Generate a W3C-style HTML vocabulary spec from a Turtle file.

Usage:
    python3 ttl-to-html.py <input.ttl> <output.html> [<namespace> <prefix>]
    python3 ttl-to-html.py <input.ttl> --inject <index.html> [<namespace> <prefix>]

The first form writes a standalone page.  The second puts the same terms,
as a ReSpec fragment, into the specification's ontology section, at the
placeholder comment "insert here auto generated html describing the
ontology" -- see inject.py; rerunning it refreshes the block in place.

The namespace/prefix pair defaults to the openprov vocabulary.  The ontology
header is the namespace IRI itself (with or without its trailing '#'), and
its rdfs:label / rdfs:comment (or dct:title / dct:description) give the
page its title and introduction.

Terms in the namespace are presented in three sections, following the
shape of openprov.ttl:

    object properties   owl:ObjectProperty, grouped by rdfs:domain, with
                        their domain, range and super-property;
    roles               individuals typed prov:Role;
    types               owl:Class, with their super-class.
"""

import sys

try:
    from rdflib import Graph, Namespace, RDF, RDFS, OWL, URIRef, Literal
except ImportError:
    print("Error: rdflib is not installed.")
    print("Install it with:  pip install rdflib")
    sys.exit(1)

import html
from pathlib import Path

from inject import inject, TERM_CSS

# ── Namespaces ────────────────────────────────────────────────────────────────

PROV    = Namespace("http://www.w3.org/ns/prov#")
DCT     = Namespace("http://purl.org/dc/terms/")
PROVEXT = Namespace("https://openprovenance.org/ns/provext#")
XSD     = Namespace("http://www.w3.org/2001/XMLSchema#")

# The vocabulary being rendered — overridden from argv for acct/sales.
VOCAB_NS     = "https://openprovenance.org/ns/openprov#"
VOCAB_PREFIX = "openprov"


# ── Helpers ───────────────────────────────────────────────────────────────────

def local_name(term: URIRef) -> str:
    """Return the local name after the last # or / in the IRI."""
    s = str(term)
    for sep in ("#", "/"):
        idx = s.rfind(sep)
        if idx != -1:
            return s[idx + 1:]
    return s


def get_literal(g: Graph, subject, predicate, lang="en") -> str:
    """Return the string value of the first matching literal (preferring @en)."""
    best = ""
    for obj in g.objects(subject, predicate):
        if isinstance(obj, Literal):
            if obj.language == lang:
                return str(obj)
            if not best:
                best = str(obj)
    return best


def get_label(g: Graph, term: URIRef) -> str:
    label = get_literal(g, term, RDFS.label)
    return label if label else local_name(term)


def get_comment(g: Graph, term: URIRef) -> str:
    return get_literal(g, term, RDFS.comment)


def esc(s: str) -> str:
    return html.escape(s)


def curie(iri: str) -> str:
    """Return a prefixed name if it falls within a known namespace, else full IRI."""
    if iri.startswith(VOCAB_NS):
        return VOCAB_PREFIX + ":" + iri[len(VOCAB_NS):]
    if iri.startswith(str(PROV)):
        return "prov:" + iri[len(str(PROV)):]
    if iri.startswith(str(PROVEXT)):
        return "provext:" + iri[len(str(PROVEXT)):]
    if iri.startswith(str(XSD)):
        return "xsd:" + iri[len(str(XSD)):]
    if iri.startswith(str(RDF)):
        return "rdf:" + iri[len(str(RDF)):]
    if iri.startswith(str(OWL)):
        return "owl:" + iri[len(str(OWL)):]
    if iri.startswith(str(RDFS)):
        return "rdfs:" + iri[len(str(RDFS)):]
    return iri


# ── Term HTML ─────────────────────────────────────────────────────────────────

def class_expr(g: Graph, node) -> str:
    """A class expression as text: a curie, or the members of an owl:unionOf."""
    if isinstance(node, URIRef):
        return curie(str(node))
    members = []
    for union in g.objects(node, OWL.unionOf):
        members = [class_expr(g, m) for m in g.items(union)]
    return " ∪ ".join(members) if members else "…"


def values(g: Graph, term: URIRef, predicate, exclude=()) -> str:
    """The objects of (term, predicate) as sorted, comma-separated text."""
    vals = sorted(class_expr(g, o) for o in g.objects(term, predicate))
    return ", ".join(v for v in vals if v not in exclude)


def anchor(g: Graph, text: str) -> str:
    """Wrap each curie of this vocabulary in a link to its term on the page."""
    out = []
    for part in text.split(", "):
        if part.startswith(VOCAB_PREFIX + ":"):
            ln = part[len(VOCAB_PREFIX) + 1:]
            out.append(f'<a href="#{esc(ln)}">{esc(part)}</a>')
        else:
            out.append(esc(part))
    return ", ".join(out)


# The rows a term's table may carry, in order: (heading, predicate, values
# not worth showing because the section already says so).
ROWS = [
    ("rdf:type",           RDF.type,           ("owl:Class", "owl:ObjectProperty")),
    ("rdfs:subClassOf",    RDFS.subClassOf,    ()),
    ("rdfs:subPropertyOf", RDFS.subPropertyOf, ()),
    ("rdfs:domain",        RDFS.domain,        ()),
    ("rdfs:range",         RDFS.range,         ()),
]


def generate_term_div(g: Graph, term: URIRef) -> str:
    """A term as a card: its name (a definition), IRI, and a table of facts."""
    ln       = local_name(term)
    label    = get_label(g, term)
    comment  = get_comment(g, term)
    full_iri = str(term)

    rows = []
    if label:
        rows.append(f'      <tr><th>rdfs:label</th><td>{esc(label)}</td></tr>')
    if comment:
        rows.append(f'      <tr><th>rdfs:comment</th><td>{esc(comment)}</td></tr>')
    for heading, predicate, exclude in ROWS:
        v = values(g, term, predicate, exclude)
        if v:
            rows.append(f'      <tr><th>{heading}</th><td><code>{anchor(g, v)}</code></td></tr>')

    table_rows = "\n".join(rows)
    return (
        f'    <div class="term" id="{esc(ln)}">\n'
        f'      <div class="term-name"><dfn class="export">{esc(VOCAB_PREFIX)}:{esc(ln)}</dfn>'
        f'<span class="term-iri"><a href="{esc(full_iri)}">{esc(full_iri)}</a></span></div>\n'
        f'      <table class="facts">\n'
        f'{table_rows}\n'
        f'      </table>\n'
        f'    </div>\n'
    )


def generate_terms(g: Graph, terms, groups=None) -> str:
    """The cards of a section; with groups, under an italic line per group."""
    if groups:
        return "".join(f'    <p class="group">{esc(gtitle)}</p>\n' +
                       "".join(generate_term_div(g, t) for t in gterms)
                       for gtitle, gterms in groups)
    return "".join(generate_term_div(g, t) for t in terms)


def generate_section(g: Graph, sid: str, title: str, intro: str, terms, groups=None) -> str:
    """A section of terms; with groups, a list of (group title, terms) subsections."""
    body = generate_terms(g, terms, groups)
    return (
        f'  <section id="{sid}">\n'
        f'    <h2>{esc(title)}</h2>\n'
        f'    <p>{intro}</p>\n'
        f'{body}'
        f'  </section>\n'
    )


def toc_entries(terms) -> str:
    return "".join(
        f'        <li><a href="#{esc(local_name(t))}">{esc(VOCAB_PREFIX)}:{esc(local_name(t))}</a></li>\n'
        for t in terms
    )


# ── CSS ───────────────────────────────────────────────────────────────────────

PAGE_CSS = """\\
    * { box-sizing: border-box; }
    body {
      font-family: Georgia, serif;
      font-size: 16px;
      line-height: 1.6;
      color: #222;
      background: #fff;
      max-width: 900px;
      margin: 0 auto;
      padding: 2em 1.5em 4em;
    }
    h1 { font-size: 2em; border-bottom: 2px solid #4285f4; padding-bottom: 0.3em; }
    h2 { font-size: 1.4em; border-bottom: 1px solid #ccc; padding-bottom: 0.2em; margin-top: 2em; }
    p.subtitle { font-size: 0.95em; color: #555; margin-top: -0.5em; }
    code { font-family: monospace; font-size: 0.95em; background: #f5f5f5; padding: 0.1em 0.3em; border-radius: 3px; }
    a { color: #1a73e8; text-decoration: none; }
    a:hover { text-decoration: underline; }
    nav#toc { background: #f8f9fa; border: 1px solid #ddd; padding: 1em 1.5em; margin: 1.5em 0; display: inline-block; min-width: 200px; }
    nav#toc h2 { margin-top: 0; border: none; font-size: 1.1em; }
    nav#toc ul { margin: 0; padding-left: 1.2em; }
    nav#toc ul ul { font-size: 0.9em; }
    nav#toc li { margin: 0.2em 0; }
    section { margin-top: 2em; }
    section > p { margin-top: 0.3em; color: #444; font-size: 0.95em; }
    table.prefixes td { padding: 0.1em 1em 0.1em 0; }
"""
CSS = PAGE_CSS + TERM_CSS


# ── Main ──────────────────────────────────────────────────────────────────────

def load(ttl_path: str):
    """The graph, its ontology node, and the terms of this vocabulary by kind."""
    g = Graph()
    g.parse(ttl_path, format="turtle")
    ontology_iri = next((o for o in (URIRef(VOCAB_NS), URIRef(VOCAB_NS.rstrip("#")))
                         if (o, RDF.type, OWL.Ontology) in g), URIRef(VOCAB_NS))

    def ours(subjects):
        return sorted({t for t in subjects if str(t).startswith(VOCAB_NS)},
                      key=lambda t: local_name(t).lower())

    prop_terms = ours(g.subjects(RDF.type, OWL.ObjectProperty))
    role_terms = ours(g.subjects(RDF.type, PROV.Role))
    type_terms = ours(g.subjects(RDF.type, OWL.Class))
    by_domain = {}
    for t in prop_terms:
        d = values(g, t, RDFS.domain) or "(no domain)"
        by_domain.setdefault(d, []).append(t)
    groups = [(f"On {d.replace(' ∪ ', ' and ')}", ts) for d, ts in by_domain.items()]
    return g, ontology_iri, prop_terms, role_terms, type_terms, groups


def generate_fragment(ttl_path: str) -> str:
    """The ontology as ReSpec sections, for the specification's ontology section."""
    g, ontology_iri, prop_terms, role_terms, type_terms, groups = load(ttl_path)
    ont_desc = (get_literal(g, ontology_iri, RDFS.comment)
                or get_literal(g, ontology_iri, DCT.description))
    imports = sorted(str(o) for o in g.objects(ontology_iri, OWL.imports))
    imports_html = "".join(
        f'<p>The ontology imports <code>{esc(i)}</code>.</p>\n' for i in imports)
    return f"""\
<p>The ontology is identified by <code>{esc(str(ontology_iri))}</code>; its terms are in the
namespace <code>{esc(VOCAB_NS)}</code>, prefix <code>{esc(VOCAB_PREFIX)}</code>.
{esc(ont_desc)}</p>
{imports_html}
<section id="ontology-properties">
  <h3>Object Properties</h3>
  <p>Each property is declared as <code>owl:ObjectProperty</code>; it is optional on the
  relation named by its domain and points at a statement that relation summarises.</p>
{generate_terms(g, prop_terms, groups)}</section>

<section id="ontology-roles">
  <h3>Roles</h3>
  <p>Each role is an individual of type <code>prov:Role</code>, for use as the
  <code>prov:role</code> of a usage or generation.</p>
{generate_terms(g, role_terms)}</section>

<section id="ontology-types">
  <h3>Types</h3>
  <p>Each type is declared as <code>owl:Class</code>, for use as a <code>prov:type</code>.</p>
{generate_terms(g, type_terms)}</section>
"""


def generate_html(ttl_path: str, html_path: str) -> None:
    g, ontology_iri, prop_terms, role_terms, type_terms, groups = load(ttl_path)

    # Ontology metadata: the header is the namespace IRI, with or without '#'.
    ont_title = (get_literal(g, ontology_iri, RDFS.label)
                 or get_literal(g, ontology_iri, DCT.title) or "Vocabulary")
    ont_desc  = (get_literal(g, ontology_iri, RDFS.comment)
                 or get_literal(g, ontology_iri, DCT.description))
    imports   = sorted(str(o) for o in g.objects(ontology_iri, OWL.imports))

    prefixes = [(VOCAB_PREFIX, VOCAB_NS), ("prov", str(PROV)), ("provext", str(PROVEXT)),
                ("owl", str(OWL)), ("rdfs", str(RDFS)), ("rdf", str(RDF)), ("xsd", str(XSD))]
    prefix_rows = "".join(
        f'      <tr><td><code>{esc(p)}</code></td><td><code>{esc(ns)}</code></td></tr>\n'
        for p, ns in prefixes
    )
    imports_html = "".join(
        f'  <p>Imports <code><a href="{esc(i)}">{esc(i)}</a></code>.</p>\n' for i in imports
    )

    html_out = f"""\
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>{esc(ont_title)}</title>
  <style>
{CSS}  </style>
</head>
<body>

  <h1>{esc(ont_title)}</h1>
  <p class="subtitle">Namespace: <code>{esc(VOCAB_NS)}</code> &nbsp; Prefix: <code>{esc(VOCAB_PREFIX)}</code></p>
  <p>{esc(ont_desc)}</p>
{imports_html}
  <nav id="toc">
    <h2>Table of Contents</h2>
    <ul>
      <li><a href="#namespaces">Namespaces</a></li>
      <li><a href="#properties">Object Properties</a>
        <ul>
{toc_entries(prop_terms)}        </ul>
      </li>
      <li><a href="#roles">Roles</a>
        <ul>
{toc_entries(role_terms)}        </ul>
      </li>
      <li><a href="#types">Types</a>
        <ul>
{toc_entries(type_terms)}        </ul>
      </li>
    </ul>
  </nav>

  <section id="namespaces">
    <h2>Namespaces</h2>
    <table class="prefixes">
{prefix_rows}    </table>
  </section>

{generate_section(g, "properties", "Object Properties",
    "Each property is declared as <code>owl:ObjectProperty</code>; it is optional on the relation "
    "named by its domain and points at a statement that relation summarises.",
    prop_terms, groups)}
{generate_section(g, "roles", "Roles",
    "Each role is an individual of type <code>prov:Role</code>, for use as the "
    "<code>prov:role</code> of a usage or generation.", role_terms)}
{generate_section(g, "types", "Types",
    "Each type is declared as <code>owl:Class</code>, for use as a <code>prov:type</code>.",
    type_terms)}
</body>
</html>
"""

    out = Path(html_path)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(html_out, encoding="utf-8")
    print(f"Written: {html_path}")


if __name__ == "__main__":
    args = sys.argv[1:]
    injecting = "--inject" in args
    if injecting:
        args.remove("--inject")
    if len(args) not in (2, 4):
        print(f"Usage: python3 {sys.argv[0]} <input.ttl> <output.html>|--inject <index.html>"
              " [<namespace> <prefix>]")
        sys.exit(1)
    if len(args) == 4:
        VOCAB_NS     = args[2]
        VOCAB_PREFIX = args[3]

    if injecting:
        inject(args[1], "ontology", "insert here auto generated html describing the ontology",
               Path(args[0]).name, Path(sys.argv[0]).name, generate_fragment(args[0]))
    else:
        generate_html(args[0], args[1])
