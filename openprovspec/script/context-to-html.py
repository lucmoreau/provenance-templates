#!/usr/bin/env python3
"""
context-to-html.py — Describe a JSON-LD context as HTML for the specification.

Usage:
    python3 context-to-html.py <context.jsonld> --inject <index.html>
    python3 context-to-html.py <context.jsonld> <output.html>

Reads the context -- an array of an imported context and a context object,
as openprov.jsonld is shaped -- and writes, for each term the object defines
with a scoped @context (a class such as Attribution), a table of the JSON
keys that context maps: the key, the IRI it stands for, whether it is a
plain or a reverse property, and its type coercion.  Prefix declarations
and the imported context are listed first; the context itself follows
verbatim as an example.  With --inject the HTML goes into the placeholder
"insert here auto generated html describing the context" (see inject.py),
and rerunning refreshes it.
"""

import html
import json
import sys
from pathlib import Path

from inject import inject, TERM_CSS


def esc(s) -> str:
    return html.escape(str(s))


VOCAB_PREFIX = "openprov"


def term_link(iri: str) -> str:
    """A curie of this vocabulary links to its definition in the ontology section."""
    if iri.startswith(VOCAB_PREFIX + ":"):
        return f'<a href="#{esc(iri[len(VOCAB_PREFIX) + 1:])}"><code>{esc(iri)}</code></a>'
    return f'<code>{esc(iri)}</code>'


def term_table(name: str, definition: dict) -> str:
    """One class-scoped term: a card with the keys its @context maps."""
    rows = []
    for key, d in definition.get("@context", {}).items():
        if isinstance(d, str):
            iri, kind, typ = d, "property", ""
        else:
            iri = d.get("@id") or d.get("@reverse", "")
            kind = "reverse property" if "@reverse" in d else "property"
            typ = d.get("@type", "")
            if "@container" in d:
                typ = (typ + " " if typ else "") + f"container {d['@container']}"
        rows.append(
            f'      <tr><td><code>{esc(key)}</code></td><td>{term_link(iri)}</td>'
            f'<td>{esc(kind)}</td><td><code>{esc(typ)}</code></td></tr>'
        )
    return (
        f'    <div class="term" id="context-{esc(name)}">\n'
        f'      <div class="term-name"><code>{esc(name)}</code>'
        f'<span class="term-iri">stands for {term_link(definition.get("@id", ""))}</span></div>\n'
        f'      <table class="keys">\n'
        f'        <thead><tr><th>key</th><th>IRI</th><th>mapping</th><th>@type</th></tr></thead>\n'
        f'        <tbody>\n' + "\n".join(rows) + '\n        </tbody>\n'
        f'      </table>\n'
        f'    </div>\n'
    )


def generate_fragment(context_path: str) -> str:
    doc = json.loads(Path(context_path).read_text(encoding="utf-8"))
    ctx = doc.get("@context", doc)
    parts = ctx if isinstance(ctx, list) else [ctx]
    imported = [p for p in parts if isinstance(p, str)]
    objects = [p for p in parts if isinstance(p, dict)]

    out = [f"<style>\n{TERM_CSS}</style>"]
    if imported:
        out.append("<p>The context builds on " +
                   ", ".join(f'<code><a href="{esc(i)}">{esc(i)}</a></code>' for i in imported) +
                   ".</p>")
    prefixes, classes, others = [], [], []
    for obj in objects:
        for k, v in obj.items():
            if k.startswith("@"):
                continue
            if isinstance(v, str):
                prefixes.append((k, v))
            elif isinstance(v, dict) and "@context" in v:
                classes.append((k, v))
            else:
                others.append((k, v))
    versions = [obj["@version"] for obj in objects if "@version" in obj]
    if versions:
        out.append(f"<p>The context declares <code>@version</code> {esc(versions[0])}.</p>")
    if prefixes:
        out.append('<p>It declares the following prefixes.</p>\n<div class="term"><table class="keys">\n'
                   '  <thead><tr><th>prefix</th><th>namespace IRI</th></tr></thead>\n  <tbody>\n' +
                   "\n".join(f'    <tr><td><code>{esc(k)}</code></td><td><code>{esc(v)}</code></td></tr>'
                             for k, v in prefixes) +
                   '\n  </tbody>\n</table></div>')
    if others:
        out.append('<p>It defines the following terms.</p>\n<table class="thinborder">\n'
                   '  <thead><tr><th>key</th><th>definition</th></tr></thead>\n  <tbody>\n' +
                   "\n".join(f'    <tr><td><code>{esc(k)}</code></td><td><code>{esc(json.dumps(v))}</code></td></tr>'
                             for k, v in others) +
                   '\n  </tbody>\n</table>')
    if classes:
        out.append('<p>Each of the following keys names a type of relation and carries a scoped '
                   'context: the keys it lists are defined only inside an object of that type; '
                   'a key mapped as a property gives the IRI as predicate, a reverse property '
                   'as the predicate pointing back at the object.</p>')
        out.extend(term_table(k, v) for k, v in classes)
    out.append(f'<pre class="example" title="The context, {esc(Path(context_path).name)}">\n'
               f'{esc(Path(context_path).read_text(encoding="utf-8").rstrip())}\n</pre>')
    return "\n".join(out) + "\n"


def generate_html(context_path: str, html_path: str) -> None:
    body = generate_fragment(context_path)
    Path(html_path).write_text(
        '<!DOCTYPE html>\n<html lang="en"><head><meta charset="UTF-8">'
        f'<title>{esc(Path(context_path).name)}</title>\n'
        '<style>body{font-family:Georgia,serif;max-width:900px;margin:0 auto;padding:2em}'
        'table.thinborder{border-collapse:collapse}table.thinborder td,table.thinborder th'
        '{border:1px solid gray;padding:2px 6px}pre{background:#f5f5f5;padding:1em;overflow:auto}'
        'code{font-family:monospace;background:#f5f5f5;padding:.1em .3em;border-radius:3px}</style>'
        f'</head>\n<body>\n<h1>{esc(Path(context_path).name)}</h1>\n{body}</body>\n</html>\n',
        encoding="utf-8")
    print(f"Written: {html_path}")


if __name__ == "__main__":
    args = sys.argv[1:]
    injecting = "--inject" in args
    if injecting:
        args.remove("--inject")
    if len(args) != 2:
        print(f"Usage: python3 {sys.argv[0]} <context.jsonld> <output.html>|--inject <index.html>")
        sys.exit(1)
    if injecting:
        inject(args[1], "context", "insert here auto generated html describing the context",
               Path(args[0]).name, Path(sys.argv[0]).name, generate_fragment(args[0]))
    else:
        generate_html(args[0], args[1])
