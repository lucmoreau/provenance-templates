package org.openprovenance.bookptm;


import org.openprovenance.book.fs.client.common.File_transformingBean;
import org.openprovenance.book.fs.client.common.File_transformingBuilder;
import org.openprovenance.book.fs.client.integrator.File_transformingInputs;
import org.openprovenance.book.fs.client.integrator.File_transformingOutputs;

import org.openprovenance.templates.catalogue.fs.integrator.BeanLocalEnactor2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FsLocalEnactor extends BeanLocalEnactor2 {

    private int counterInitialValue=-10000;
    private final Map<String, AtomicInteger> counterMap=new HashMap<>() ;
    private final Map<String, List<Integer>> recordedValues=new HashMap<>();
    final private List<Object> history;
    final private Map<Integer, Object> id2object;
    final private Map<Integer, Object []> id2array;
    final private List<String> csv;

    public FsLocalEnactor(LocalEnactor templateInvoker) {
        super();
        this.history=templateInvoker.getHistory();
        this.id2object=templateInvoker.getId2object();
        this.id2array=templateInvoker.getId2array();
        this.csv=templateInvoker.getCsv();
    }


    @Override
    public Integer newIdentifier(String field, String counter) {
        counterMap.computeIfAbsent(counter, k -> {
            counterInitialValue=counterInitialValue-10000;
            return new AtomicInteger(counterInitialValue);
        });
        int newValue = counterMap.get(counter).getAndDecrement();
        recordedValues.computeIfAbsent(field, k -> new LinkedList<>()).add(newValue);
        return newValue;
    }

    public String newSIdentifier(String field, String counter) {
        counterMap.computeIfAbsent(counter, k -> {
            counterInitialValue=counterInitialValue-10000;
            return new AtomicInteger(counterInitialValue);
        });
        Integer newValue = counterMap.get(counter).getAndDecrement();
        recordedValues.computeIfAbsent(field, k -> new LinkedList<>()).add(newValue);
        return String.valueOf(newValue);
    }

    @Override
    public File_transformingOutputs process(File_transformingInputs bean) {
        File_transformingOutputs out = super.process(bean);
        File_transformingBean fileTransformingBean=merge(bean, out);
        history.add(fileTransformingBean);
        id2object.put(out.ID, fileTransformingBean);
        id2array.put(out.ID, fileTransformingBean.process(new File_transformingBuilder().aArgs2RecordConverter()));
        csv.add(fileTransformingBean.process(new File_transformingBuilder().aArgs2CsVConverter));
        return out;
    }

    private File_transformingBean merge(File_transformingInputs fileTransformingInputs, File_transformingOutputs fileTransformingOutputs) {
        File_transformingBuilder builder=new File_transformingBuilder();
        Object[] fileIn= fileTransformingInputs.process(builder.aArgs2RecordConverter());
        Object[] fileOut= fileTransformingOutputs.process(builder.aArgs2RecordConverter());
        Object[] file=merge(fileIn, fileOut);
        return builder.toBean(file);
    }





    private Object[] merge(Object[] arrayIn, Object[] arrayOut) {
        Object[] result=new Object[arrayIn.length];
        for (int i=0;i<arrayIn.length;i++) {
            if (arrayIn[i]!=null) result[i]=arrayIn[i];
        }
        for (int i=0;i<arrayOut.length;i++) {
            if (arrayOut[i]!=null) result[i]=arrayOut[i];
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