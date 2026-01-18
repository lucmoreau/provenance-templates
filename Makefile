PROVTOOLBOX_DIR=$(HOME)/IdeaProjects/ProvToolbox
PROVCONVERT=$(PROVTOOLBOX_DIR)/modules-executable/toolbox/target/appassembler/bin/provconvert

compile.ttf:
	$(PROVCONVERT) -inputBaseDir `pwd`/template-intro1 -outputBaseDir `pwd`/template-intro1  -templatebuilder `pwd`/template-intro1/src/main/resources/catalogue/transport-catalogue.json -templateLibrary `pwd`/template-intro1/src/main/resources/templates -templateLibrary `pwd`/template-intro1/target/generated-templates


test.py:
	export PYTHONPATH=/Users/luc/IdeaProjects/ProvToolbox/modules-template/prov-template-library/target/generated-python/:/Users/luc/IdeaProjects/ProvToolbox/modules-template/prov-template-library/src/main/python:target/generated-python:src/test/python; python3 src/test/python/org/openprovenance/bookptm/run_box_workflow.py
