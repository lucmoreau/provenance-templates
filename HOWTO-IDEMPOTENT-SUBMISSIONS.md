# HOWTO: exactly-once statement submissions with `Idempotency-Key`

A provenance store accepts template records over HTTP: the client POSTs a
bean, the service inserts a row, and the response carries the identifiers of
the freshly minted provenance nodes. But HTTP delivery is at-least-once, not
exactly-once. If a POST times out, or the connection drops after the store
committed the insert but before the response reached the client, the client
cannot tell "the record never landed" apart from "the record landed and the
answer was lost" — and the safe move, retrying, then inserts the same record
twice.

The `Idempotency-Key` header closes that gap. This guide shows the mechanism
end to end with `curl` against a store generated for the **transport template
library** (`org.openprovenance.bookptm:template-intro1`, the library built by
this project).

## 1. Prerequisites

A running store service for the transport library. From ProvToolbox, the
archetype Makefile generates and launches one (context `/book`, port `7075`,
PostgreSQL database `bookptm`):

```bash
cd ProvToolbox/modules-tools/prov-template-archetype
make archetype.generate.book.tr
cd target/artif/artif-service
mvn clean install
make jetty
```

Throughout this guide the endpoint is

```
http://localhost:7075/book/provapi/statements
```

and every request uses the template media type
`application/vnd.kcl.prov-template+json`. If your service is deployed behind
Keycloak, add `-H "Authorization: Bearer $TOKEN"` to each `curl`; a
development store on an open endpoint needs nothing.

## 2. The contract in one paragraph

Attach an `Idempotency-Key` header — any string, at most one JSON entry per
keyed request — and the store guarantees: **the record behind that key is
inserted at most once, and every POST bearing that key returns the
identifiers minted by the first successful insert.** A request without the
header behaves exactly as before (at-least-once); old services that predate
the feature ignore the header, so clients can adopt it unilaterally.

## 3. Walkthrough

### 3.1 Mint an item

`transporting` needs an existing item node as its input, so first enact
`item_init` (template `org.openprovenance.templates.transport.EntityInit`) —
itself with a key, because this POST deserves exactly-once too:

```bash
curl -s -X POST http://localhost:7075/book/provapi/statements \
  -H "Content-Type: application/vnd.kcl.prov-template+json" \
  -H "Accept: application/vnd.kcl.prov-template+json" \
  -H "Idempotency-Key: demo-run-42:item-init:1" \
  -d '[{"isA":"org.openprovenance.templates.transport.EntityInit",
        "type":"crate",
        "location":"warehouse A",
        "time":"2026-08-24T10:00:00Z"}]'
```

```json
[ {
  "isA" : "org.openprovenance.templates.transport.EntityInit",
  "ID" : 1,
  "entity" : 101,
  "entity0" : 102,
  "activity" : 103
} ]
```

(Your identifiers will differ; what matters is their stability below.)

### 3.2 Transport it

Use the returned `entity0` as the transported item, again with a key:

```bash
curl -s -X POST http://localhost:7075/book/provapi/statements \
  -H "Content-Type: application/vnd.kcl.prov-template+json" \
  -H "Accept: application/vnd.kcl.prov-template+json" \
  -H "Idempotency-Key: demo-run-42:transporting:1" \
  -d '[{"isA":"org.openprovenance.templates.transport.Transporting",
        "item0":102,
        "ilocation":"warehouse A -> port of Dover",
        "time":"2026-08-24T11:30:00Z"}]'
```

```json
[ {
  "isA" : "org.openprovenance.templates.transport.Transporting",
  "ID" : 2,
  "item1" : 104,
  "transporting" : 105
} ]
```

### 3.3 Retry it — the point of the exercise

Pretend the response above was lost and replay the request **verbatim, same
key**:

```bash
curl -s -X POST http://localhost:7075/book/provapi/statements \
  -H "Content-Type: application/vnd.kcl.prov-template+json" \
  -H "Accept: application/vnd.kcl.prov-template+json" \
  -H "Idempotency-Key: demo-run-42:transporting:1" \
  -d '[{"isA":"org.openprovenance.templates.transport.Transporting",
        "item0":102,
        "ilocation":"warehouse A -> port of Dover",
        "time":"2026-08-24T11:30:00Z"}]'
```

