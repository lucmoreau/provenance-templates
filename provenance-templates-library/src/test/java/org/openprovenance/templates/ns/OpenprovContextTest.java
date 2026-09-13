package org.openprovenance.templates.ns;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * openprov.jsonld extends the PROV-JSONLD context by redefining six type-scoped terms, and JSON-LD replaces a
 * redefined term whole: the provext part of each scope is restated, and must not drift from the specification's.
 * The specification's context is read from the prov-jsonld artifact, whose copy its build keeps byte-identical
 * to the published one.
 */
public class OpenprovContextTest extends TestCase {

    static final String OPENPROV_CONTEXT = "template-pages/ns/openprov.jsonld";
    static final String OPENPROV_ONTOLOGY = "template-pages/ns/openprov.ttl";
    static final String OPENPROV_SCHEMA = "../openprovspec/openprov-schema.json";
    static final String SPEC_SCHEMA_URL = "https://openprovenance.org/prov-jsonld/schema.json";
    static final String SPEC_SCHEMA_RESOURCE = "/2024-08-25/jsonldschema.json";
    static final String SPEC_CONTEXT_URL = "https://openprovenance.org/prov-jsonld/context.jsonld";
    static final String SPEC_CONTEXT_RESOURCE = "/2024-08-25/jsonldcontext.jsonld";
    static final String OPENPROV_NS = "https://openprovenance.org/ns/openprov#";
    static final List<String> EXTENDED = List.of("Attribution", "Membership", "Specialization", "Communication", "Start", "End");

    static final ObjectMapper mapper = new ObjectMapper();

    JsonNode spec;
    JsonNode extension;

    @Override
    protected void setUp() throws IOException {
        try (InputStream in = OpenprovContextTest.class.getResourceAsStream(SPEC_CONTEXT_RESOURCE)) {
            assertNotNull("specification context on the classpath (prov-jsonld artifact)", in);
            spec = mapper.readTree(in).get("@context");
        }
        JsonNode root = mapper.readTree(new File(OPENPROV_CONTEXT)).get("@context");
        assertTrue("an array: the specification's context, then the extension", root.isArray() && root.size() == 2);
        assertEquals(SPEC_CONTEXT_URL, root.get(0).asText());
        extension = root.get(1);
        assertEquals(OPENPROV_NS, extension.get("openprov").asText());
    }

    /** The extension redefines the six extended types and nothing else of the specification's. */
    public void testOnlyTheExtendedTypesAreRedefined() {
        Set<String> redefined = new TreeSet<>();
        extension.fieldNames().forEachRemaining(f -> { if (!f.startsWith("@") && spec.has(f)) redefined.add(f); });
        assertEquals(new TreeSet<>(EXTENDED), redefined);
    }

    /** Each extended type keeps the specification's @id and every term of the specification's scope, unchanged. */
    public void testRestatedScopesDoNotDrift() {
        for (String type : EXTENDED) {
            JsonNode specType = spec.get(type);
            JsonNode extType = extension.get(type);
            assertEquals(type + " @id", specType.get("@id"), extType.get("@id"));
            JsonNode specScope = specType.get("@context");
            JsonNode extScope = extType.get("@context");
            Iterator<Map.Entry<String, JsonNode>> terms = specScope.fields();
            while (terms.hasNext()) {
                Map.Entry<String, JsonNode> term = terms.next();
                assertEquals(type + " restates " + term.getKey() + " as the specification has it", term.getValue(), extScope.get(term.getKey()));
            }
        }
    }

    /** Every term the extension adds maps an IRI-valued property in the openprov namespace, named as PROV-O names them. */
    public void testAddedTermsAreOpenprovProperties() {
        for (String type : EXTENDED) {
            JsonNode specScope = spec.get(type).get("@context");
            Iterator<Map.Entry<String, JsonNode>> terms = extension.get(type).get("@context").fields();
            while (terms.hasNext()) {
                Map.Entry<String, JsonNode> term = terms.next();
                if (specScope.has(term.getKey())) continue;
                String id = term.getValue().path("@id").asText();
                assertTrue(type + "." + term.getKey() + " -> " + id, id.startsWith("openprov:had"));
                assertEquals(type + "." + term.getKey() + " takes identifiers", "@id", term.getValue().path("@type").asText());
                assertFalse(type + "." + term.getKey() + " is a plain name", term.getKey().contains(":"));
            }
        }
    }

    /** The properties the context maps to and the object properties the ontology declares are the same set. */
    public void testContextAndOntologyAgree() throws IOException {
        Set<String> inContext = new TreeSet<>();
        for (String type : EXTENDED) {
            extension.get(type).get("@context").elements().forEachRemaining(t -> {
                String id = t.path("@id").asText();
                if (id.startsWith("openprov:")) inContext.add(id.substring("openprov:".length()));
            });
        }
        Set<String> inOntology = new TreeSet<>();
        Matcher m = Pattern.compile("(?m)^:(\\w+) rdf:type owl:ObjectProperty").matcher(Files.readString(new File(OPENPROV_ONTOLOGY).toPath(), StandardCharsets.UTF_8));
        while (m.find()) inOntology.add(m.group(1));
        assertEquals(inOntology, inContext);
    }

    /**
     * openprov-schema.json extends the specification's schema by reference: each extended relation's definition
     * carries the specification's properties plus exactly the scope's terms, and every other definition it uses is
     * a reference into the published schema, so the two cannot drift apart.
     */
    public void testSchemaAdmitsTheScopedTermsAndReferencesTheRest() throws IOException {
        JsonNode specSchema;
        try (InputStream in = OpenprovContextTest.class.getResourceAsStream(SPEC_SCHEMA_RESOURCE)) {
            assertNotNull("specification schema on the classpath (prov-jsonld artifact)", in);
            specSchema = mapper.readTree(in).get("definitions");
        }
        JsonNode schema = mapper.readTree(new File(OPENPROV_SCHEMA));
        assertEquals("#/definitions/prov:Document", schema.get("$ref").asText());
        JsonNode definitions = schema.get("definitions");
        for (String type : EXTENDED) {
            Set<String> expected = new TreeSet<>();
            specSchema.get("prov:" + type).get("properties").fieldNames().forEachRemaining(expected::add);
            extension.get(type).get("@context").fields().forEachRemaining(t -> {
                if (t.getValue().path("@id").asText().startsWith("openprov:")) expected.add(t.getKey());
            });
            Set<String> actual = new TreeSet<>();
            definitions.get("prov:" + type).get("properties").fieldNames().forEachRemaining(actual::add);
            assertEquals(type, expected, actual);
        }
        List<String> refs = new ArrayList<>();
        collectRefs(definitions, refs);
        for (String ref : refs) {
            if (ref.startsWith("#/definitions/")) assertTrue(ref, definitions.has(ref.substring("#/definitions/".length())));
            else assertTrue(ref, ref.startsWith(SPEC_SCHEMA_URL + "#/definitions/") && specSchema.has(ref.substring((SPEC_SCHEMA_URL + "#/definitions/").length())));
        }
    }

    static void collectRefs(JsonNode node, List<String> refs) {
        if (node.isObject()) node.fields().forEachRemaining(f -> { if (f.getKey().equals("$ref")) refs.add(f.getValue().asText()); else collectRefs(f.getValue(), refs); });
        else if (node.isArray()) node.forEach(n -> collectRefs(n, refs));
    }
}
