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

    private String url="http://localhost:8080";
    public FileTest(String testName) {
        super(testName);

    }

    public void testFile1() throws IOException {
        LocalEnactor templateInvoker = new LocalEnactor();
        FsLocalEnactor fsTemplateInvoker = new FsLocalEnactor(templateInvoker);
        doTestFile("target/workflow-fs1",
                new File1Workflow(templateInvoker, fsTemplateInvoker, null),
                templateInvoker,
                fsTemplateInvoker,
                14);
    }

    public void testFile2() throws IOException {
        LocalEnactor templateInvoker = new LocalEnactor();
        FsLocalEnactor fsTemplateInvoker = new FsLocalEnactor(templateInvoker);
        doTestFile("target/workflow-fs2",
                new File2Workflow(templateInvoker, fsTemplateInvoker, null, File2Workflow.MarkerMode.SAME_MARKER, File2Workflow.MarkerMode.SAME_MARKER),
                templateInvoker,
                fsTemplateInvoker,
                16);
    }


    public void testFile3() throws IOException {
        LocalEnactor templateInvoker = new LocalEnactor();
        FsLocalEnactor fsTemplateInvoker = new FsLocalEnactor(templateInvoker);
        doTestFile("target/workflow-fs3",
                new File2Workflow(templateInvoker, fsTemplateInvoker, null, File2Workflow.MarkerMode.DISTINCT_MARKERS, File2Workflow.MarkerMode.SAME_MARKER),
                templateInvoker,
                fsTemplateInvoker,
                16);
    }

    public void testFile4() throws IOException {
        LocalEnactor templateInvoker= new LocalEnactor();
        FsLocalEnactor fsTemplateInvoker=new FsLocalEnactor(templateInvoker);
        doTestFile("target/workflow-fs4",
                new File2Workflow(templateInvoker, fsTemplateInvoker,null, File2Workflow.MarkerMode.DISTINCT_MARKERS, File2Workflow.MarkerMode.DISTINCT_MARKERS),
                templateInvoker,
                fsTemplateInvoker,
                16);
    }

    public void testFile5() throws IOException {
        LocalEnactor templateInvoker= new LocalEnactor();
        FsLocalEnactor fsTemplateInvoker=new FsLocalEnactor(templateInvoker);
        List<Object> inputs=new ArrayList<>();
        List<Object> outputs=new ArrayList<>();
        new FSWorkflow(fsTemplateInvoker,inputs, outputs).workflow("doc123", 1, 220, 222, 1, 50, "path",null,null);
        // display outputs
        System.out.println("Outputs: "+new ObjectMapper().writeValueAsString(outputs));
      
    }


    public void doTestFile(String out_prefix, Workflow workflow, LocalEnactor templateInvoker, FsLocalEnactor fsTemplateInvoker, int expected) throws IOException {
        List<Object> results= new ArrayList<>(workflow.run());
        // append recordedValues to results


        results.add(templateInvoker.getHistory());
        results.add(templateInvoker.getId2object());
        results.add(templateInvoker.getId2array());
        results.add(templateInvoker.getCsv());
        results.add(workflow.getConnections());
        results.add(templateInvoker.getCvsInputs());

        // print all results
        try {
            new ObjectMapper().writeValue(new File(out_prefix + "Test.json"), results);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        assertEquals(expected,results.size());

        System.out.println("Results size: "+results);
        System.out.println("Results size: "+new ObjectMapper().writeValueAsString(templateInvoker.getId2array()));


        new TemplatesToDot(workflow.getConnections(), templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream(out_prefix + "-viz.svg"), "template_connections");
        new TemplatesToDot(workflow.getConnectionsNoAgent(), templateInvoker.getId2array(), "template", pf, null).convert(null, new FileOutputStream(out_prefix + "-viz2.svg"), "template_connections");

        //new TemplatesToDot(workflow.connections, templateInvoker.getId2array(), "prov", pf, null).convert(null, new FileOutputStream("target/viz3.svg"), "template_connections");

        TemplatesToDot templateProcessing = new TemplatesToDot(workflow.getConnections(), templateInvoker.getId2array(), "prov", pf, null);
        Document doc= templateProcessing.getDocument();
        new InteropFramework().writeDocument(out_prefix + "-viz3.provn",doc);
        templateProcessing.convert(null, new FileOutputStream(out_prefix + "-viz3.svg"), "template_connections");


        new TemplatesToDot(workflow.getConnections(), templateInvoker.getId2array(), "entities", pf, null).convert(null, new FileOutputStream(out_prefix + "-viz4.svg"), "template_connections");




    }



}
