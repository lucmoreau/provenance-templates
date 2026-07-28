
BOOK_DIR=/Users/luc/git-papers/papers/book-ptm
PAGES_DIR=`pwd`/template-pages


go:
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/collections/InsertingIntoCollection do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/collections/RemovingFromCollection do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/fs/FileFiltering do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/fs/FileSplitting do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/fs/FileTransforming do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/generic/Forking2 do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/generic/Joining2 do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/generic/Parallel2 do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/generic/Product2 do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/generic/Transforming1 do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/physical/Packing do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/physical/Transporting do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/physical/Unpacking do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/physical/Weighing do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/responsibility/AgentInit do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/responsibility/Assigning do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/responsibility/Examining do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/responsibility/HandingOver do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle1-Agent-UGD do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle1-Entity-UGD do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle2-Agent-SDS do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle2-Entity-SDS do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle3-AGA do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle4-AIA do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/triangles/Triangle5-GGM do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/ptm/Instantiating do.file
	@$(MAKE) -f $(MAKEFILE_LIST)  FILE=$(PAGES_DIR)/org/openprovenance/templates/ptm/Merging do.file


TMP_TMPLATE=/tmp/tmplate.md

do.file:
	@cat  $(FILE).md | sed 's/(project\/template\-intro1/(\/Users\/luc\/IdeaProjects\/provenance\-templates/g' > $(TMP_TMPLATE)
	@pandoc -F pandoc-crossref -F mermaid-filter -L $(BOOK_DIR)/filters/columns.lua --toc --toc-depth=3  --lof  -V papersize:a4 --metadata linkReferences=true --citeproc --embed-resources --standalone --syntax-definition $(BOOK_DIR)/highlight/sparql.xml $(TMP_TMPLATE) > $(FILE).html
	@sed 's/**//g' $(FILE).md | sed 's/`//g' | sed 's/!\[/- Figure: (/g' | sed 's/\[@/(/g'  | sed 's/\[/(/g' | sed 's/\]/)/g' | yq -o json -p yaml - | jq 'add' > $(FILE).json
	@yq -o yaml -p json $(FILE).json > $(FILE).yaml
	@yq $(FILE).json
	@yq $(FILE).yaml



go1:
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/collections/InsertingIntoCollection do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/collections/RemovingFromCollection do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Packing do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Unpacking do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Weighing do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Transporting do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle1-Entity-UGD do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle1-Agent-UGD do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle2-Entity-SDS do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle2-Agent-SDS do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle3-AGA do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle4-AIA do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle5-GGM do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/HandingOver do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/Assigning do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/Examining do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/AgentInit do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileTransforming do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileFiltering do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileSplitting do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileMerging do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Forking2 do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Joining2 do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Parallel2 do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Product2 do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Transforming1 do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/ptm/Instantiating do.file1
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/ptm/Merging do.file1


do.file1:
	@echo $(FILE)
	@cp $(FILE).md tmp.md
	@pandoc -F pandoc-crossref -F mermaid-filter -L filters/columns.lua --toc --toc-depth=3  --lof  -V papersize:a4  --metadata  linkReferences=true --bibliography fullbib.bib --citeproc --embed-resources --standalone --syntax-definition highlight/sparql.xml tmp.md > $(FILE).html
	@rm -f $(FILE)
	@cd `dirname $(FILE)`; ln -s `basename $(FILE).html` `basename $(FILE)`
	@sed 's/**//g' $(FILE).md | sed 's/`//g' | sed 's/!\[/- Figure: (/g' | sed 's/\[@/(/g'  | sed 's/\[/(/g' | sed 's/\]/)/g' | yq -o json -p yaml - | jq 'add' > $(FILE).json
	@yq -o yaml -p json $(FILE).json > $(FILE).yaml
#	@yq $(FILE).json
#	@yq $(FILE).yaml


test:
	@echo `dirname $(FILE)`
	@echo `basename $(FILE)`


go1.link:
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/collections/InsertingIntoCollection do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/collections/RemovingFromCollection do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Packing do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Unpacking do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Weighing do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/physical/Transporting do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle1-Entity-UGD do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle1-Agent-UGD do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle2-Entity-SDS do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle2-Agent-SDS do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle3-AGA do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle4-AIA do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/triangles/Triangle5-GGM do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/HandingOver do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/Assigning do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/Examining do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/responsibility/AgentInit do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileTransforming do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileFiltering do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileSplitting do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/fs/FileMerging do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Forking2 do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Joining2 do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Parallel2 do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Product2 do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/generic/Transforming1 do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/ptm/Instantiating do.link
	@$(MAKE) -f template-library/Makefile FILE=template-library/org/openprovenance/templates/ptm/Merging do.link


do.link:
	@echo $(FILE)
	@rm -f $(FILE)
	@cd `dirname $(FILE)`; ln -s `basename $(FILE).html` `basename $(FILE)`
