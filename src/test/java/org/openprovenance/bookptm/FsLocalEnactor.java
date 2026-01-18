package org.openprovenance.bookptm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openprovenance.book.fs.client.common.*;
import org.openprovenance.book.fs.client.integrator.*;
import org.openprovenance.templates.catalogue.fs.integrator.BeanLocalEnactor2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FsLocalEnactor extends BeanLocalEnactor2 {

    private final LocalEnactor templateInvoker;
    private final Map<String, AtomicInteger> counterMap ;
    private final Map<String, List<Integer>> recordedValues;
    final private List<Object> history;
    final private Map<Integer, Object> id2object;
    final private Map<Integer, Object []> id2array;
    final private List<String> csv;
    final private boolean negative;
    final private List<List<String>> cvsInputs;


    public FsLocalEnactor(LocalEnactor templateInvoker) {
        super();
        this.history=templateInvoker.getHistory();
        this.id2object=templateInvoker.getId2object();
        this.id2array=templateInvoker.getId2array();
        this.csv=templateInvoker.getCsv();
        this.counterMap=templateInvoker.getCounterMap();
        this.recordedValues=templateInvoker.getRecordedValues();
        this.cvsInputs=templateInvoker.getCvsInputs();
        this.negative=templateInvoker.isNegative();
        this.templateInvoker=templateInvoker;
    }

    int sign() {
        return negative ? -1 : 1;
    }

    @Override
    public Integer newIdentifier(String field, String counter) {
        return templateInvoker.newIdentifier(field, counter);
    }

    public String newSIdentifier(String field, String counter) {
        return  templateInvoker.newSIdentifier(field, counter);
    }

    @Override
    public File_transformingOutputs process(File_transformingInputs bean) {
        File_transformingOutputs out = super.process(bean);
        File_transformingBean fileTransformingBean=merge(bean, out);
        history.add(fileTransformingBean);
        System.out.println("Processing transforming with ID="+out.ID);
        id2object.put(out.ID, fileTransformingBean);
        id2array.put(out.ID, fileTransformingBean.process(new File_transformingBuilder().aArgs2RecordConverter()));
        csv.add(fileTransformingBean.process(new File_transformingBuilder().aArgs2CsVConverter));
        return out;
    }


    @Override
    public File_initOutputs process(File_initInputs bean) {
        File_initOutputs out = super.process(bean);
        File_initBean fileInitBean=merge(bean, out);
        history.add(fileInitBean);
        id2object.put(out.ID, fileInitBean);
        id2array.put(out.ID, fileInitBean.process(new File_initBuilder().aArgs2RecordConverter()));
        csv.add(fileInitBean.process(new File_initBuilder().aArgs2CsVConverter));
        return out;
    }


    List<String> util2CSV (File_transforming_compositeInputs bean) {
        File_transforming_compositeBean fileTransformingCompositeBean=new File_transforming_compositeBean();
        for(File_transformingInputs_1 b: bean.__elements) {
            File_transformingBean fileTransformingBean=merge(b, null);
            fileTransformingCompositeBean.addElements(fileTransformingBean);
        }
        fileTransformingCompositeBean.count=bean.__elements.size();
        fileTransformingCompositeBean.type=new File_transformingBean().isA;

        String csvLines = fileTransformingCompositeBean.process(new File_transforming_compositeBuilder().aArgs2CsVConverter);

        return List.of(csvLines.split("\n"));
    }

    @Override
    public File_transforming_compositeOutputs process(File_transforming_compositeInputs bean) {
        File_transforming_compositeOutputs out = super.process(bean);
        File_transforming_compositeBean fileTransformingCompositeBean=merge(bean, out);
        fileTransformingCompositeBean.type="org.openprovenance.book.fs.FileTransforming";
        history.add(fileTransformingCompositeBean);
        if (out.ID!=null) {
            id2object.put(out.ID, fileTransformingCompositeBean);
            Object[][] compositeRecord = fileTransformingCompositeBean.process(new File_transforming_compositeBuilder().aArgs2RecordConverter);
            id2array.put(out.ID, compositeRecord);
        } else {
            System.out.println("Transforming composite with null ID");
        }
        try {
            System.out.println(new ObjectMapper().writeValueAsString(fileTransformingCompositeBean));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String csvLines = fileTransformingCompositeBean.process(new File_transforming_compositeBuilder().aArgs2CsVConverter);
        csv.addAll(List.of(csvLines.split("\n")));


        cvsInputs.add(util2CSV(bean));
        return out;
    }

    @Override
    public File_transformingOutputs process(File_transformingInputs_1 bean,
                                            Map<String, Map<Integer, Integer>> map) {
        File_transformingOutputs out = super.process(bean, map);
        File_transformingBean fileTransformingBean=merge(bean, out);

        id2object.put(out.ID, fileTransformingBean);
        id2array.put(out.ID, fileTransformingBean.process(new File_transformingBuilder().aArgs2RecordConverter()));

        return out;

    }

    private File_transformingBean merge(File_transformingInputs_1 in, File_transformingOutputs fileTransformingOutputs) {
        File_transformingBuilder builder=new File_transformingBuilder();
        if (fileTransformingOutputs==null) {
            Object[] fileIn= builder.aArgs2RecordConverter().process(in.transformed_file,in.filename,in.file,in.method,in.engineer, in.transforming,in.path,in.time,in.start,in.end);
            Object[] file = merge(fileIn, null);
            return builder.record2bean(file);
        } else {
            Object[] fileIn= in.process(builder.aArgs2RecordConverter());
            Object[] fileOut = fileTransformingOutputs.process(builder.aArgs2RecordConverter());
            Object[] file = merge(fileIn, fileOut);
            return builder.record2bean(file);
        }
    }

    private File_transformingBean merge(File_transformingInputs fileTransformingInputs, File_transformingOutputs fileTransformingOutputs) {
        File_transformingBuilder builder=new File_transformingBuilder();
        Object[] fileIn= fileTransformingInputs.process(builder.aArgs2RecordConverter());
        Object[] fileOut= fileTransformingOutputs.process(builder.aArgs2RecordConverter());
        Object[] file=merge(fileIn, fileOut);
        return builder.record2bean(file);
    }
    private File_initBean merge(File_initInputs fileInitInputs, File_initOutputs fileInitOutputs) {
        File_initBuilder builder=new File_initBuilder();
        Object[] fileIn= fileInitInputs.process(builder.aArgs2RecordConverter());
        Object[] fileOut= fileInitOutputs.process(builder.aArgs2RecordConverter());
        Object[] file=merge(fileIn, fileOut);
        return builder.record2bean(file);
    }

    private File_transforming_compositeBean merge(File_transforming_compositeInputs bean, File_transforming_compositeOutputs out) {
        File_transforming_compositeBean res = new File_transforming_compositeBean();
        File_transformingBuilder build = new File_transformingBuilder();
        for (int i = 0; i < bean.__elements.size(); i++) {
            Object[] transformingIn = bean.__elements.get(i).process(build.aArgs2RecordConverter());
            Object[]transformingOut = out.__elements.get(i).process(build.aArgs2RecordConverter());
            Object[] transforming = merge(transformingIn, transformingOut);
            res.addElements(build.record2bean(transforming));
        }
        res.count=bean.__elements.size();
        return res;
    }


    private Object[] merge(Object[] arrayIn, Object[] arrayOut) {
        Object[] result=new Object[arrayIn.length];
        for (int i=0;i<arrayIn.length;i++) {
            if (arrayIn[i]!=null) result[i]=arrayIn[i];
        }
        if (arrayOut!=null) {
            for (int i = 0; i < arrayOut.length; i++) {
                if (arrayOut[i] != null) result[i] = arrayOut[i];
            }
        }
        return result;
    }

    public Map<String, AtomicInteger> getCounterMap() {
        return counterMap;
    }

    public Map<String, List<Integer>> getRecordedValues() {
        return recordedValues;
    }

    public List<Object> getHistory() {
        return history;
    }

    public Map<Integer, Object> getId2object() {
        return id2object;
    }
    public Map<Integer, Object []> getId2array() {
        return id2array;
    }

    public List<String> getCsv() {
        return csv;
    }
}