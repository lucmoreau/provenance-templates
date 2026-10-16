# PROV-TEMPLATE family of documents.
#
#   make examples   regenerate the primer's outputs: the TTF prov-template-primer/examples/pipeline-ttf.json
#                   instantiates and merges the pipeline templates into examples/run/, and every template is
#                   drawn as SVG into examples/figures/
#   make validate   check the examples against ProvToolbox's JSON Schemas (bindings, TTF) and the library's
#                   index schema, with ajv
#   make serve      serve the documents at http://127.0.0.1:8765/ (ReSpec's data-include needs http, not file:)
#
# The JVM runs in UTC so that the times of the bindings ("...Z") print as written.

PROVTOOLBOX  ?= $(HOME)/IdeaProjects/ProvToolbox
TOOLBOX_BIN  ?= $(PROVTOOLBOX)/modules-executable/toolbox/target/appassembler/bin
JSONSCHEMA   ?= $(PROVTOOLBOX)/modules-template/prov-template/src/main/resources/jsonschema
LIBRARY      ?= ../provenance-templates-library
PORT         ?= 8765

PRIMER       = prov-template-primer/examples
TEMPLATES    = $(wildcard $(PRIMER)/templates/*.provn)
RUN_ENV      = TZ=UTC JAVA_OPTS="-Duser.timezone=UTC"

.PHONY: examples figures validate serve

examples: figures
	rm -rf $(PRIMER)/run
	cd $(PRIMER) && $(RUN_ENV) $(TOOLBOX_BIN)/prov-template-process . . -configs pipeline-ttf.json

figures:
	mkdir -p $(PRIMER)/figures
	for t in $(TEMPLATES); do \
	  $(RUN_ENV) $(TOOLBOX_BIN)/provconvert -infile $$t -outfile $(PRIMER)/figures/$$(basename $$t .provn).svg || exit 1; \
	done

validate:
	for f in $(PRIMER)/bindings/*.json $(PRIMER)/cbindings/*.json; do \
	  ajv validate -s $(JSONSCHEMA)/bindings-schema.json -d $$f || exit 1; \
	done
	ajv validate -s $(JSONSCHEMA)/template-transformation-file-schema.json -d $(PRIMER)/pipeline-ttf.json
	ajv validate -s $(LIBRARY)/src/main/resources/json-schema/index-schema.json -d $(PRIMER)/templates/index.json

serve:
	python3 -m http.server $(PORT) --bind 127.0.0.1
