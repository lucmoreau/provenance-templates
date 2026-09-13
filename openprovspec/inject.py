"""
inject.py — Put generated HTML into a hand-written ReSpec document, repeatably.

The document carries a placeholder comment where each generated block goes:

    <!-- insert here auto generated html describing the ontology -->

The first run leaves the placeholder in place and appends the block after
it, fenced by markers naming the generator:

    <!-- insert here auto generated html describing the ontology -->
    <!-- BEGIN generated: ontology (ttl-to-html.py from openprov.ttl) -->
    ...
    <!-- END generated: ontology -->

Later runs replace what stands between the markers, so the make target can
be rerun whenever the source changes, and the rest of the document -- the
prose the editors write -- is never touched.
"""

import re
from pathlib import Path


def inject(index_path: str, key: str, placeholder: str, source: str, generator: str, html: str) -> None:
    """Replace, or first insert, the block `key` in index_path with html."""
    path = Path(index_path)
    doc = path.read_text(encoding="utf-8")
    begin = f"<!-- BEGIN generated: {key} ({generator} from {source}) -->"
    end = f"<!-- END generated: {key} -->"
    block = f"{begin}\n{html.rstrip()}\n{end}"

    fenced = re.compile(
        rf"<!-- BEGIN generated: {re.escape(key)} \(.*?\) -->.*?<!-- END generated: {re.escape(key)} -->",
        re.S,
    )
    if fenced.search(doc):
        doc, n = fenced.subn(lambda m: block, doc, count=1)
        action = "updated"
    else:
        marker = f"<!-- {placeholder} -->"
        if marker not in doc:
            raise SystemExit(f"{index_path}: no placeholder <!-- {placeholder} --> and no "
                             f"generated block '{key}' to replace")
        doc = doc.replace(marker, f"{marker}\n{block}", 1)
        action = "inserted"
    path.write_text(doc, encoding="utf-8")
    print(f"{index_path}: {action} the generated '{key}' block")


# The style of the generated blocks -- a card per term, as on the standalone
# pages -- emitted once by each generator inside its block, so that the
# hand-written document need not carry it.  Selectors are scoped to .term.
TERM_CSS = """\
.term { background: #e8f0fe; color: #222; border-left: 3px solid #4285f4; border-radius: 3px;
        padding: 0.8em 1em; margin: 1em 0; }
.term a { color: #1a73e8; }
.term code { color: inherit; }
.term .term-name { font-size: 1.1em; margin: 0 0 0.5em; color: #1a73e8; font-weight: bold; }
.term .term-name dfn { font-style: normal; }
.term .term-iri { font-size: 0.85em; color: #555; font-weight: normal; margin-left: 0.6em; }
.term table { border-collapse: collapse; width: 100%; font-size: 0.93em; margin: 0; }
.term table.facts th { text-align: left; vertical-align: top; white-space: nowrap; width: 160px;
        padding: 0.2em 0.8em 0.2em 0; color: #555; font-weight: normal; font-style: italic;
        border: none; background: none; }
.term table.keys th { text-align: left; padding: 0.2em 0.8em 0.2em 0; color: #555;
        font-weight: normal; font-style: italic; border: none; border-bottom: 1px solid #4285f4;
        background: none; }
.term td { padding: 0.2em 0.8em 0.2em 0; border: none; vertical-align: top; }
.term td code { word-break: break-all; }
p.group { font-style: italic; color: #444; margin: 1.5em 0 0.3em; }
"""
