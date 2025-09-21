package org.openprovenance.bookptm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openprovenance.bk.physical.client.configurator.PropertyOrderConfigurator;
import org.openprovenance.bk.physical.configurator.TableConfiguratorForTypesWithMap;
import org.openprovenance.bk.physical.configurator.TableConfiguratorWithMap;
import org.openprovenance.prov.dot.ProvToDot;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.exception.UncheckedException;
import org.openprovenance.prov.template.log2prov.FileBuilder;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static org.openprovenance.bk.physical.client.logger.Logger.initializeBeanTable;
import static org.openprovenance.prov.model.NamespacePrefixMapper.DOT_NS;
import static org.openprovenance.prov.template.compiler.common.Constants.INPUT;
import static org.openprovenance.prov.template.compiler.common.Constants.OUTPUT;


public class TemplatesToDot extends ProvToDot {

    private static final Logger logger = LogManager.getLogger(TemplatesToDot.class);
    private final List<TemplateConnection> templateConnections;
    private final Map<String, Map<String, Map<String, String>>> ioMap;
    private final Map<String, Map<String, String>> baseTypes;
    private final ProvFactory pf;
    private final Map<String, Map<String, List<String>>> successors;
    private final String style;
    private final String principal;
    private final Map<Integer, Object[]> id2array;
    private final Map<String, FileBuilder> documentBuilderDispatcher;
    private final Map<String, Map<String, Set<String>>> typeAssignment;

    HashMap<String,String> map=new HashMap<>() {{
        put("PROV_HOST", "example.org");
        put("PROV_API", "http://example.org/prov-api/");
    }};

    ObjectMapper om=new ObjectMapper();
    static TypeReference<Map<String,Map<String, Map<String, String>>>> typeRef = new TypeReference<>() {};



