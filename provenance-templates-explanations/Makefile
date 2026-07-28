
# keep sub-make chatter (Entering/Leaving directory) out of stdout: aggregate
# targets pipe the generated json into jq
MAKEFLAGS += --no-print-directory

PROVTOOLBOX_DIR=/Users/luc/IdeaProjects/ProvToolbox
JAVA_OPTS="-Dlog4j.configurationFile=src/test/resources/ptm/config/log4j2.xml -Djava.util.logging.config.file=src/test/resources/ptm/config/logging.properties"

# provman uses its own copy of the lexical database: the deployed service (make jetty
# in the generated project) holds an exclusive hsqldb lock on the nlg-wrapper copy,
# and hsqldb allows a single process per database.
LEXICON_SOURCE=$(PROVTOOLBOX_DIR)/modules-explanations/nlg-wrapper/target/dictionary
LEXICON_DIR=$(CURDIR)/target/dictionary
JAVA_OPTS2=-DXPLAIN_LEXICON=$(LEXICON_DIR)/lexAccess2013.data

$(LEXICON_DIR)/lexAccess2013.data:
	@echo "Copying lexical database to $(LEXICON_DIR) ..." >&2
	@mkdir -p $(LEXICON_DIR)
	@cp $(LEXICON_SOURCE)/lexAccess2013.data $(LEXICON_DIR)/
	@cp $(LEXICON_SOURCE)/lexAccess2013.properties $(LEXICON_DIR)/
	@cp $(LEXICON_SOURCE)/lexAccess2013.script $(LEXICON_DIR)/

PROVMAN=export JAVA_OPTS=$(JAVA_OPTS2); $(PROVTOOLBOX_DIR)/modules-executable-provman/target/appassembler/bin/provmanagement
NLG_XPLAIN=src/main/resources/xplain/nlg

XPLAN_SCHEMA=src/main/resources/schema/xplan-schema.json
AJV=ajv

# X-plan template files to validate (excludes dictionaries, profiles, config, and
# provbasic2.json which intentionally contains // comments and is not strict JSON)
XPLAN_FILES = \
    $(NLG_XPLAIN)/fs/fs-xplan-library.json \
    $(NLG_XPLAIN)/fs/fs-awk-column-selection.json \
    $(NLG_XPLAIN)/fs/fs-file-derivation.json \
    $(NLG_XPLAIN)/fs/fs-file-init.json \
    $(NLG_XPLAIN)/fs/fs-file-splitting.json \
    $(NLG_XPLAIN)/fs/fs-file-validating.json \
    $(NLG_XPLAIN)/fs/fs-file-approving.json \
    $(NLG_XPLAIN)/fs/fs-generic-responsibility.json \
    $(NLG_XPLAIN)/tr/tr-xplan-library.json \
    $(NLG_XPLAIN)/tr/tr-transporting.json \
    $(NLG_XPLAIN)/tr/tr-packing.json \
    $(NLG_XPLAIN)/tr/tr-unpacking.json \
    $(NLG_XPLAIN)/tr/tr-examining.json \
    $(NLG_XPLAIN)/tr/tr-item-init.json \
    $(NLG_XPLAIN)/tr/tr-examining-no-instrument.json \
    $(NLG_XPLAIN)/tr/tr-agent-init.json \
    $(NLG_XPLAIN)/tr/tr-assigning.json \
    $(NLG_XPLAIN)/tr/tr-handingover.json \
    $(NLG_XPLAIN)/provbasic/provbasic.json \
    $(NLG_XPLAIN)/provbasic/actedonbehalfof1.json \
    $(NLG_XPLAIN)/provbasic/activity1.json \
    $(NLG_XPLAIN)/provbasic/agent1.json \
    $(NLG_XPLAIN)/provbasic/alternate1.json \
    $(NLG_XPLAIN)/provbasic/association1.json \
    $(NLG_XPLAIN)/provbasic/attribution1.json \
    $(NLG_XPLAIN)/provbasic/derivation1.json \
    $(NLG_XPLAIN)/provbasic/end1.json \
    $(NLG_XPLAIN)/provbasic/end2.json \
    $(NLG_XPLAIN)/provbasic/end3.json \
    $(NLG_XPLAIN)/provbasic/entity1.json \
    $(NLG_XPLAIN)/provbasic/generation1.json \
    $(NLG_XPLAIN)/provbasic/invalidation1.json \
    $(NLG_XPLAIN)/provbasic/member1.json \
    $(NLG_XPLAIN)/provbasic/specialization1.json \
    $(NLG_XPLAIN)/provbasic/start1.json \
    $(NLG_XPLAIN)/provbasic/start2.json \
    $(NLG_XPLAIN)/provbasic/start3.json \
    $(NLG_XPLAIN)/provbasic/usage1.json

