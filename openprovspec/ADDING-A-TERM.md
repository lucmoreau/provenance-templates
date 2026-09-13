# Adding a term to the OpenProv vocabulary

The vocabulary is written once, here, and read from here by the template
library (`provenance-templates-library/template-pages/ns/openprov.{ttl,jsonld}` and `openprov-schema.json`
are symlinks to this directory) and by ProvToolbox (which keeps a byte-identical
copy of the context, pinned by a test). Three kinds of term exist, and each
touches a different set of files:

| kind | example | ttl | jsonld + schema | ProvToolbox |
|---|---|---|---|---|
| property of a scoped relation (attribution, membership, specialization, communication, start, end) | `openprov:hadPreviousEntity`, written `previousEntity` | yes | yes | yes |
| role | `openprov:asMember` | yes | no | no |
| type (class) | `openprov:InsertingItemIntoCollection` | yes | no | no |

Properties follow PROV-O's convention for a qualified derivation: the property
is `hadX` (`openprov:hadActivity`), and documents write it without `had`
(`openprov:activity` in PROV-N and PROV-JSON, the bare `activity` in
PROV-JSONLD), the way PROV-N writes `prov:type`. The written name is the *term*,
the `hadX` name the *property*; both appear below.

## 1. In this directory

1. `openprov.ttl` — define the term in the section for its kind, with
   `rdfs:domain`, `rdfs:range` (properties), `rdfs:label` and `rdfs:comment`.
   A property scoped to two relations takes a `owl:unionOf` domain, as
   `:hadActivity` does. Name it `hadX`; if the same word already names
   something else on the relation (`entity` beside `specificEntity` and
   `generalEntity`), choose a distinct one (`previousEntity`).
2. `openprov.jsonld` — properties only: add the term to the type-scoped
   `@context` of the relation (`Attribution`, `Membership`, `Specialization`,
   `Communication`, `Start`, `End`; a new relation restates the specification's
   scope for it, and the library's `OpenprovContextTest.EXTENDED` lists it):
   `"x": { "@id": "openprov:hadX", "@type": "@id" }`. Roles and types need no
   context entry; they are values, written as `openprov:asMember`.
3. `make html` — refreshes the generated ontology and context sections of
   `index.html`, the specification page, from the two files. Hand-written prose
   in `index.html` is left alone: edit it if the new term deserves a mention.
   `make standalone` writes `openprov.html` and `context.html`, the same
   sections as self-contained pages; run it too, so they stay in step.
4. `make schema` — regenerates `openprov-schema.json`: the published
   PROV-JSONLD schema extended by reference (`script/schema-extend.py`), restating
   only the scoped relations, each with one property per term of its
   context scope, and the document-to-statement chain. A property needs no
   further step; a validator resolves the PROV-JSONLD schema alongside
   (`ajv validate -s openprov-schema.json -r <spec schema.json> -d doc.jsonld`).

## 2. In ProvToolbox (properties only; roles and types need nothing)

Branch `development2_0`, module `modules-core`:

1. `prov-model/src/main/java/org/openprovenance/prov/model/OpenprovTerms.java`
   — add the pair `"x", "hadX"` to the `scope(Kind.PROV_..., ...)` of its
   relation (a new relation also needs its JSON-LD mixin `JLD_...` given the
   scoped key handlers, `ScopedKey(De)Serializer.<Relation>`, and the PROV-N,
   PROV-JSON and Scala sites the existing ones have). This table is what every parser and serialiser consults: PROV-N
   (`TreeTraversal`, `NotationConstructor`), PROV-JSON (`ProvJsonReader`,
   `ProvJsonWriter`), PROV-JSONLD (`ScopedKeyDeserializer`,
   `ScopedKeySerializer`) and the Scala model (`OpenprovAttributes`).
2. `prov-jsonld/src/main/resources/openprov-context/openprov.jsonld` and
   `openprov-schema.json` — copy the two files over them, byte for byte.
   `OpenprovTermsTest` fails until the table, the context and the schema agree;
   the round-trip tests hold every document citing the openprov context to the
   schema (the specification's schema resolved from the module's own copy).
3. Tests naming the terms, to extend with the new one:
   `prov-n/.../notation/test/OpenprovAttributesTest.java`,
   `prov-jsonld/.../json/test/OpenprovAttributesJsonTest.java`,
   `prov-jsonld/.../jsonld11/test/OpenprovTermsTest.java` with its inputs
   `prov-jsonld/src/test/resources/openprov/{attribution,membership,specialization}.jsonld`,
   `prov-model-scala/src/test/scala/.../OpenprovAttributesSpec.scala`.
4. `RELEASES.md` — one line.
5. Build in full, from the ProvToolbox root, with the environment sourced
   (`~/.bashrc` sets the database and JDK the integration tests need):

       bash -c 'source ~/.bashrc; mvn -o install -Dmaven.javadoc.skip=true'

## 3. In the template library (`provenance-templates-library`)

1. Bindings and templates that use the term: under `src/main/resources/bindings`
   the term is a value, `{"@id": "openprov:x"}`; under `src/main/resources/templates`
   an attribute, `openprov:x = 'var:...'`. A bindings file that names openprov
   declares it in its `context` as `"openprov": "${openprov_ns}"`
   (`src/main/resources/ttfs/common-ns.json` maps the variable).
2. `template-pages/org/openprovenance/templates/**/*.md` — the page prose, if
   it lists the relation's attributes. The `.md` is the source; regenerate its
   `.html`, `.json` and `.yaml` with

       make -f template-pages/Makefile FILE=`pwd`/template-pages/org/openprovenance/templates/<package>/<Template> do.file

   (pandoc, pandoc-crossref, mermaid-filter, yq and jq on the path).
3. Rebuild, which regenerates `target/generated-templates` with the rebuilt
   toolbox, validates every bindings file and runs `OpenprovContextTest`, the
   drift tests between `openprov.jsonld` and the PROV-JSONLD context it extends, and between `openprov-schema.json` and the schema and context it extends:

       bash -c 'source ~/.bashrc; mvn -o clean install -Dmaven.javadoc.skip=true'

4. Check the output: no `openprov:` compact key should appear in a generated
   `.jsonld` (the serialiser writes the bare term), and every generated `.provn`
   should read back through `provconvert`.

## 4. Publish

1. Commit `provenance-templates` (this directory and the library together, so
   the symlinked files and the pages that cite them move as one) and push.
2. **Manual, by Luc:** on openprovenance.org, pull `provenance-templates`. That
   refreshes https://openprovenance.org/ns/openprov (the Turtle, also at
   `openprov.ttl`), https://openprovenance.org/ns/openprov.html (this
   `index.html`), https://openprovenance.org/ns/openprov.jsonld — the context
   ProvToolbox's PROV-JSONLD output cites, so processors resolve the new term
   only once this step is done — and https://openprovenance.org/ns/openprov-schema.json.
3. Push ProvToolbox.

## The template vocabulary (`template.ttl`)

`template.ttl` defines `http://openprovenance.org/ns/tmpl#`, the reserved
attributes of provenance templates, served at https://openprovenance.org/ns/tmpl
through `template-pages/ns/tmpl.ttl`. Its terms are ProvToolbox's
(`InstantiateUtil` in prov-template: `label`, `time`, `startTime`, `endTime`,
`ID`, `linked`; `if` in the compiler); a new one is added there, with its
handling, and here. The namespace was `http://openprovenance.org/tmpl#` until
2026-09-13; ProvToolbox accepts that on input (`LEGACY_TMPL_NS`) and never
writes it.
