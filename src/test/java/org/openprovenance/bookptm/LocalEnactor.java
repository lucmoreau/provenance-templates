package org.openprovenance.bookptm;

import org.openprovenance.bk.physical.client.common.HandoverBean;
import org.openprovenance.bk.physical.client.common.HandoverBuilder;
import org.openprovenance.bk.physical.client.common.TransportingBean;
import org.openprovenance.bk.physical.client.common.TransportingBuilder;
import org.openprovenance.bk.physical.client.integrator.*;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalEnactor extends BeanLocalEnactor2 {

    private int counterInitialValue=-10000;
    private final Map<String, AtomicInteger> counterMap=new HashMap<>() ;
    private final Map<String, List<Integer>> recordedValues=new HashMap<>();
    private List<Object> history=new LinkedList<>();
    private Map<Integer, Object> id2object=new HashMap<>();
    private Map<Integer, Object []> id2array=new HashMap<>();
    private List<String> csv=new LinkedList<>();



    @Override
    public Integer newIdentifier(String field, String counter) {
        counterMap.computeIfAbsent(counter, k -> {
            counterInitialValue=counterInitialValue-10000;
            return new AtomicInteger(counterInitialValue);
        });
        int newValue = counterMap.get(counter).getAndDecrement();
        recordedValues.computeIfAbsent(field, k -> new java.util.LinkedList<>()).add(newValue);
        return newValue;
    }

    public String newSIdentifier(String field, String counter) {
        counterMap.computeIfAbsent(counter, k -> {
            counterInitialValue=counterInitialValue-10000;
            return new AtomicInteger(counterInitialValue);
        });
        Integer newValue = counterMap.get(counter).getAndDecrement();
        recordedValues.computeIfAbsent(field, k -> new java.util.LinkedList<>()).add(newValue);
        return String.valueOf(newValue);
    }





    @Override
    public TransportingOutputs process(TransportingInputs bean) {
        TransportingOutputs out = super.process(bean);
        TransportingBean transportingBean=merge(bean, out);
        history.add(transportingBean);
        id2object.put(out.ID, transportingBean);
        id2array.put(out.ID, transportingBean.process(new TransportingBuilder().aArgs2RecordConverter()));
        csv.add(transportingBean.process(new TransportingBuilder().aArgs2CsVConverter));
        return out;
    }

    @Override
    public HandoverOutputs process(HandoverInputs bean) {
        HandoverOutputs out = super.process(bean);
        HandoverBean handoverBean=merge(bean, out);
        history.add(handoverBean);
        id2object.put(out.ID, handoverBean);
        id2array.put(out.ID, handoverBean.process(new HandoverBuilder().aArgs2RecordConverter()));
        csv.add(handoverBean.process(new HandoverBuilder().aArgs2CsVConverter));
        return out;
    }



    private TransportingBean merge(TransportingInputs transportingInputs, TransportingOutputs transportingOutputs) {
        TransportingBuilder builder=new TransportingBuilder();
        Object[] transportingIn= transportingInputs.process(builder.aArgs2RecordConverter());
        Object[] transportingOut= transportingOutputs.process(builder.aArgs2RecordConverter());
        Object[] transporting=merge(transportingIn, transportingOut);
        return builder.toBean(transporting);
    }

    private HandoverBean merge(HandoverInputs handoverInputs, HandoverOutputs handoverOutputs) {
        HandoverBuilder builder=new HandoverBuilder();
        Object[] handoverIn= handoverInputs.process(builder.aArgs2RecordConverter());
        Object[] handoverOut= handoverOutputs.process(builder.aArgs2RecordConverter());
        Object[] handover=merge(handoverIn, handoverOut);
        return builder.toBean(handover);
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