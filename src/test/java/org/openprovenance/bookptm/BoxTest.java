package org.openprovenance.bookptm;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.interop.InteropFramework;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.openprovenance.templates.catalogue.transport.Init.pf;

public class BoxTest extends TestCase {
    private final LocalEnactor templateInvoker;
    private String url="http://localhost:8080";
   public BoxTest(String testName) {
        super(testName);
        this.templateInvoker = new LocalEnactor();
    }

    public void testBox() throws IOException {
        BoxWorkflow workflow = new BoxWorkflow(templateInvoker, null);
        List<Object> results= new ArrayList<>(workflow.run());
        // append recordedValues to results


        results.add(templateInvoker.getHistory());
        results.add(templateInvoker.getId2object());
        results.add(templateInvoker.getId2array());
        results.add(templateInvoker.getCsv());
        results.add(workflow.connections);

        // print all results
        new ObjectMapper().writeValue(new File("target/testBox.json"), results);

        assertEquals(37+8+2,results.size());


        new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream("target/viz.svg"), "template_connections");
        new TemplatesToDot(workflow.connectionsNoAgent, templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream("target/viz2.svg"), "template_connections");

        //new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "prov", pf, null).convert(null, new FileOutputStream("target/viz3.svg"), "template_connections");

        TemplatesToDot templateProcessing = new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "prov", pf, null);
        Document doc= templateProcessing.getDocument();
        new InteropFramework().writeDocument("target/viz3.provn",doc);
        templateProcessing.convert(null, new FileOutputStream("target/viz3.svg"), "template_connections");


        new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "entities", pf, null).convert(null, new FileOutputStream("target/viz4.svg"), "template_connections");




    }



}
