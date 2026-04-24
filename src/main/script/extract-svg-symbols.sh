#!/usr/bin/env bash
#
# extract-svg-symbols.sh
#
# Reads an SVG file, extracts every <symbol> element and its id attribute
# using XPath (xmllint), and writes a JSON object to an output file where
# each key is the symbol id and each value is the full <symbol> XML string.
#
# Usage:
#   ./extract-svg-symbols.sh [input.svg [output.json]]
#
# Defaults:
#   input  — /Users/luc/git-papers/papers/book-ptm/project/template-intro1/src/main/resources/icons/file-icons.svg
#   output — symbols.json  (written next to the input file)
#
# Dependencies: xmllint (libxml2), jq
#
# Output shape:
#   {
#     "icon-file-init":         "<symbol id=\"icon-file-init\" ...>…</symbol>",
#     "icon-file-transforming": "<symbol id=\"icon-file-transforming\" ...>…</symbol>",
#     …
#   }
#
# Note on XPath and namespaces
# ────────────────────────────
# SVG files carry a default namespace (xmlns="http://www.w3.org/2000/svg").
# xmllint's XPath engine requires namespace prefixes for any element in a
# named namespace, so a bare //symbol query returns nothing.  Using
# local-name() bypasses namespace resolution entirely and works on any
# SVG regardless of how xmlns declarations are written.

set -euo pipefail

# ── Arguments ─────────────────────────────────────────────────────────────────
SVG_FILE="${1:-src/main/resources/icons/file-icons.svg}"
OUTPUT_FILE="${2:-$(dirname "$SVG_FILE")/file-icons.json}"

# ── Dependency check ──────────────────────────────────────────────────────────
for cmd in xmllint jq; do
    command -v "$cmd" >/dev/null 2>&1 \
        || { echo "Error: '$cmd' not found — please install it first." >&2; exit 1; }
done

[[ -f "$SVG_FILE" ]] \
    || { echo "Error: SVG file not found: $SVG_FILE" >&2; exit 1; }

# ── Extract symbol IDs via XPath ──────────────────────────────────────────────
# local-name() matches the element regardless of any XML namespace, so this
# works on SVG files with or without xmlns declarations.
#
# xmllint --xpath outputs all matching attribute nodes as a flat string:
#   id="icon-file-init" id="icon-file-transforming" …
RAW_IDS=$(xmllint --xpath '//*[local-name()="symbol"]/@id' "$SVG_FILE" 2>/dev/null \
    || true)

if [[ -z "$RAW_IDS" ]]; then
    echo "Error: no <symbol> elements found in: $SVG_FILE" >&2
    exit 1
fi

# Parse the flat attribute string into one id per line.
# grep -oE matches each double-quoted value; tr strips the surrounding quotes.
IDS=$(printf '%s' "$RAW_IDS" | grep -oE '"[^"]+"' | tr -d '"')

# ── Build JSON ────────────────────────────────────────────────────────────────
# For each id, extract the complete <symbol> element via XPath and emit a
# one-key JSON object  { "id": "<symbol>…</symbol>" }.
# Piping all objects to `jq -s 'add'` merges them into a single JSON object.
# jq handles all escaping (quotes, backslashes, newlines) automatically.
{
    while IFS= read -r id; do
        [[ -z "$id" ]] && continue

        symbol=$(xmllint \
            --xpath "//*[local-name()='symbol'][@id='${id}']" \
            "$SVG_FILE" 2>/dev/null \
            || true)

        if [[ -z "$symbol" ]]; then
            echo "Warning: could not extract element for id='${id}'" >&2
            continue
        fi

        # Emit one JSON object per symbol; jq handles all escaping
        jq -n --arg k "$id" --arg v "$symbol" '{($k): $v}'

    done <<< "$IDS"
} | jq -s 'add' > "$OUTPUT_FILE"

# ── Summary ───────────────────────────────────────────────────────────────────
count=$(printf '%s\n' "$IDS" | grep -c .)
echo "Extracted $count symbol(s) from $(basename "$SVG_FILE") → $OUTPUT_FILE"
