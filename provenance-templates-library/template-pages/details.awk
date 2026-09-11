# The Details value as a YAML folded block scalar (">-"): a block scalar is
# literal text, so prose containing ": " no longer becomes a mapping key -- that
# is what broke 13 pages -- and folding reproduces what the old plain scalar
# gave the 16 that worked: each paragraph's lines joined with spaces, paragraphs
# kept apart.  Every content line is set at four spaces (a more-indented line
# would be kept literal rather than folded), blank lines are made truly empty,
# trailing whitespace dropped, and leading blank lines skipped.  Details is the
# last field on every page, so the block runs to the end of the file.
/^- Details:[[:space:]]*$/ { print "- Details: >-"; inblock = 1; next }
inblock {
    line = $0
    sub(/[[:space:]]+$/, "", line)
    if (line == "") { if (started) print ""; next }
    sub(/^[[:space:]]+/, "", line)
    print "    " line
    started = 1
    next
}
{ print }
