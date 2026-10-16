# PROV-TEMPLATE specifications TODO — Outstanding Work

> **🔢 LAST ALLOCATED TASK NUMBER: S24** — the highest task id ever assigned (whether still open here or retired to
> `DONE.md`). **When allocating a new task, take the NEXT number (S25) and bump this line.** Do NOT reuse a number
> freed by retirement. Ids are prefixed `S` so as not to collide with `TEMPLATES-TODO.md`'s `T-n` in the same
> repository.
>
> Completed tasks are in `DONE.md`. Paths are relative to `provenance-templates-specification/` unless stated.
> Structure as in ProvToolbox's `TODO.md`: an index of one-line rows, the narrative in each task's section.
> Last updated: 2026-10-16 (algorithm from the book).

---

## Task Index

When a task is selected and completed: (1) remove it from this index, (2) move the task
section to `DONE.md` with a completion date, and add it to `DONE.md`'s index.

**Index format — one short sentence per task, narrative in the body.** Each row in this index is a
title / brief one-line summary *only*. ALL narrative — diagnosis, findings, design, evidence,
attempt history — belongs in the task's `###` section under `## Tasks` below, never in the index row.

| # | Task | Priority | Category | Section |
|---|---|---|---|---|
| S21 | Rule on the enumeration order: the book cycles the last name variable fastest, ProvToolbox the first. | 🔴 HIGH | Ruling / Algorithms | [S21 section](#-s21-rule-on-the-enumeration-order) |
| S23 | Check that reusing the book's algorithm text in a public specification is compatible with the publisher. | 🔴 HIGH | Ruling / Rights | [S23 section](#-s23-reuse-of-the-books-text) |
| S2 | ProvToolbox: implement `tmpl:if` / `tmpl:notif` as the book's algorithm specifies them. | 🟡 MEDIUM | ProvToolbox / Algorithms | [S2 section](#-s2-implement-tmplif-and-tmplnotif-in-provtoolbox) |
| S3 | ProvToolbox: drop relations that are not well-formed (`used(a,-,-)`), keep identified ones, as the book specifies. | 🟡 MEDIUM | ProvToolbox / Algorithms | [S3 section](#-s3-provtoolbox-well-formedness-of-relations) |
| S4 | Confirm the editor's affiliation (University of Sussex, from the book) and where the former editors appear. | 🔴 HIGH | Ruling / Front matter | [S4 section](#-s4-confirm-the-editors-affiliation) |
| S5 | `tmpl:` attributes that survive instantiation, and `tmpl:order` missing from `template.ttl`. | 🟡 MEDIUM | Vocabulary / Algorithms | [S5 section](#-s5-tmpl-attributes-that-survive-instantiation) |
| S6 | ProvToolbox: ignore a time variable bound to several values (book, vocabulary) rather than take the last. | 🔵 LOW | ProvToolbox / Algorithms | [S6 section](#-s6-provtoolbox-tmpltime-with-several-values) |
| S22 | Specify how linked ("aligned") variables enter the book's enumeration; the book leaves them out. | 🟡 MEDIUM | Algorithms | [S22 section](#-s22-linked-variables-in-the-books-enumeration) |
| S7 | Align the bindings JSON Schema and ProvToolbox (`@lang`/`@language`, single-value entries, `linked`, types, IRIs). | 🟡 MEDIUM | Schemas / Bindings | [S7 section](#-s7-align-the-bindings-json-schema-and-provtoolbox) |
| S8 | Bindings schemas: the JSON Schema rejects members the compiler reads; consider a schema of their own. | 🟡 MEDIUM | Schemas / Bindings schemas | [S8 section](#-s8-a-json-schema-for-bindings-schemas) |
| S9 | Index and TTF JSON Schemas: missing members, tuple-form `items` that only checks the first entry. | 🟡 MEDIUM | Schemas / TTF | [S9 section](#-s9-index-and-ttf-json-schemas) |
| S10 | Error signalling in ProvToolbox departs from the error table of PROV-TEMPLATE-ALGORITHMS. | 🟡 MEDIUM | ProvToolbox / Errors | [S10 section](#-s10-error-signalling-in-provtoolbox) |
| S11 | Library inconsistencies found while writing PROV-TEMPLATE-LIBRARY, to be filed in `TEMPLATES-TODO.md`. | 🟡 MEDIUM | Library | [S11 section](#-s11-library-inconsistencies-to-file) |
| S12 | Publication at openprovenance.org/templates-spec/: dated snapshots, static export, password protection while in development. | 🟡 MEDIUM | Publication | [S12 section](#-s12-publication) |
| S13 | Verify the bibliographic references (TSE paper, PROV-JSONLD URL, the book's publisher and URL). | 🔵 LOW | References | [S13 section](#-s13-verify-the-bibliographic-references) |
| S14 | Primer outputs are not reproducible: fresh `uuid:` identifiers each run; the JVM time zone decides the offsets. | 🔵 LOW | Primer / Build | [S14 section](#-s14-reproducible-primer-outputs) |
| S15 | Primer: a section on the template compiler — generated builders, compact records, `log2prov`. | 🔵 LOW | Primer | [S15 section](#-s15-primer-section-on-the-template-compiler) |
| S16 | Specify catalogues and composite templates (today only described, non-normatively). | 🔵 LOW | Specification / Compiler | [S16 section](#-s16-specify-catalogues-and-composite-templates) |
| S17 | Publish the vocabularies `fs`, `phys`, `resp`, `ptm` that the library uses. | 🔵 LOW | Library / Vocabularies | [S17 section](#-s17-publish-the-library-vocabularies) |
| S18 | Interpreter gaps: `wasInfluencedBy`, `mentionOf`, dictionaries dropped; `hadMember` with several members. | 🔵 LOW | ProvToolbox / Algorithms | [S18 section](#-s18-interpreter-gaps) |
| S19 | Link the specifications from the repository README and from openprovspec. | 🔵 LOW | Documentation | [S19 section](#-s19-link-the-specifications) |
| S20 | Check every cross-document and external link once published (in the manner of T-2). | 🔵 LOW | Quality / Web | [S20 section](#-s20-check-the-links) |

---

## Tasks

---

### 🔴 S21: Rule on the enumeration order

**Priority**: 🔴 HIGH.  **Category**: Ruling / Algorithms.  **Filed 2026-10-16** (rewriting PROV-TEMPLATE-ALGORITHMS on the book).

The book (`~/git-papers/papers/book-ptm/section-4.4.md`, §4.4.3 step 6) orders the name variables of an element
lexicographically and enumerates their Cartesian product with the **last** variable cycling fastest. ProvToolbox
(`Using`/`UsingIterator`, `index[0]` fastest) cycles the **first** fastest. Value variables are indexed by the position
`i`, so the two disagree on which copy gets which entry: in `prov-template-algorithms/examples/attribution.provn`,
`prov:type = ex:t2a, ex:t2b` lands on `(ex:en2, ex:ag1)` by the book (`attribution-expected.provn`, written by hand) and
on `(ex:en1, ex:ag2)` by ProvToolbox. The specification follows the book; appendix A notes ProvToolbox. Either
ProvToolbox changes (which reorders every existing bindings file with multi-valued value variables) or the book and the
specification change.

---

### 🔴 S23: Reuse of the book's text

**Priority**: 🔴 HIGH.  **Category**: Ruling / Rights.  **Filed 2026-10-16**.

PROV-TEMPLATE-ALGORITHMS §3–§5 follow the book's §4.4 closely (structure, cases 1–5.b.5, illustrations, errors), in
adapted wording; §6 follows §5.3.2. The book is submitted to a publisher: check that publishing this text openly, at
openprovenance.org/templates-spec/, is compatible with the agreement.

---

### 🟡 S2: Implement tmpl:if and tmpl:notif in ProvToolbox

**Priority**: 🟡 MEDIUM.  **Category**: ProvToolbox / Algorithms.  **Filed 2026-10-16**; re-scoped the same day.

The book's algorithm (cases 5.b.1, 5.b.2, 5.b.5) specifies them: `tmpl:if` keeps the element only if the variable is
bound (a sequence counts as bound), `tmpl:notif` only if it is not. ProvToolbox's interpreter implements neither:
`IFVAR_URI` is declared in `InstantiateUtil.java:38-39` and never used by `InstantiateAction`; a bound `tmpl:if`
survives in the output. The template compiler implements `tmpl:if` as a truth test, for `wasInvalidatedBy` and
qualified `hadMember` only (`StatementCompilerAction2.java`); `tmpl:notif` appears nowhere in the code.

---

### 🟡 S3: ProvToolbox well-formedness of relations

**Priority**: 🟡 MEDIUM.  **Category**: ProvToolbox / Algorithms.  **Filed 2026-10-16**; re-scoped the same day.

The book's well-formedness table (PROV-TEMPLATE-ALGORITHMS §2.1) keeps a relation if it has an identifier or its required
edge properties. ProvToolbox keeps `used(ex:writing,-,-)`, `wasDerivedFrom(ex:report, -)`, `wasInformedBy(ex:writing,-)`
(run of `prov-template-algorithms/examples/unbound.provn`), and drops an attribution, association or delegation without
agent even when it has an identifier (`InstantiateAction.java` lines 186, 207, 239). PROV-TEMPLATE 2014 had made the
first case an error, `UnboundMandatoryVariable`; the book drops the relation instead.

---

### 🔴 S4: Confirm the editor's affiliation

**Priority**: 🔴 HIGH.  **Category**: Ruling / Front matter.  **Filed 2026-10-16**.

`common/config.js` gives Luc Moreau as sole editor, **University of Sussex** (from `ptmbook/index.html`, the most recent
source); `openprovspec/index.html` and `template.html` still say King's College London, and prov-jsonld gave KCL.

Former editors: Luc ruled (2026-10-16) that Dong Huynh and Danius Michaelides are not previous authors of the
algorithm, which is now the book's. They remain former editors "(editor of the 2014-06-07 version)", University of
Southampton, with the previous-version link, on the overview and PROV-TEMPLATE only (the definitions descend from
2014); the overview acknowledges them. PROV-TEMPLATE-ALGORITHMS has Luc alone, and cites 2014 only in its appendix
"Relation to earlier work". Confirm whether they should also be dropped from the overview and PROV-TEMPLATE.

---

### 🟡 S5: tmpl attributes that survive instantiation

**Priority**: 🟡 MEDIUM.  **Category**: Vocabulary / Algorithms.  **Filed 2026-10-16**.

`template.ttl` says every `tmpl:` attribute is consumed. In ProvToolbox: a bound `tmpl:if` survives (S2); `tmpl:ID`
survives when it cannot be applied (`InstantiateAction.java:466-474`); `tmpl:linked` naming a `vargen:` variable
survives with a UUID value (384-390); `tmpl:label`/`tmpl:time` are kept on purpose when the value is a variable
(template-generating templates). `tmpl:order` (written with `-genorder`) is not in `template.ttl` at all; PROV-TEMPLATE §3.4
lists it as output-only. Add `tmpl:order` (and the compiler's reserved `tmpl:activity`, `tmpl:activityType`) to
`template.ttl`/`tmpl.html`, and decide which survivals are bugs.

---

### 🔵 S6: ProvToolbox tmpl:time with several values

**Priority**: 🔵 LOW.  **Category**: ProvToolbox / Algorithms.  **Filed 2026-10-16**; re-scoped the same day.

The book (case 5.b.2) and `template.ttl:39` ignore a time variable bound to several values;
`InstantiateAction.processTemplateAttributes` calls `setTime` for each value, so the last wins.

---

### 🟡 S22: Linked variables in the book's enumeration

**Priority**: 🟡 MEDIUM.  **Category**: Algorithms.  **Filed 2026-10-16**.

The book (§4.4.3, last paragraph) mentions "aligned" variables, iterated in lock step, "not discussed in this book and
supported by ProvToolbox". PROV-TEMPLATE-ALGORITHMS §4.4 proposes: a set of linked variables contributes one dimension to
the product, placed at the first of them in `NV_E`. To confirm, with S21 (the position of the dimension matters for the
indexing of value variables).

---

### 🟡 S7: Align the bindings JSON Schema and ProvToolbox

**Priority**: 🟡 MEDIUM.  **Category**: Schemas / Bindings.  **Filed 2026-10-16**.

`prov-template/schemas/bindings-schema.json` is a copy of ProvToolbox's
`modules-template/prov-template/src/main/resources/jsonschema/bindings-schema.json` (95386ae29). Differences found:
- language tag: the schema has `@language` (JSON-LD), `VDescriptor` reads `@lang`, and the interpreter ignores it;
- an attribute variable's entry written as a single object `[{"@value": …}]` is accepted by the code, rejected by the
  schema (`ArrayOfArraysOfValues`); the primer uses the nested form;
- the top-level `linked` member is accepted by the code (`Bindings.java`), not by the schema (`additionalProperties: false`);
- the schema admits `xsd:float` and `xsd:dateTime`; `DescriptorUtils.newValue` supports only string, int, boolean on
  ordinary attributes;
- `{"@id": "http://…"}` is split on `:` (`DescriptorUtils.java:30-41`), so full IRIs are unsupported;
- `ArrayOfArraysOfValues.items` is a one-element tuple, so only the first entry is validated (draft-07 semantics).

Editor's notes in PROV-TEMPLATE §4.2. When fixed, refresh the copy (S12 may add a `make schemas` target).

---

### 🟡 S8: A JSON Schema for bindings schemas

**Priority**: 🟡 MEDIUM.  **Category**: Schemas / Bindings schemas.  **Filed 2026-10-16**.

The same `bindings-schema.json` validates bindings and bindings schemas (the library's `validate-bindings` feeds it both).
The template compiler reads members the schema rejects: `@role`, `@escape`, `@semantic.type`, `@unique`,
`@sql.new.inputs`, `@sql.also.outputs` (name variables), `@sql.foreign` (attribute variables), `@output: "optional"`
(`OutputFieldValue`), top-level `@return` (`TemplateBindingsSchema.java`). PROV-TEMPLATE §5 documents the schema's members
and lists the others in an editor's note. Decide whether bindings schemas get their own JSON Schema (and whether
"bindings schema" or "compact bindings" is the name used in the documents; the documents say "bindings schema").

---

### 🟡 S9: Index and TTF JSON Schemas

**Priority**: 🟡 MEDIUM.  **Category**: Schemas / TTF.  **Filed 2026-10-16**.

- `prov-template/schemas/template-index-schema.json` (copy of the library's `json-schema/index-schema.json`) allows only
  `jsonld`, `provn`, `png`, `svg`; the index a TTF writes also has `qualifiedPng`, `qualifiedSvg`, `hasProvenance`.
- `template-transformation-file-schema.json`: `template_path`, `bindings_path`, `variableMaps` use tuple-form
  `"items": [ {…} ]`, which validates the first entry only; `InstantiateTask`/`MergeTask` have Java fields
  (`bindings` on merge, `variableMaps`) that the schema does not list.

---

### 🟡 S10: Error signalling in ProvToolbox

**Priority**: 🟡 MEDIUM.  **Category**: ProvToolbox / Errors.  **Filed 2026-10-16**.

PROV-TEMPLATE-ALGORITHMS §6 lists the errors a processor MUST signal. ProvToolbox today: `BundleMissing` is an
`UncheckedException`; unequal lengths in a linked group give an `IndexOutOfBoundsException` (shorter later variable) or
silent truncation (longer); too few entries for an attribute variable reuse the first with a stderr warning
(`Using.java:142-149`); unsupported literal types raise `IllegalArgumentException`; `warnAboutUnusedBindingsVariables`
removes the used variables from the caller's `bindings.var` (`Instantiater.java:122-123`, a live `keySet()` view).
Whether to file these in ProvToolbox's `TODO.md` is Luc's call.

---

### 🟡 S11: Library inconsistencies to file

**Priority**: 🟡 MEDIUM.  **Category**: Library.  **Filed 2026-10-16**.

Listed in `prov-template-library/index.html` appendix A; to be filed in `../TEMPLATES-TODO.md` (not done without Luc):
- physical pages give FQNs `physical.*`, the build `transport.*`;
- `Triangle2-Entity-SDS.md` gives the IRI of `Triangle2-Agent-SDS` and the name `Triangle-Entity-SDS`;
- the TTF of `Triangle1-Entity-UGD` names it `triangles.Triangle1-UGD`;
- `cbindings/…/fs/file-merging.json` is a copy of `file-splitting.json`; `FileMerging` is not in `fs-catalogue.json`;
- T-1 has moved on: `Product2-2.md` exists (its icon is still missing); eight pages remain;
- the root `README.md` still gives `org.openprovenance.bookptm:template-intro1` (T-3).

---

### 🟡 S12: Publication

**Priority**: 🟡 MEDIUM.  **Category**: Publication.  **Filed 2026-10-16**.

Published at <https://openprovenance.org/templates-spec/> (Luc, 2026-10-16): `common/config.js` sets `latestVersion` to
`https://openprovenance.org/templates-spec/<shortName>/`, and the documents refer to each other by relative links
(`../prov-template/`); `index.html` is the landing page. Password protection while in development: nginx basic auth in
the server block (no `.htaccess` in nginx). To decide: dated snapshots in the manner of prov-jsonld (`YYYY-MM-DD/` directories, top-level links); exporting static HTML from ReSpec (the documents load
ReSpec from w3.org and include files with `data-include`, which needs http); a `make schemas` target refreshing the
schema copies from ProvToolbox.

---

### 🔵 S13: Verify the bibliographic references

**Priority**: 🔵 LOW.  **Category**: References.  **Filed 2026-10-16**.

Written from memory, to check: `PROV-TEMPLATE-TSE` (Moreau, Batlajery, Huynh, Michaelides, Packer, "A Templating System
to Generate Provenance", IEEE TSE 44(2), 2018, doi 10.1109/TSE.2017.2659745); `PROV-JSONLD`
(`https://www.w3.org/submissions/prov-jsonld/`); `PTM-BOOK` (no publisher or URL known; `ptmbook/index.html` gives title,
author, 2026); `OPENPROV` and `TMPL-VOCAB` URLs.

---

### 🔵 S14: Reproducible primer outputs

**Priority**: 🔵 LOW.  **Category**: Primer / Build.  **Filed 2026-10-16**.

`make examples` regenerates `prov-template-primer/examples/run/`; unbound `vargen:` activities get fresh `uuid:`
identifiers each time, so every regeneration changes the files. Times print in the JVM's time zone: `-dateTime` and
`-timeZone` do not affect instantiation, so the Makefile runs with `TZ=UTC JAVA_OPTS=-Duser.timezone=UTC`. A seeded UUID
option in ProvToolbox, or binding the `vargen:` activities in the primer, would make the outputs stable.

---

### 🔵 S15: Primer section on the template compiler

**Priority**: 🔵 LOW.  **Category**: Primer.  **Filed 2026-10-16**.

The primer stops at bindings schemas and TTFs. A section could compile a small catalogue for the pipeline templates,
show a generated builder and a compact record, and run `provconvert -log2prov`. Keep clear of the book's PLEAD
(ML pipeline) and gzip examples.

---

### 🔵 S16: Specify catalogues and composite templates

**Priority**: 🔵 LOW.  **Category**: Specification / Compiler.  **Filed 2026-10-16**.

PROV-TEMPLATE §8.4 describes the template compiler and its catalogue non-normatively. The catalogue has its own JSON
Schema (`prov-template-compiler/src/main/resources/schema/template-project-schema.json`); composite templates
(`consistsOf`, `sharing`) are a compiler notion only. Decide whether they become normative.

---

### 🔵 S17: Publish the library vocabularies

**Priority**: 🔵 LOW.  **Category**: Library / Vocabularies.  **Filed 2026-10-16**.

`fs` (`http://openprovenance.org/ns/fs#`), `phys`, `resp` (`…/ns/responsibility#`) and `ptm` are used by the library
(`ttfs/common-ns.json`) and defined nowhere in `openprovspec/`. Note also the http/https split: `tmpl`, `fs`, `phys`, …
are http, `openprov` and `provext` https.

---

### 🔵 S18: Interpreter gaps

**Priority**: 🔵 LOW.  **Category**: ProvToolbox / Algorithms.  **Filed 2026-10-16**.

`InstantiateAction` has no-op methods for `wasInfluencedBy`, `mentionOf` and the dictionary relations (always dropped),
and throws `UnsupportedOperationException` for `hadMember` with several members. Merge (`IndexedDocument`) cannot merge
`mentionOf` and dictionaries, and does not write anonymous qualified memberships and identified `alternateOf`. Noted in
PROV-TEMPLATE-ALGORITHMS.

---

### 🔵 S19: Link the specifications

**Priority**: 🔵 LOW.  **Category**: Documentation.  **Filed 2026-10-16**.

Add the specification folder to `../README.md`, and link it from `openprovspec/template.html` and the library's landing
page.

---

### 🔵 S20: Check the links

**Priority**: 🔵 LOW.  **Category**: Quality / Web.  **Filed 2026-10-16**.

Once published (S12), crawl the five documents (cf. `provenance-templates-library/scripts/crawl_templates.py`, T-2): the
template IRIs in the library document, the `examples/` directory links of the primer, and the references.
