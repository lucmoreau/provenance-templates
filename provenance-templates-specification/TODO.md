# PROV-TEMPLATE specifications TODO — Outstanding Work

> **🔢 LAST ALLOCATED TASK NUMBER: S20** — the highest task id ever assigned (whether still open here or retired to
> `DONE.md`). **When allocating a new task, take the NEXT number (S21) and bump this line.** Do NOT reuse a number
> freed by retirement. Ids are prefixed `S` so as not to collide with `TEMPLATES-TODO.md`'s `T-n` in the same
> repository.
>
> Completed tasks are in `DONE.md`. Paths are relative to `provenance-templates-specification/` unless stated.
> Structure as in ProvToolbox's `TODO.md`: an index of one-line rows, the narrative in each task's section.
> Last updated: 2026-10-16.

---

## Task Index

When a task is selected and completed: (1) remove it from this index, (2) move the task
section to `DONE.md` with a completion date, and add it to `DONE.md`'s index.

**Index format — one short sentence per task, narrative in the body.** Each row in this index is a
title / brief one-line summary *only*. ALL narrative — diagnosis, findings, design, evidence,
attempt history — belongs in the task's `###` section under `## Tasks` below, never in the index row.

| # | Task | Priority | Category | Section |
|---|---|---|---|---|
| S2 | Rule on `tmpl:if` / `tmpl:notif`: specified and in the vocabulary, not implemented by the interpreter. | 🔴 HIGH | Ruling / Algorithms | [S2 section](#-s2-rule-on-tmplif-and-tmplnotif) |
| S3 | Rule on relations with an empty mandatory slot (`used(a,-,-)`): error, as in 2014, or leave them out. | 🔴 HIGH | Ruling / Algorithms | [S3 section](#-s3-rule-on-relations-with-an-empty-mandatory-slot) |
| S4 | Confirm the editor's affiliation (University of Sussex, from the book) and the former-editor wording. | 🔴 HIGH | Ruling / Front matter | [S4 section](#-s4-confirm-the-editors-affiliation) |
| S5 | `tmpl:` attributes that survive instantiation, and `tmpl:order` missing from `template.ttl`. | 🟡 MEDIUM | Vocabulary / Algorithms | [S5 section](#-s5-tmpl-attributes-that-survive-instantiation) |
| S6 | Rule on `tmpl:time` bound to several values: vocabulary says ignored, code takes the last. | 🟡 MEDIUM | Ruling / Vocabulary | [S6 section](#-s6-rule-on-tmpltime-with-several-values) |
| S7 | Align the bindings JSON Schema and ProvToolbox (`@lang`/`@language`, single-value entries, `linked`, types, IRIs). | 🟡 MEDIUM | Schemas / Bindings | [S7 section](#-s7-align-the-bindings-json-schema-and-provtoolbox) |
| S8 | Bindings schemas: the JSON Schema rejects members the compiler reads; consider a schema of their own. | 🟡 MEDIUM | Schemas / Bindings schemas | [S8 section](#-s8-a-json-schema-for-bindings-schemas) |
| S9 | Index and TTF JSON Schemas: missing members, tuple-form `items` that only checks the first entry. | 🟡 MEDIUM | Schemas / TTF | [S9 section](#-s9-index-and-ttf-json-schemas) |
| S10 | Error signalling in ProvToolbox departs from the error table of PROV-TEMPLATE-ALGORITHMS. | 🟡 MEDIUM | ProvToolbox / Errors | [S10 section](#-s10-error-signalling-in-provtoolbox) |
| S11 | Library inconsistencies found while writing PROV-TEMPLATE-LIBRARY, to be filed in `TEMPLATES-TODO.md`. | 🟡 MEDIUM | Library | [S11 section](#-s11-library-inconsistencies-to-file) |
| S12 | Decide publication: URLs, dated snapshots as in prov-jsonld, static export of the ReSpec documents. | 🟡 MEDIUM | Publication | [S12 section](#-s12-publication) |
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

### 🔴 S2: Rule on tmpl:if and tmpl:notif

**Priority**: 🔴 HIGH.  **Category**: Ruling / Algorithms.  **Filed 2026-10-16** (writing PROV-TEMPLATE-ALGORITHMS).

`openprovspec/template.ttl` defines `tmpl:if` (instantiate the statement only if the variable is bound) and `tmpl:notif`
(its complement); `prov-template/index.html` §3.4 and `prov-template-algorithms/index.html` §4.6 specify them as such.
ProvToolbox's interpreter implements neither: `IFVAR_URI` is declared in `InstantiateUtil.java:38-39` and never used by
`InstantiateAction`. A statement carrying `tmpl:if` is instantiated whatever the binding, and a bound `tmpl:if` survives
in the output (seen in the primer: `agent(ex:grace,[…, tmpl:if = 'ex:grace'])` before the primer was rewritten to
rely on the unbound-node rule instead). The template compiler implements `tmpl:if` as a truth test, for
`wasInvalidatedBy` and qualified `hadMember` only (`StatementCompilerAction2.java:233-256, 521-538, 601-617`);
`tmpl:notif` appears nowhere in the code.

**Options.** (a) implement both in `InstantiateAction` (drop the statement per the vocabulary, consume the attribute),
and align the compiler on "bound" rather than "true"; (b) withdraw them from the vocabulary and the specifications.
Editor's notes are in both documents until ruled.

---

### 🔴 S3: Rule on relations with an empty mandatory slot

**Priority**: 🔴 HIGH.  **Category**: Ruling / Algorithms.  **Filed 2026-10-16**.

`prov-template-algorithms/examples/unbound-expanded.provn` shows that ProvToolbox keeps `used(ex:writing,-,-)`,
`wasDerivedFrom(ex:report, -)` and `wasInformedBy(ex:writing,-)` when their second argument is an unbound `var:`
variable. These are not valid PROV-N. PROV-TEMPLATE 2014-06-07 made this the error `UnboundMandatoryVariable`; today only
nodes, attributions, associations and delegations are left out (`InstantiateAction.java` lines 77, 144, 186, 207, 239,
326). The algorithms document states the current behaviour, with an `issue` box in §4.6.

**Options.** (a) restore the error; (b) leave such relations out, like nodes without identifier (consistent with the
current treatment of attributions); (c) keep them but require `-allexpanded` for valid PROV.

---

### 🔴 S4: Confirm the editor's affiliation

**Priority**: 🔴 HIGH.  **Category**: Ruling / Front matter.  **Filed 2026-10-16**.

`common/config.js` gives Luc Moreau as sole editor, **University of Sussex** (from `ptmbook/index.html`, the most recent
source); `openprovspec/index.html` and `template.html` still say King's College London, and prov-jsonld gave KCL. Danius
Michaelides and Trung Dong Huynh are former editors "(editor of the 2014-06-07 version)", with University of Southampton
as their 2014 affiliation, on the overview, PROV-TEMPLATE and PROV-TEMPLATE-ALGORITHMS (the three documents that
descend from 2014), not on the primer and the library (new documents); the overview acknowledges them, and the status
sections of PROV-TEMPLATE and PROV-TEMPLATE-ALGORITHMS name them. Confirm or adjust.

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

### 🟡 S6: Rule on tmpl:time with several values

**Priority**: 🟡 MEDIUM.  **Category**: Ruling / Vocabulary.  **Filed 2026-10-16**.

`template.ttl:39` says a `tmpl:time` variable bound to several values is ignored; `InstantiateAction.processTemplateAttributes`
calls `setTime` for each value, so the last wins. PROV-TEMPLATE requires a single value; the algorithms document notes the
behaviour. Pick one and align the other.

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

`common/config.js` sets `latestVersion` to `https://openprovenance.org/<shortName>/` for each document, and the
documents refer to each other by relative links (`../prov-template/`). To decide: the URLs; dated snapshots in the
manner of prov-jsonld (`YYYY-MM-DD/` directories, top-level links); exporting static HTML from ReSpec (the documents load
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
