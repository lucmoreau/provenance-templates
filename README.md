# provenance-templates

Root project aggregating:

- [`provenance-templates-library`](provenance-templates-library/README.md) —
  the provenance template library for the book (Maven artifact
  `org.openprovenance.bookptm:template-intro1`).
- [`provenance-templates-explanations`](provenance-templates-explanations/README.md) —
  explanation plans (x-plans), dictionaries and profiles for those templates
  (Maven artifact `org.openprovenance.bookpt:template-explanations`).

Build everything with:

```
mvn clean install
```

## Guides

- [HOWTO: exactly-once statement submissions with
  `Idempotency-Key`](HOWTO-IDEMPOTENT-SUBMISSIONS.md) — retry statement POSTs
  safely against a generated store, illustrated with `curl` and the transport
  library's `item_init` and `transporting` templates.
