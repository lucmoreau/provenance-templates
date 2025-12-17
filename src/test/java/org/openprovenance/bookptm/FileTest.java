package org.openprovenance.bookptm;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.openprovenance.templates.catalogue.transport.Init.pf;

public class FileTest extends TestCase {
    private final LocalEnactor templateInvoker;
    private final FsLocalEnactor fsTemplateInvoker;
    private String url="http://localhost:8080";
   public FileTest(String testName) {
        super(testName);
        this.templateInvoker = new LocalEnactor();
        this.fsTemplateInvoker=new FsLocalEnactor(templateInvoker);
    }

    public void testFile() throws IOException {
        FileWorkflow workflow = new FileWorkflow(templateInvoker, fsTemplateInvoker,null);
        List<Object> results= new ArrayList<>(workflow.run());
        // append recordedValues to results


        results.add(templateInvoker.getHistory());
        results.add(templateInvoker.getId2object());
        results.add(templateInvoker.getId2array());
        results.add(templateInvoker.getCsv());
        results.add(workflow.connections);

        // print all results
        try {
            new ObjectMapper().writeValue(new File("target/testFile.json"), results);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        assertEquals(13,results.size());

        System.out.println("Results size: "+results);
        System.out.println("Results size: "+new ObjectMapper().writeValueAsString(templateInvoker.getId2array()));


        new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream("target/fs-viz.svg"), "template_connections");
        new TemplatesToDot(workflow.connectionsNoAgent, templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream("target/fs-viz2.svg"), "template_connections");

        //new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "prov", pf, null).convert(null, new FileOutputStream("target/viz3.svg"), "template_connections");

        TemplatesToDot templateProcessing = new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "prov", pf, null);
        Document doc= templateProcessing.getDocument();
        new InteropFramework().writeDocument("target/fs-viz3.provn",doc);
        templateProcessing.convert(null, new FileOutputStream("target/fs-viz3.svg"), "template_connections");


        new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "entities", pf, null).convert(null, new FileOutputStream("target/fs-viz4.svg"), "template_connections");




    }



}
