# TEMPLATES-TODO — Outstanding Work

> **🔢 LAST ALLOCATED TASK NUMBER: T-4** — the highest task id ever assigned (whether still open here
> or retired to `TEMPLATES-DONE.md`). **When allocating a new task, take the NEXT number and bump this
> line.** Do NOT reuse a number freed by retirement — a retired task is gone from the index but its
> number is still taken.
>
> Self-contained reference — survives memory compaction.
> Completed tasks are retired to `TEMPLATES-DONE.md` (create it on first retirement), each entry
> recording the task ID, completion date, what was done, and the key files changed.
> All paths relative to `/Users/luc/IdeaProjects/provenance-templates/provenance-templates-library/`
> unless stated.
> Last updated: 2026-07-28.

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
