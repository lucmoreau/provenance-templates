package org.openprovenance.templates.catalogue.transport;

import junit.framework.TestCase;
import org.openprovenance.book.physical.client.integrator.TransportingInputs;
import org.openprovenance.book.physical.client.integrator.WeighingInputs;
import org.openprovenance.templates.catalogue.transport.sql.access_control.QueryInvoker4;

/**
 * The generated QueryInvoker4 must pass text inputs to the insert functions as quoted, escaped TEXT literals.
 * <p>
 * Regression: {@code ilocation} (transporting) and {@code unit} (weighing) were appended raw, so
 * {@code "ilocation": "x"} failed with {@code column "x" does not exist} and a crafted value was SQL injection.
 * A value containing a quote must come out with the quote doubled, so Postgres stores it unchanged.
 */
public class TextInputQuotingTest extends TestCase {

    private static final String EVIL = "O'Brien'); drop table record_index; --";

    private String sql(TransportingInputs t) {
        StringBuilder sb = new StringBuilder();
        new QueryInvoker4(sb, "alice").process(t);
        return sb.toString();
    }

    private String sql(WeighingInputs w) {
        StringBuilder sb = new StringBuilder();
        new QueryInvoker4(sb, "alice").process(w);
        return sb.toString();
    }

    public void testTransportingIlocationIsQuoted() {
        TransportingInputs t = new TransportingInputs();
        t.item0 = 1;
        t.ilocation = "x";
        assertTrue(sql(t), sql(t).contains("insert_transporting (1,null,null,null,'x'::TEXT,"));

        t.ilocation = EVIL;
        assertTrue(sql(t), sql(t).contains(",'O''Brien''); drop table record_index; --'::TEXT,"));
    }

    public void testWeighingUnitIsQuoted() {
        WeighingInputs w = new WeighingInputs();
        w.item0 = 2;
        w.weight = 1.5;
        w.unit = "kg";
        assertTrue(sql(w), sql(w).contains("insert_weighing (2,null,null,null,1.5,'kg'::TEXT,"));

        w.unit = "it's";
        assertTrue(sql(w), sql(w).contains(",'it''s'::TEXT,"));
    }
}
