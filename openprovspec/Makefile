# The OpenProv specification: index.html is the hand-written ReSpec document;
# `make html` refreshes the two generated blocks inside it -- the ontology,
# from openprov.ttl, and the JSON-LD context, from openprov.jsonld -- at
# their placeholders (see inject.py).  Rerun after either source changes.
#
#   make html        refresh the generated blocks in index.html
#   make standalone  openprov.html and context.html, the same content as
#                    self-contained pages, for checking the generators

.PHONY: html standalone

html:
	python3 ttl-to-html.py openprov.ttl --inject index.html
	python3 context-to-html.py openprov.jsonld --inject index.html

standalone:
	python3 ttl-to-html.py openprov.ttl openprov.html
	python3 context-to-html.py openprov.jsonld context.html