    public Map<String, Map<String, Map<String, String>>> getIoMap(String ioMapString) {
        List<String> toExclude = List.of("plead_transforming_composite");
        try {
            Map<String, Map<String, Map<String, String>>> ioMap = om.readValue(ioMapString, typeRef);
            ioMap.get(INPUT). entrySet().removeIf( entry -> toExclude.contains(entry.getKey()) || entry.getValue() == null || entry.getValue().isEmpty());
            ioMap.get(OUTPUT).entrySet().removeIf( entry -> toExclude.contains(entry.getKey()) || entry.getValue() == null || entry.getValue().isEmpty());
            return ioMap;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public TemplatesToDot(List<TemplateConnection> templateConnections,
                          Map<Integer, Object []> id2array,
                          String style,
                          ProvFactory pf,
                          String principal) {
        super(pf);
        this.pf=pf;
        this.templateConnections = templateConnections;
        this.templateConnections.forEach(tc -> { tc.in_id=abs(tc.in_id); tc.out_id=abs(tc.out_id);});
        this.ioMap = getIoMap(org.openprovenance.bk.physical.client.logger.Logger.ioMap);
        this.style=style;
        this.principal=principal;
        this.id2array=id2array;
        this.documentBuilderDispatcher=initializeBeanTable(new TableConfiguratorWithMap(map,pf));
        this.successors=initializeBeanTable(new TableConfiguratorForSuccessors(documentBuilderDispatcher));
        Map<String, String[]> propertyOrder = initializeBeanTable(new PropertyOrderConfigurator());
        this.typeAssignment = initializeBeanTable(new TableConfiguratorForTypesWithMap(new HashMap<>(), propertyOrder,this.documentBuilderDispatcher,null));
        this.baseTypes = getBaseTypes();



    }

    public Map<String, Map<String, String>> getBaseTypes() {

        typeAssignment.entrySet().removeIf(entry -> entry.getValue() ==null || entry.getValue().isEmpty());

        Map<String,Map<String,String>> baseTypes
                = typeAssignment
                .keySet()
                .stream()
                .collect(Collectors
                        .toMap(tpl -> tpl,
                                tpl ->
                                        typeAssignment
                                                .get(tpl)
                                                .keySet()
                                                .stream()
                                                .collect(Collectors
                                                        .toMap(var->var,
                                                                var -> preferredType(typeAssignment
                                                                        .get(tpl)
                                                                        .get(var))))));

        return baseTypes;
    }


    private int colorValue(String s) {
        if (s==null) return 0;
        switch (s) {
            case "http://www.w3.org/ns/prov#Entity":
                return 1;
            case "http://www.w3.org/ns/prov#Activity":
                return 2;
            case "http://www.w3.org/ns/prov#Agent":
                return 3;
            default:
                return 0;
        }
    }

    private String preferredType(Set<String> value) {
        return value.stream().max(Comparator.comparingInt(this::colorValue)).orElse("none");
    }


    public static String createHtmlTable(TemplateInfo templateInfo,
                                         List<String> inputsNames,
                                         List<String> inputsPorts,
                                         List<String> inputsColors,
                                         List<String> outputsNames,
                                         List<String> outputsPorts,
                                         List<String> outputColors) {
        StringBuilder html = new StringBuilder();

        // Start building the HTML for the table
        html.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");

        // First row with rowspan and input cells
        html.append("  <TR>\n");
        html.append(String.format("    <TD ROWSPAN=\"3\" HREF=\"%s\"  TARGET=\"_blank\">%s </TD>\n", templateInfo.url, templateInfo.templateId));
        for (int i = 0; i < inputsNames.size(); i++) {
            html.append(String.format("    <TD PORT=\"%s\" BGCOLOR=\"%s\" HREF=\"%s\" TARGET=\"_blank\">%s</TD>\n",
                    inputsPorts.get(i), inputsColors.get(i), templateInfo.url.replace(".svg", "/"+inputsNames.get(i)), inputsNames.get(i)));
        }
        html.append("  </TR>\n");

        // Second row for outputs
        html.append("  <TR>\n");
        for (int i = 0; i < outputsNames.size(); i++) {
            html.append(String.format("    <TD PORT=\"%s\" BGCOLOR=\"%s\"  HREF=\"%s\"  TARGET=\"_blank\">%s</TD>\n",
                    outputsPorts.get(i), outputColors.get(i), templateInfo.url.replace(".svg", "/"+outputsNames.get(i)), outputsNames.get(i)));
        }
        html.append("  </TR>\n");

        // Close the table
        html.append("</TABLE>");

        return html.toString();
    }

   final Map<String, String> provcolors = new HashMap<>() {{
        put("http://www.w3.org/ns/prov#Entity", ENTITY_FILLCOLOUR);
        put("http://www.w3.org/ns/prov#Activity", ACTIVITY_FILL_COLOUR);
        put("http://www.w3.org/ns/prov#Agent", AGENT_FILLCOLOUR);
    }};




    public void convert(Document graph, OutputStream os, String title) {
        try {
            File dotFile=File.createTempFile("temp", ".dot");
            logger.info("dotFile: " + dotFile);
            convert(graph, new PrintStream(new FileOutputStream(dotFile)) ,title);
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("dot  -Tsvg " + dotFile);
            InputStream is=proc.getInputStream();
            org.apache.commons.io.IOUtils.copy(is, os);
            logger.info("finished conversion to svg");
            @SuppressWarnings("unused")
            boolean resultCode=dotFile.delete();
        } catch (IOException e) {
            logger.throwing(e);
            throw new UncheckedException(e);
        }
    }

    public void convert(Document ignore, PrintStream out, String title) {
        switch (style) {
            case "template":
                convert_template(ignore, out, title);
                break;
            case "prov":
                convert_prov(ignore, out, title);
                break;
            case "entities":
                convert_entities(ignore, out, title);
                break;
            default:
                throw new UnsupportedOperationException("style not supported: " + style);
        }
    }

    public void convert_prov(Document ignore, PrintStream out, String title) {
        Set<RecordEntry> the_templates = new HashSet<>();

        for (TemplateConnection templateConnection : templateConnections) {
            RecordEntry entry_in=new RecordEntry();
            entry_in.table=templateConnection.in_template;
            entry_in.key=templateConnection.in_id;
            the_templates.add(entry_in);

            RecordEntry entry_out=new RecordEntry();
            entry_out.table=templateConnection.out_template;
            entry_out.key=templateConnection.out_id;
            the_templates.add(entry_out);
        }

        List<Object[]> the_records = new LinkedList<>();
        for (RecordEntry linked_record : the_templates) {
            Integer simple = linked_record.key;

            List<Object[]> simple_records = querySimple(linked_record.table, simple, false, principal);
            the_records.addAll(simple_records);
        }

        Document result=constructDocument(the_records);

        super.convert(result, out, title);

    }

    public List<Object[]> querySimple(String table, Integer key, boolean all, String principal) {
        return id2array.get(key)==null ? Collections.emptyList() : Collections.singletonList(id2array.get(key));
    }

    public void convert_entities(Document ignore, PrintStream out, String title) {
        // creates a map from in to out
        Map<QualifiedName,QualifiedName> map=new HashMap<>();
        for (TemplateConnection templateConnection : templateConnections) {
            QualifiedName outQn = qualifiedPortNameAsQn(templateConnection.out_template, String.valueOf(templateConnection.out_id), templateConnection.out_property);
            QualifiedName inQn = qualifiedPortNameAsQn(templateConnection.in_template, String.valueOf(templateConnection.in_id), templateConnection.in_property);
            map.put(inQn,outQn);
        }

        Document doc = pf.newDocument();

        Set<QualifiedName> seen=new HashSet<>();
        for (TemplateConnection templateConnection : templateConnections) {

            String template = templateConnection.in_template;
            String templateId = String.valueOf(templateConnection.in_id);
            String property = templateConnection.in_property;
            List<String> next=successors.get(template).get(property);
            if (next!=null) {
                for (String n: next) {
                    QualifiedName older = map.get(qualifiedPortNameAsQn(template, templateId, property));
                    QualifiedName newer = qualifiedPortNameAsQn(template, templateId, n);

                    if (!seen.contains(older)) {
                        seen.add(older);
                        doc.getStatementOrBundle().add(pf.newEntity(older));
                    }
                    if (!seen.contains(newer)) {
                        seen.add(newer);
                        doc.getStatementOrBundle().add(pf.newEntity(newer));
                    }

                    List<Attribute> attrs=new LinkedList<>();
                    attrs.add(pf.newAttribute(pf.newQualifiedName(DOT_NS,"style","dot"), "dashed", pf.getName().XSD_STRING));
                    WasDerivedFrom edge = pf.newWasDerivedFrom(null,newer, older, null, null, null, attrs);
                    doc.getStatementOrBundle().add(edge);
                }
            }

        }

        super.convert(doc, out, title);
    }


    public void convert_template(Document doc, PrintStream out, String title) {
        if (title!=null) name=title;
        prelude(doc, out);

        // pairs <template, templateInstance>
        Set<TemplateInfo> allTemplates = new HashSet<>();
        for (TemplateConnection templateConnection : templateConnections) {
            allTemplates.add(TemplateInfo.of(templateConnection.in_template, templateName(templateConnection.in_template, templateConnection.in_id),  url(templateConnection.in_template,  templateConnection.in_id)));
            allTemplates.add(TemplateInfo.of(templateConnection.out_template,templateName(templateConnection.out_template,templateConnection.out_id), url(templateConnection.out_template, templateConnection.out_id)));
        }

        Map<String, Map<String, String>> inputs=ioMap.get("input"); //templateDispatcher.getInputs();
        Map<String, Map<String, String>> outputs=ioMap.get("output"); //templateDispatcher.getOutputs();


        for (TemplateInfo templateInfo: allTemplates) {
           // System.out.println("- templateInfo: " + templateInfo);

            String template = templateInfo.template;
            String templateId = templateInfo.templateId;
            Map<String, String> templateBaseTypes = baseTypes.get(template);



            List<String> inputsNames  = new ArrayList<>(inputs.get(template).keySet());
            List<String> inputPorts   = inputsNames.stream().map(s -> portName(template,templateId,s)).collect(Collectors.toList());
            List<String> inputsColors = inputsNames.stream().map(s -> provcolors.get(templateBaseTypes.get(s))).collect(Collectors.toList()); //inputPorts.stream().map(s -> "lightgreen").collect(Collectors.toList());

            List<String> outputsNames  = new ArrayList<>(outputs.get(template).keySet());
            List<String> outputsPorts  = outputsNames.stream().map(s -> portName(template, templateId,s)).collect(Collectors.toList());
            List<String> outputsColors = outputsNames.stream().map(s -> provcolors.get(templateBaseTypes.get(s))).collect(Collectors.toList()); //outputsPorts.stream().map(s -> "orange").collect(Collectors.toList());


            String html = createHtmlTable(templateInfo, inputsNames, inputPorts, inputsColors, outputsNames, outputsPorts, outputsColors);
            emitTemplate(template, templateId, html, out);

        }

        for (TemplateConnection templateConnection : templateConnections) {
            emitEdge(qualifiedPortName(templateConnection.in_template,  templateName(templateConnection.in_template, templateConnection.in_id),  templateConnection.in_property),
                     qualifiedPortName(templateConnection.out_template, templateName(templateConnection.out_template,templateConnection.out_id), templateConnection.out_property),
                     out);
        }

        postlude(doc,out);
        out.close();

    }

    String headstyle="invempty";
    String tailstyle="empty";

    private void emitEdge(String source, String destination, PrintStream out) {
        StringBuffer sb=new StringBuffer();
        sb.append("\n");
        sb.append(source).append(":n"); // anchor to north
        sb.append(" -> ");
        sb.append(destination).append(":s"); // anchor to south
        sb.append("[dir=\"both\", arrowhead=\"").append(headstyle).append("\", arrowtail=\"").append(tailstyle).append("\"]");
        sb.append(";\n");
        out.println(sb.toString());
    }

    private String portName(String template, String templateId, String property) {
        return  template+"_"+templateId+"_"+property;
    }


    private String qualifiedPortName(String template, String templateId, String property) {
        return templateId + ":" + portName(template, templateId, property);
    }
    private QualifiedName qualifiedPortNameAsQn(String template, String templateId, String property) {
        return pf.newQualifiedName( "/ptl/provapi/template/", template + "/"+ templateId + "/" + property, "ex");
    }

    public void emitTemplate(String template, String templateId, String htmlTable, PrintStream out) {
        StringBuffer sb=new StringBuffer();
        sb.append("\n");
        sb.append("node [shape=plaintext]\n");
        sb.append(templateId);
        sb.append(" [label=<");
        sb.append(htmlTable);
        sb.append(">];\n");
        out.println(sb.toString());
    }

    private String templateName(String template, Integer id) {
        return template+"_"+id;
    }

    private String livePrefix(String relation) {
        return "/ptl/provapi/live/" + relation+"/" ;
    }

    private String urlPrefix(String template) {
        return "/ptl/provapi/template/" + template+"/";
    }
    private String url(String template, Integer id) {
        return "/ptl/provapi/template/" + template+"/"+id + ".svg";
    }

    public static class TemplateInfo {
        private final String template;
        private final String templateId;
        private final String url;

        private TemplateInfo (String template, String templateId, String url) {
            this.template=template;
            this.templateId=templateId;
            this.url=url;
        }
        static public TemplateInfo of(String template, String templateId, String url) {
            return new TemplateInfo(template, templateId, url);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TemplateInfo that = (TemplateInfo) o;
            return Objects.equals(template, that.template) && Objects.equals(templateId, that.templateId) && Objects.equals(url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(template, templateId, url);
        }
    }

    static class RecordEntry {
        public String table;
        public Integer key;
        @Override
        public String toString() {
            return "RecordEntry{" +
                    "table='" + table + '\'' +
                    ", key=" + key +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecordEntry that = (RecordEntry) o;
            return Objects.equals(table, that.table) && Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(table, key);
        }
    }


    Document constructDocument(Collection<Object[]> the_records) {
        return constructDocument(documentBuilderDispatcher, the_records);
    }


    public Document constructDocument(Map<String, FileBuilder> documentBuilderDispatcher, Collection<Object[]> the_records) {
        IndexedDocument iDoc = new IndexedDocument(pf, pf.newDocument());
        for (Object[] record : the_records) {
            FileBuilder builder = documentBuilderDispatcher.get((String)record[0]);
            if (builder != null) {
                Document doc = builder.make(record);
                iDoc.merge(doc);

            } else {
                throw new UnsupportedOperationException("unknown record " + record[0] + " " + Arrays.asList(record));
            }
        }
        return iDoc.toDocument();
    }

}
