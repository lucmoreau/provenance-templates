PROVTOOLBOX_DIR=$(HOME)/IdeaProjects/ProvToolbox
PROVCONVERT=$(PROVTOOLBOX_DIR)/modules-executable/toolbox/target/appassembler/bin/provconvert

PY_PATH=/Users/luc/IdeaProjects/ProvToolbox/modules-template/prov-template-library/target/generated-python/:/Users/luc/IdeaProjects/ProvToolbox/modules-template/prov-template-library/src/main/python:target/generated-python:src/test/python


compile.ttf:
	$(PROVCONVERT) -inputBaseDir `pwd`/template-intro1 -outputBaseDir `pwd`/template-intro1  -templatebuilder `pwd`/template-intro1/src/main/resources/catalogue/transport-catalogue.json -templateLibrary `pwd`/template-intro1/src/main/resources/templates -templateLibrary `pwd`/template-intro1/target/generated-templates
icons:
	@./src/main/script/extract-svg-symbols.sh src/main/resources/icons/file-icons.svg src/main/resources/icons/file-icons.json
	@./src/main/script/extract-svg-symbols.sh src/main/resources/icons/transport-icons.svg src/main/resources/icons/transport-icons.json

test.py:
	export PYTHONPATH=$(PY_PATH); python3 src/test/python/org/openprovenance/bookptm/run_box_workflow.py


fs.local.py:
	@export PYTHONPATH=$(PY_PATH); python3 src/test/python/org/openprovenance/bookptm/fs_run_workflow.py local
fs.remote.py:
	@export PYTHONPATH=$(PY_PATH); python3 src/test/python/org/openprovenance/bookptm/fs_run_workflow.py remote

box.local.py:
	@export PYTHONPATH=$(PY_PATH); python3 src/test/python/org/openprovenance/bookptm/box_run_workflow.py local
box.remote.py:
	@export PYTHONPATH=$(PY_PATH); python3 src/test/python/org/openprovenance/bookptm/box_run_workflow.py remote

fs.local.jsweet.js:
	node src/test/js/jsweet_run_workflow.js local
fs.remote.jsweet.js:
	node src/test/js/jsweet_run_workflow.js remote


fs.local.js:
	export NODE_PATH=`pwd`/target/generated-js; node src/test/js/fs_run_workflow.js local
fs.remote.js:
	export NODE_PATH=`pwd`/target/generated-js; node src/test/js/fs_run_workflow.js remote

box.local.js:
	export NODE_PATH=`pwd`/target/generated-js; node src/test/js/box_run_workflow.js local
box.remote.js:
	export NODE_PATH=`pwd`/target/generated-js; node src/test/js/box_run_workflow.js remote

fs.local.rs:
	cd target/generated-rust; cargo run fs local
box.local.rs:
	cd target/generated-rust; cargo run box local


local:
	make fs.local.js
	make fs.local.py
	make fs.local.jsweet.js
	make fs.local.rs
	make box.local.js
	make box.local.py
	make box.local.rs

fs.all.js:
	make fs.local.js
	make fs.local.jsweet.js
	make fs.remote.js
	make fs.remote.jsweet.js


fs.remote:
	make fs.remote.py
	make fs.remote.js
	make fs.remote.jsweet.js

box.remote:
	make box.remote.py

cat:
	mvn compiler:compile@compile-source
	mvn prov:compile-catalogue@compile.catalogue
	mvn exec:exec@python-fs-run-workflow
	mvn exec:exec@python-box-run-workflow
	mvn exec:exec@js-fs-run-workflow
