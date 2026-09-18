# The OpenProv specification: index.html is the hand-written ReSpec document;
# `make html` refreshes the two generated blocks inside it -- the ontology,
# from openprov.ttl, and the JSON-LD context, from openprov.jsonld -- at
# their placeholders (see script/inject.py), the one block of
# template.html, the template vocabulary, from template.ttl, and writes
# provtoolbox.html, the ProvToolbox vocabulary's page, from provtoolbox.ttl
# (no hand-written page there: the generated page is the published one).
# Rerun after any source changes.
#
#   make html        refresh the generated blocks in index.html and template.html,
#                    and provtoolbox.html
#   make schema      openprov-schema.json, the PROV-JSONLD schema extended
#                    with the context's terms by reference (fetches the
#                    published schema; see script/schema-extend.py)
#   make standalone  openprov.html, context.html and tmpl.html, the same
#                    content as self-contained pages, for checking the generators

.PHONY: all html schema standalone

all: html schema

html:
	python3 script/ttl-to-html.py openprov.ttl --inject index.html
	python3 script/context-to-html.py openprov.jsonld --inject index.html
	python3 script/template-ttl-to-html.py template.ttl --inject template.html
	python3 script/provtoolbox-ttl-to-html.py provtoolbox.ttl provtoolbox.html

standalone:
	python3 script/ttl-to-html.py openprov.ttl openprov.html
	python3 script/context-to-html.py openprov.jsonld context.html
	python3 script/template-ttl-to-html.py template.ttl tmpl.html

SPEC_SCHEMA=https://openprovenance.org/prov-jsonld/schema.json

schema:
	curl -s -o /tmp/prov-jsonld-schema.json $(SPEC_SCHEMA)
	python3 script/schema-extend.py /tmp/prov-jsonld-schema.json openprov.jsonld openprov-schema.json
	rm /tmp/prov-jsonld-schema.json