```json
[ {
  "isA" : "org.openprovenance.templates.transport.Transporting",
  "ID" : 2,
  "item1" : 104,
  "transporting" : 105
} ]
```

No second record was inserted, and the response is **the original answer,
replayed**: same shape, same identifiers — `ID` 2, `item1` 104,
`transporting` 105. A client cannot tell a replay from the first delivery
(JSON field order aside), so retry handling needs no special case: parse
every response the same way.

Verify in the database, if you like:

```bash
psql bookptm -c "SELECT count(*) FROM transporting;"
psql bookptm -c "SELECT key, table_name, submission_key
                 FROM record_index WHERE submission_key IS NOT NULL;"
```

One `transporting` row; one indexed key.

## 4. Choosing keys

The store treats the key as an opaque string with one property: **global
uniqueness per store, forever**. Two different submissions must never share a
key — a reused key silently answers the second submission with the first
one's record. Three rules keep you safe:

- **Name the act of submission, not its content.** Never derive the key by
  hashing the bean: two legitimately distinct records can be byte-identical
  (a second delivery of the same consignment), and a content key would
  wrongly collapse them.
- **Make it deterministic across retries.** The key must be computed *before*
  the first attempt and reused verbatim on every retry — typically
  `<client-identity>:<work-unit>:<sequence>`, as in `demo-run-42:transporting:1`
  above. If your client crashes and recovers from its own journal, the
  recovered attempt must regenerate the same key.
- **Scope it to your client identity.** If several producers write to one
  store, prefix each producer's keys with something only it uses, so
  independent numbering schemes cannot collide.

One key covers one POST, which carries one JSON entry. This holds for
composite templates too (e.g. `packing_composite`): a composite is a single
POST, all of its element rows commit or roll back atomically with it, so it
takes a single key — and a replayed composite response is reassembled in
full, `__elements` included.

## 5. Requests the store refuses

A keyed request is deliberately narrow. You get `400 Bad Request` for:

- a keyed request whose JSON list carries more than one entry (the key names
  *one* submission — batch several beans, and the store cannot know which one
  the key means);
- a keyed CSV submission (`text/csv` bodies are bulk imports; key individual
  JSON submissions instead);
- a keyed request without `Accept: application/vnd.kcl.prov-template+json`.

## 6. How it works (for the curious)

Every record insert already writes a companion row into `record_index`, the
store's per-submission audit table (which record, which table, which
principal, when) — and it does so **in the same SQL statement** as the record
insert itself. The idempotency mechanism rides that statement:

1. The header value lands in a new `record_index.submission_key` column,
   guarded by a partial unique index (`WHERE submission_key IS NOT NULL`).
2. A duplicate key raises a unique violation, which aborts the *entire*
   composed statement — the record insert included, along with any provenance
   nodes it would have minted. Nothing partial ever survives; this is also
   why two concurrent deliveries of the same submission are safe (one
   commits, the other blocks on the index, then fails and replays).
3. The service catches exactly that violation, looks the key up in
   `record_index`, re-reads the already-stored record, and answers with the
   original identifiers.

Keyless requests write `NULL` into the column; the partial index never sees
them, so their behaviour — including the freedom to insert duplicates — is
byte-for-byte what it always was. Pre-existing databases are migrated in
place at service startup (`ADD COLUMN IF NOT EXISTS` plus
`CREATE UNIQUE INDEX IF NOT EXISTS`); rows from before the migration keep a
`NULL` key and are unaffected.

## 7. What this does not do

- It does not deduplicate by content: two POSTs with identical beans and
  *different* keys insert two records — by design.
- It does not span stores: the key's uniqueness scope is one store database.
- It does not replace client-side bookkeeping: the client still decides what
  constitutes one submission and numbers it. The store only promises that a
  number, once used, is answered consistently forever.
