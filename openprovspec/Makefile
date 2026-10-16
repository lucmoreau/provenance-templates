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
#   make diagram     schema-dependencies.svg and .png from schema-dependencies.mmd,
#                    the schemas and contexts and how they depend on each other
#                    (needs mmdc, the Mermaid CLI: npm install -g @mermaid-js/mermaid-cli)

.PHONY: all html schema standalone diagram

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

MMDC=mmdc

diagram: schema-dependencies.svg schema-dependencies.png

schema-dependencies.svg: schema-dependencies.mmd
	$(MMDC) -i $< -o $@ -b white

schema-dependencies.png: schema-dependencies.mmd
	$(MMDC) -i $< -o $@ -b white -s 2 -w 2400