schema.validate:
	@echo "Validating x-plan files against $(XPLAN_SCHEMA) ..."
	@failed=0; \
	for f in $(XPLAN_FILES); do \
	    result=$$($(AJV) validate -s $(XPLAN_SCHEMA) -d "$$f" --errors=text 2>&1); \
	    rc=$$?; \
	    fname=$$(basename "$$f"); \
	    if [ $$rc -eq 0 ]; then \
	        echo "  OK   $$fname"; \
	    else \
	        echo "  FAIL $$fname"; \
	        echo "$$result"; \
	        failed=$$((failed+1)); \
	    fi; \
	done; \
	if [ $$failed -eq 0 ]; then \
	    echo "All x-plan files are valid."; \
	else \
	    echo "$$failed file(s) failed validation."; \
	    exit 1; \
	fi
DOCUMENTS=src/test/resources/prov

# SOURCE_NAME/PROPERTY_NAME rename the key of a shared x-plan in the generated json,
# so that its sentences can be labelled by scenario (e.g. fs-file-fitting,
# fs-file-filtering, tr-weighing). By default, the key is left unchanged.
SOURCE_NAME=fs-file-derivation
PROPERTY_NAME=$(SOURCE_NAME)

# x-plan library used by batch.explain; tr targets override it
XPLAN_LIB=$(NLG_XPLAIN)/fs/fs-xplan-library.json
TR_XPLAN_LIB=$(NLG_XPLAIN)/tr/tr-xplan-library.json

batch.explain: $(LEXICON_DIR)/lexAccess2013.data
	@$(PROVMAN) explain --infile $(DOCUMENTS)/$(FILE).provn --outfile target/narrative-loan.provn --text target/narrative-loan.json --language $(XPLAN_LIB) -s target/simplenlg2.txt --profile=$(PROFILE)  --batch-templates=$(BATCH)
	@jq 'with_entries(if .key == "$(SOURCE_NAME)" then .key = "$(PROPERTY_NAME)" else . end) | with_entries(select(.value != ""))' target/narrative-loan.json



xp.filtering:
	@$(MAKE) batch.explain FILE=file_filtering BATCH=[fs-awk-column-selection]

# the filtering variants share the fs-file-derivation x-plan (see xp.fitting.on.filtering):
# verb and object qualifier come from the dictionary, keyed on prov:type
xp.filtering.all: xp.filtering xp.fitting.on.filtering xp.filtering.no.method xp.filtering.no.agent xp.filtering.no.association xp.filtering.no.date

xp.filtering.no.method:
	@$(MAKE) batch.explain FILE=file_filtering_no_method BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-filtering
xp.filtering.no.agent:
	@$(MAKE) batch.explain FILE=file_filtering_no_agent BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-filtering
xp.filtering.no.association:
	@$(MAKE) batch.explain FILE=file_filtering_no_association BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-filtering
xp.filtering.no.date:
	@$(MAKE) batch.explain FILE=file_filtering_no_date BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-filtering

xp.transforming.all: xp.transforming xp.transforming.no.method xp.transforming.no.agent xp.transforming.no.association

xp.init.all: xp.init xp.init.no.agent xp.init.no.date

xp.transforming:
	@$(MAKE) batch.explain FILE=file_transforming1 BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-transforming
xp.transforming.no.method:
	@$(MAKE) batch.explain FILE=file_transforming1_no_method BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-transforming
xp.transforming.no.agent:
	@$(MAKE) batch.explain FILE=file_transforming1_no_agent BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-transforming
xp.transforming.no.association:
	@$(MAKE) batch.explain FILE=file_transforming1_no_association BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-transforming
