# TEMPLATES-DONE — Retired Tasks

> Tasks retired from `TEMPLATES-TODO.md`, newest first. Each entry: task ID, completion date, what
> was done, key files changed.

---

### ✅ T-5: A merge's provenance record names all its inputs — CLOSED 2026-10-09, no change needed

**Outcome.** The finding was wrong. All 22 merges' records already name every input listed in their
config. Checked against the 09-13 build's records: for every merge, the set of `ptm_merging`
inputs equals the task's `inputs`, 0 mismatches. ProvToolbox's `MergeTask`
(`modules-template/prov-template/src/main/java/org/openprovenance/prov/template/core/ttf/MergeTask.java`,
the `for (int i=1; i<foundTemplates.size(); i++)` loop) already writes one `ptm_merging` record
`(input1, input_i)` per extra input and per format, all with the same `merging` id. That is
option B, and `log2prov` unions the records into one activity with every input `used`. The
original count came from reading only the first row of each record file. No files changed. The
composite-record defects found along the way are real and are filed as ProvToolbox T26. The
naming of inputs is T-6.

**Task as filed (for the record):**

#### 🟡 T-5: A merge's provenance record names all its inputs

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
