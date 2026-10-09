# TEMPLATES-TODO — Outstanding Work

> **🔢 LAST ALLOCATED TASK NUMBER: T-6** — the highest task id ever assigned (whether still open here
> or retired to `TEMPLATES-DONE.md`). **When allocating a new task, take the NEXT number and bump this
> line.** Do NOT reuse a number freed by retirement — a retired task is gone from the index but its
> number is still taken.
>
> Self-contained reference — survives memory compaction.
> Completed tasks are retired to `TEMPLATES-DONE.md` (create it on first retirement), each entry
> recording the task ID, completion date, what was done, and the key files changed.
> All paths relative to `/Users/luc/IdeaProjects/provenance-templates/provenance-templates-library/`
> unless stated.
> Last updated: 2026-10-09.

---

## Task Index

When a task is selected and completed: (1) remove it from this index, (2) move the task
section to `TEMPLATES-DONE.md` with a completion date.

**Index format — one short sentence per task, narrative in the body.** Each row is a title / brief
one-line summary *only*; all narrative (context, findings, design, evidence) belongs in the task's
`###` section under `## Tasks` below.

| # | Task | Priority | Category | Section |
|---|---|---|---|---|
| T-1 | Create the missing template documentation pages (`.md`) for nine templates (fs ×5, generic ×1, physical ×3). | 🟡 MEDIUM | Documentation | [T-1 section](#-t-1-create-the-missing-template-documentation-pages) |
| T-2 | Check all URLs of the published template pages with the crawler script. | 🔵 LOW | Quality / Web | [T-2 section](#-t-2-check-all-urls-of-the-published-template-pages) |
| T-3 | Change the package in which Java code is generated — remove `bookptm`. | 🟡 MEDIUM | Build / Naming | [T-3 section](#-t-3-change-the-package-in-which-java-code-is-generated--remove-bookptm) |
| T-4 | Add a "How to use the template library" page to the template web site, referring to the workflows and the book. | 🟡 MEDIUM | Documentation | [T-4 section](#-t-4-add-a-how-to-use-the-template-library-page-to-the-template-web-site) |
| T-5 | A merge's provenance record names all its inputs, not the first two: one record per input, sharing the activity id. | 🟡 MEDIUM | Provenance / ptm | [T-5 section](#-t-5-a-merges-provenance-record-names-all-its-inputs) |
| T-6 | Provenance records name templates and inputs as the outputs they are, so the joined records form one graph. | 🟡 MEDIUM | Provenance / ptm | [T-6 section](#-t-6-provenance-records-identify-templates-by-their-output-path) |

---

## Tasks

### 🟡 T-1: Create the missing template documentation pages

**Status**: OPEN (created 2026-07-28).
**Priority**: 🟡 MEDIUM. **Category**: Documentation.

**Goal.** Nine templates have index buttons (and, for all but one, icons) but no documentation
page. Create the `.md` source for each, following the structure of the existing pages
(e.g. `template-pages/org/openprovenance/templates/physical/Packing.md`):

- `template-pages/org/openprovenance/templates/fs/FileApproving.md`
- `template-pages/org/openprovenance/templates/fs/FileInit.md`
- `template-pages/org/openprovenance/templates/fs/FileTraining.md`
- `template-pages/org/openprovenance/templates/fs/FileTransformingComposite.md`
- `template-pages/org/openprovenance/templates/fs/FileValidating.md`
- `template-pages/org/openprovenance/templates/generic/Product2-2.md`
- `template-pages/org/openprovenance/templates/physical/EntityInit.md`
- `template-pages/org/openprovenance/templates/physical/PackingComposite.md`
- `template-pages/org/openprovenance/templates/physical/UnpackingComposite.md`

**Page structure to follow** (see any existing page, e.g. `triangles/Triangle3-AGA.md`):
front-matter bullet list — **Name**, **Fully Qualified Name**, **IRI**
(`https://openprovenance.org/templates/org/openprovenance/templates/<family>/<Name>`),
**Purpose**, **Context**, **Design considerations**, **Automation** (link to the relevant
`src/main/resources/ttfs/config-*.json` when one exists) — followed by the template figure
(the generated `*.qualified.png` / `*.svg` under `target/generated-templates/...`).

**Also needed for each page:**
- a `do.file` line in `template-pages/Makefile`'s `go` target (pandoc renders `.md` →
  `.html`/`.json`/`.yaml`; see the existing entries);
- source material: the templates themselves under
  `src/main/resources/templates/org/openprovenance/templates/...` and the generated variants in
  `target/generated-templates/`; bindings schemas under `src/main/resources/bindings/...` document
  the variables (Purpose/Context prose can draw on the catalogue descriptions in
  `src/main/resources/catalogue/*.json`).

**Notes.**
- Icons already exist in `src/main/resources/icons/` (and are copied to `template-pages/icons/`
  by `make -f template-pages/Makefile icons`) for all of these except `generic/Product2-2`;
  design that icon alongside its page (the `generic/Product2` icon is the natural starting
  point — a second variant of the product pattern).
- `Product2-2` has no index button yet either — add it to the Generic category of
  `template-pages/index.html` when the page exists.

**DoD.** All nine `.md` files exist and render through `do.file` without pandoc errors; the
buttons on `index.html` resolve to the generated pages; `Product2-2` has button + icon.

---

### 🔵 T-2: Check all URLs of the published template pages

**Status**: OPEN (created 2026-07-28).
**Priority**: 🔵 LOW. **Category**: Quality / Web.

**Goal.** Verify that every URL reachable from the published template site resolves, using the
crawler script.

**The script.** `scripts/crawl_templates.py` — crawls from the seed
`https://openprovenance.org/templates/`, extracting every `href`/`src` and following any
discovered URL sharing the seed prefix; outputs the unique URL list sorted alphabetically.
(Note: the task was stated as "the script in `src/main/script`", but that directory currently
holds only `extract-svg-symbols.sh`; the URL crawler lives in `scripts/`. Consider moving it to
`src/main/script/` for consistency as part of this task.)

**Work items.**
1. Extend/complement the crawler so that, beyond *collecting* URLs, it *checks* them: report the
   HTTP status of every discovered URL (including external ones referenced from the pages, and
   non-text resources such as images/icons, which `fetch()` currently skips for extraction but
   which should still be status-checked).
2. Run it against the published site after the next deployment of `template-pages` (which now
   includes the `icons/` directory and the new buttons — several buttons intentionally point to
   pages that do not exist yet; cross-reference with T-1 rather than treating those as
   regressions).
3. Fix any genuinely broken links (or record them as expected-pending against T-1).

**DoD.** A clean crawler/checker run (no unexpected non-2xx URL), with the expected-pending list
empty once T-1 is done.

---

### 🟡 T-3: Change the package in which Java code is generated — remove `bookptm`

**Status**: OPEN (created 2026-07-28).
**Priority**: 🟡 MEDIUM. **Category**: Build / Naming.

**Goal.** The `bookptm` name (a leftover of the book-PTM project origins) should disappear from
the packages of the code in and generated by this library.

**Inventory of where `bookptm` (and the related `book` generated packages) appear today:**
- **Maven coordinates**: `groupId org.openprovenance.bookptm` (`pom.xml`), parent
  `org.openprovenance.bookptm:book-ptm` (`pom-jsweet.xml`).
- **Hand-written Java**: `src/main/java/org/openprovenance/bookptm/` (`App`, and the
  `workflows/GenerateBoxWorkflow` + `GeneratePleadWorkflow` generator classes) and ~15 test
  classes under `src/test/java/org/openprovenance/bookptm/`.
- **Python tests**: `src/test/python/org/openprovenance/bookptm/` (referenced by the
  `pom.xml` exec executions' arguments and `PYTHONPATH`s).
- **Catalogue configuration** (drives the *generated* packages):
  `src/main/resources/catalogue/*.json` — the per-template `"package"` fields
  (e.g. `org.openprovenance.book.physical`, `org.openprovenance.book.responsibility`,
  `org.openprovenance.book.fs`) and the `"past-generators"` class references
  (`org.openprovenance.bookptm.workflows.*`).

**Design decisions to make first.**
- Target package naming — e.g. `org.openprovenance.templates.<family>` for generated beans/builders
  (aligning with the template FQNs) and a matching home for the generator classes.
- Whether the Maven `groupId` changes too, and in which release: **external consumers depend on
  it** — the ProvToolbox archetype (`templateLibraryGroupId=org.openprovenance.bookptm`,
  `modules-tools/prov-template-archetype/Makefile` in ProvToolbox) and the generated book
  services resolve `org.openprovenance.bookptm:template-intro1`; a rename must be coordinated
  there (cf. the root `provenance-templates` aggregator, which already adopted
  `org.openprovenance.templates`).

**Work items.** Rename the catalogue `"package"` entries and generator classes; move the
hand-written Java/Python sources; update the `pom.xml` exec paths/PYTHONPATHs and any
SQL/webjar paths that embed package names; regenerate and rebuild; coordinate the groupId change
with the ProvToolbox archetype defaults if in scope.

**DoD.** No `bookptm` occurrence remains in this library's sources or generated output
(`grep -r bookptm src/ target/generated-sources/` empty), the library builds green, and — if the
groupId changes — the ProvToolbox archetype builds a working book service against the new
coordinates.

---

### 🟡 T-4: Add a "How to use the template library" page to the template web site

**Status**: OPEN (created 2026-07-28).
**Priority**: 🟡 MEDIUM. **Category**: Documentation.

**Goal.** The template web site (`template-pages/`, published at
`https://openprovenance.org/templates/`) currently offers only the per-template pages reached
from `index.html`; there is no page explaining *how to use the library as a whole*. Create a new
"How to use the template library" page that walks a newcomer through the library: what the
template families are, how to pick a template, how to instantiate it (bindings), and how the
generated code (beans/builders) is consumed from an application.

**Two sources to draw on and link to:**
- **The workflows** — the generator workflows in
  `src/main/java/org/openprovenance/bookptm/workflows/` (`GenerateBoxWorkflow`,
  `GeneratePleadWorkflow`): explain their role as worked end-to-end examples of composing
  library templates into an application workflow, and reference them from the page.
  (Package paths will change under T-3 — write the page so the rename only touches links.)
- **The book** — the book-PTM material (`BOOK_DIR=/Users/luc/git-papers/papers/book-ptm` in
  `template-pages/Makefile`): refer the reader to the book (relevant chapters) for the
  underlying provenance template method, and reuse/adapt its expository material where
  appropriate rather than writing from scratch.

**Work items.**
1. Write the page as `.md` under `template-pages/` (top level, alongside `index.html` — it is
   site-wide, not per-template), following the pandoc pipeline of the existing pages.
2. Add a `do.file` line for it in `template-pages/Makefile`'s `go` target so it renders to
   `.html`/`.json`/`.yaml` like the template pages.
3. Link it prominently from `index.html` (e.g. an intro/"Getting started" link above the
   category buttons).
4. Cross-reference: the workflows (with links to the sources or rendered listings), the book
   (chapter references), and a few representative template pages as running examples.

**DoD.** The page renders through `do.file` without pandoc errors, is reachable from
`index.html`, and contains working references to both the workflows and the book (the T-2
crawler run should pick it up and report no broken links from it).

---

### 🟡 T-5: A merge's provenance record names all its inputs

**Status**: OPEN (created 2026-10-09; option B ruled 2026-10-09).
**Priority**: 🟡 MEDIUM. **Category**: Provenance / ptm.

**Finding (2026-10-09).** Every ttfs task writes a `hasProvenance` PROV-CSV record. A merge task's
record is a `ptm_merging` record, and that template has exactly two inputs (`template1`,
`template2`). ProvToolbox's `MergeTask` (`modules-template/prov-template/src/main/java/org/openprovenance/prov/template/core/ttf/MergeTask.java`,
the `Ptm_mergingBean` fill near the end) sets `bean.template1`/`bean.template2` from the first two
`inputs` and drops the rest. 21 of the library's 22 merges have more than two inputs (3 to 8; e.g.
`collections/inserting/collection-inserting` and `responsibility/assigning/assigning` have 8,
`ptm/creating-template/creating-template` has 4), so their records lose 1 to 6 inputs each. Only
the merge in `config-generic-transforming.json` is a true two-input merge.

**How `-log2prov` reads a record.** `provconvert -log2prov <Init>` calls `<Init>.main`. That
`Init` class is generated from one catalogue; the ptm records use
`org.openprovenance.prov.template.library.ptm.Init`, generated from ProvToolbox's `tp_ptm.json`.
`Init` registers one `FileBuilder` per template in the catalogue. ProvToolbox's `FileBuilder`
(`modules-template/prov-template-compiler/.../log2prov/FileBuilder.java`, `processRecord`) parses
the file as CSV and looks up each record's first field (the template's `name`, e.g.
`ptm_merging`) in that registry. A record with an unknown name is skipped, with only an info log
line. So yes: for a composite merge record to be read, the composite must be declared in
`tp_ptm.json` (as `ptm/ptm_merging` composite, `consistsOf` a one-input element template) so
that the ptm `Init` registers a builder for it. Declaring it is not enough, though: today
`log2prov` cannot read any composite record (checked 2026-10-09 against this library's compiled
transport catalogue, which has `packing_composite`/`unpacking_composite`):
1. **No builder for composites.** The generated `Init` sizes its `builders` array for every
   catalogue entry but fills only the simple ones (transport: 9 slots, 7 filled). The two
   composite slots stay `null`, and `FileBuilder.registerBuilders` calls `Class.forName(null)`:
   `-log2prov org.openprovenance.templates.catalogue.transport.Init` dies with a
   `NullPointerException` (`FileBuilder.java:178`) before reading a single line.
2. **Key mismatch.** A composite record starts with the template's fully qualified name
   (`Packing_compositeBuilder.logPacking_composite` writes
   `org.openprovenance.templates.transport.PackingComposite`), whereas the registry is keyed by
   `getName()` (`packing_composite`).
3. **One physical line.** The composite's `args2csv` joins the header (`name,bean,count,type`)
   and the element records with a literal two-character `\n`
   (`sb.append("\\n")` in the generated source). The CSV parser therefore sees one record whose
   fields run into each other (`type\npacking`, ...), not a header followed by element records.

**Options.**
- **(A) Composite `ptm_merging`.** Declare it in `tp_ptm.json` and fix 1–3 in the ProvToolbox
  template compiler and `FileBuilder`. This also makes the fs/transport composites readable,
  but it is ProvToolbox work beyond the ptm library.
- **(B) One record per input, sharing the activity id.** `MergeTask` writes several `ptm_merging`
  records per output, all with the same `merging` id (pairing up the inputs, or, cleaner, a new
  simple one-input template such as `ptm_merging_input`). This works with today's reader:
  `log2prov` unions records by identifier. Tested 2026-10-09: two `ptm_merging` rows for
  `creating-template.provn` with the same id and inputs (1,2) and (3,4) gave one
  `ptm:MergingTemplates` activity with four `used` edges. Records already repeat per output
  format in this way.

**Ruled (Luc, 2026-10-09): B.** Defects 1–3 are filed separately as ProvToolbox T26
(`ProvToolbox/TODO.md`); T-5 does not wait for them.

**Open questions.** Keep `ptm_merging` for the two-input case, or retire it? Do the library's own
`ptm/ptm-merging` template and its bindings
(`bindings/org/openprovenance/bindings/ptm/ptm-merging.json`, built by `config-ptm.json` from
`generic/parallel2`) need a counterpart too?

**DoD.** Regenerated records: for each of the 22 merges, the record's inputs equal the task's
`inputs` in the config (count and names). `log2prov` of a merge record yields one `used` per
input. The `log2prov.*.all` SVGs in `target/` still build.

---

### 🟡 T-6: Provenance records identify templates by their output path

**Status**: OPEN (created 2026-10-09).
**Priority**: 🟡 MEDIUM. **Category**: Provenance / ptm.

**Finding (2026-10-09).** Concatenate every `target/generated-templates/**/*.prov-csv` and run
`provconvert -log2prov org.openprovenance.prov.template.library.ptm.Init`. The result is a valid
document (~1,000 nodes) that does not form a lineage graph: 38 weakly connected components, and
some of those connections are false. A record names its inputs in three different ways, none of
which matches how an output is named (the output path relative to `output_dir`, plus a format
extension, e.g. `org/openprovenance/templates/triangles/triangle1-ugd/triangle1-ugd.jsonld`):
- **Merge inputs** are bare file names (`MergeTask`: `bean.template1 = fileinDirs1.getName()`),
  e.g. `creating-template-triangle1-ugd.jsonld`. None of the 69 merge inputs recorded matches an
  output.
- **Instantiation templates** are recorded as written in the config: a Java-style name
  (`org.openprovenance.templates.triangles.Triangle1-UGD`) or a path
  (`org/openprovenance/templates/generic/parallel2`). The name is never linked to the merge that
  produced the template. `target/generated-templates/index.json` already keys such merges by
  that name (when the merge declares `outputFullyQualifiedName`); hand-written templates map
  name → `src/main/resources/templates/<path lowercased>.{provn,jsonld}`.
- **Bindings** are bare file names (`agent.json`, `activity.json`), so unrelated families that
  happen to use the same file name are joined into one false component.

**Fix (ProvToolbox `InstantiateTask`/`MergeTask`).** Record the resolved file each task actually
read: the template found on `template_path`, the bindings found on `bindings_path`, and each merge
input. Record each one relative to its root (output dir for generated templates, resources root for
hand-written templates and bindings), so that an input written by an earlier task has the same
identifier as that task's output. Formats: an output is recorded once per format
(`.jsonld`, `.provn`, `.png`, ...). Decide whether the input is the `.jsonld`/`.provn` that was
actually read, or a format-free template identifier with the formats as its specialisations.

**Evidence of the target.** A throwaway rebuild on 2026-10-09 got one connected graph: activity ids
and times taken from the records, inputs filled in from the configs, and names resolved through
`index.json`. It had 148 templates (12 hand-written), 114 bindings and 136 activities. T-5 is
needed for merge records to carry all the inputs on their own.

**DoD.** `log2prov` of all records joined (excluding the per-family `all.prov-csv`) gives one
connected component. Every `used` input of an activity is either the output of another activity
or a hand-written template or bindings file. No two families share a bindings node.