xp.init:
	@$(MAKE) batch.explain FILE=file_init BATCH=[fs-file-init]
xp.init.no.agent:
	@$(MAKE) batch.explain FILE=file_init_no_agent BATCH=[fs-file-init]
xp.init.no.date:
	@$(MAKE) batch.explain FILE=file_init_no_date BATCH=[fs-file-init]

xp.validating.all: xp.validating xp.validating.method xp.validating.no.agent xp.validating.no.date
xp.validating:
	@$(MAKE) batch.explain FILE=file_validating BATCH=[fs-file-validating]
xp.validating.method:
	@$(MAKE) batch.explain FILE=file_validating_method BATCH=[fs-file-validating]
xp.validating.no.agent:
	@$(MAKE) batch.explain FILE=file_validating_no_agent BATCH=[fs-file-validating]
xp.validating.no.date:
	@$(MAKE) batch.explain FILE=file_validating_no_date BATCH=[fs-file-validating]

xp.approving.all: xp.approving xp.approving.no.agent xp.approving.no.score
xp.approving:
	@$(MAKE) batch.explain FILE=file_approving BATCH=[fs-file-approving]
xp.approving.no.agent:
	@$(MAKE) batch.explain FILE=file_approving_no_agent BATCH=[fs-file-approving]
xp.approving.no.score:
	@$(MAKE) batch.explain FILE=file_approving_no_score BATCH=[fs-file-approving]

xp.splitting.all: xp.splitting xp.splitting.four.way

xp.splitting:
	@$(MAKE) batch.explain FILE=file_splitting1 BATCH=[fs-file-splitting]
xp.splitting.four.way:
	@$(MAKE) batch.explain FILE=file_splitting_four_way BATCH=[fs-file-splitting]

xp.fitting.all: xp.fitting xp.fitting.no.method xp.fitting.untyped.method xp.fitting.untyped.entities xp.fitting.no.agent xp.fitting.no.association xp.fitting.no.date

# same x-plan as xp.fitting: verb and object qualifier come from the dictionary, keyed on prov:type
xp.fitting.on.filtering:
	@$(MAKE) batch.explain FILE=file_filtering BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-filtering

xp.fitting:
	@$(MAKE) batch.explain FILE=file_training BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.no.method:
	@$(MAKE) batch.explain FILE=file_training_no_method BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.untyped.method:
	@$(MAKE) batch.explain FILE=file_training_untyped_method BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.untyped.entities:
	@$(MAKE) batch.explain FILE=file_training_untyped_entities BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.no.agent:
	@$(MAKE) batch.explain FILE=file_training_no_agent BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.no.association:
	@$(MAKE) batch.explain FILE=file_training_no_association BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting
xp.fitting.no.date:
	@$(MAKE) batch.explain FILE=file_training_no_date BATCH=[fs-file-derivation] PROPERTY_NAME=fs-file-fitting

# --- Transport book (tr): x-plans for the physical and responsibility template packages.
# Test documents are based on recent records from the bookptm database (see each
# document's header comment for its record of origin).

tr.explain:
	@$(MAKE) batch.explain XPLAN_LIB=$(TR_XPLAN_LIB) FILE=$(FILE) BATCH=$(BATCH) SOURCE_NAME=$(SOURCE_NAME) PROPERTY_NAME=$(PROPERTY_NAME)

xp.tr.all: xp.tr.transporting.all xp.tr.packing.all xp.tr.unpacking.all xp.tr.weighing.all xp.tr.examining xp.tr.agent.init.all xp.tr.item.init.all xp.tr.assigning.all xp.tr.handingover.all

xp.tr.transporting.all: xp.tr.transporting xp.tr.transporting.full xp.tr.transporting.no.agent
xp.tr.transporting:
	@$(MAKE) tr.explain FILE=tr_transporting BATCH=[tr-transporting]
xp.tr.transporting.full:
	@$(MAKE) tr.explain FILE=tr_transporting_full BATCH=[tr-transporting]
xp.tr.transporting.no.agent:
	@$(MAKE) tr.explain FILE=tr_transporting_no_agent BATCH=[tr-transporting]

