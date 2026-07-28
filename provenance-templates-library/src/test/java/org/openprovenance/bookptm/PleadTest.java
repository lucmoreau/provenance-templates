package org.openprovenance.bookptm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.openprovenance.templates.catalogue.fs.integrator.InputOutputProcessor;
import org.openprovenance.templates.catalogue.fs.integrator.LocalEnactor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PleadTest extends TestCase {
    private final InputOutputProcessor templateInvoker;
    private final ObjectMapper om = new ObjectMapper();

    public PleadTest(InputOutputProcessor templateInvoker) {
        this.templateInvoker = templateInvoker;
    }

    public PleadTest() {
        Map<String, AtomicInteger> map=new HashMap<>() {{
            put("activity", new AtomicInteger(1000));
            put("file", new AtomicInteger(1000));
            put("score", new AtomicInteger(10000));
            put("approval", new AtomicInteger(100000));
        }};

        LocalEnactor templateInvoker= new LocalEnactor(false);
        //FsLocalEnactor fsTemplateInvoker=new FsLocalEnactor(templateInvoker);

        this.templateInvoker = templateInvoker;
    }

    public void testPlead1() {
        List<Object> inputs=new LinkedList<>();
        List<Object> outputs=new LinkedList<>();
        //new PleadWorkflow(templateInvoker,null,null).workflow("doc123", 1, 220, 222, 1, 50, "path",null,null);
        new PleadWorkflow(templateInvoker,inputs,outputs).workflow("doc123", 1, 220, 222, 1, 50, "path",null,null);
        try {
            // write output to file target/plead-test1.json


            System.out.println(om.writeValueAsString(outputs));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