xp.tr.packing.all: xp.tr.packing xp.tr.packing.no.agent
xp.tr.packing:
	@$(MAKE) tr.explain FILE=tr_packing BATCH=[tr-packing]
xp.tr.packing.no.agent:
	@$(MAKE) tr.explain FILE=tr_packing_no_agent BATCH=[tr-packing]

xp.tr.unpacking.all: xp.tr.unpacking xp.tr.unpacking.full
xp.tr.unpacking:
	@$(MAKE) tr.explain FILE=tr_unpacking BATCH=[tr-unpacking]
xp.tr.unpacking.full:
	@$(MAKE) tr.explain FILE=tr_unpacking_full BATCH=[tr-unpacking]

# weighing shares the tr-examining x-plan: the verb (weigh/examine) comes from the
# dictionary, keyed on the activity type; the weight phrase is optional
xp.tr.weighing.all: xp.tr.weighing xp.tr.weighing.unit xp.tr.weighing.no.delegation xp.tr.weighing.no.scale
xp.tr.weighing:
	@$(MAKE) tr.explain FILE=tr_weighing BATCH=[tr-examining][tr-examining-no-instrument] SOURCE_NAME=tr-examining PROPERTY_NAME=tr-weighing
xp.tr.weighing.unit:
	@$(MAKE) tr.explain FILE=tr_weighing_unit BATCH=[tr-examining][tr-examining-no-instrument] SOURCE_NAME=tr-examining PROPERTY_NAME=tr-weighing
xp.tr.weighing.no.delegation:
	@$(MAKE) tr.explain FILE=tr_weighing_no_delegation BATCH=[tr-examining][tr-examining-no-instrument] SOURCE_NAME=tr-examining PROPERTY_NAME=tr-weighing
xp.tr.weighing.no.scale:
	@$(MAKE) tr.explain FILE=tr_weighing_no_scale BATCH=[tr-examining][tr-examining-no-instrument] SOURCE_NAME=tr-examining-no-instrument PROPERTY_NAME=tr-weighing
xp.tr.examining:
	@$(MAKE) tr.explain FILE=tr_examining BATCH=[tr-examining][tr-examining-no-instrument]

xp.tr.agent.init.all: xp.tr.agent.init xp.tr.agent.init.scale xp.tr.agent.init.no.location
xp.tr.agent.init:
	@$(MAKE) tr.explain FILE=tr_agent_init BATCH=[tr-agent-init]
xp.tr.agent.init.scale:
	@$(MAKE) tr.explain FILE=tr_agent_init_scale BATCH=[tr-agent-init]
xp.tr.agent.init.no.location:
	@$(MAKE) tr.explain FILE=tr_agent_init_no_location BATCH=[tr-agent-init]

xp.tr.item.init.all: xp.tr.item.init xp.tr.item.init.dated xp.tr.item.init.located
xp.tr.item.init:
	@$(MAKE) tr.explain FILE=tr_item_init BATCH=[tr-item-init]
xp.tr.item.init.dated:
	@$(MAKE) tr.explain FILE=tr_item_init_dated BATCH=[tr-item-init]
xp.tr.item.init.located:
	@$(MAKE) tr.explain FILE=tr_item_init_located BATCH=[tr-item-init]

xp.tr.assigning.all: xp.tr.assigning xp.tr.assigning.no.agent
xp.tr.assigning:
	@$(MAKE) tr.explain FILE=tr_assigning BATCH=[tr-assigning]
xp.tr.assigning.no.agent:
	@$(MAKE) tr.explain FILE=tr_assigning_no_agent BATCH=[tr-assigning]

xp.tr.handingover.all: xp.tr.handingover xp.tr.handingover.no.giver
xp.tr.handingover:
	@$(MAKE) tr.explain FILE=tr_handingover BATCH=[tr-handingover]
xp.tr.handingover.no.giver:
	@$(MAKE) tr.explain FILE=tr_handingover_no_giver BATCH=[tr-handingover]

#| xgroup_by(.)
xp.fs.all:
	@$(MAKE) xp.transforming.all xp.filtering.all xp.init.all xp.splitting.all xp.fitting.all xp.validating.all xp.approving.all | jq -s '.  '

xp.all: xp.fs.all xp.tr.all