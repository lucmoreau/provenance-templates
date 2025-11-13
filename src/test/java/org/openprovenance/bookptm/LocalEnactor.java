package org.openprovenance.bookptm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openprovenance.templates.catalogue.transport.integrator.BeanLocalEnactor2;
import org.openprovenance.templates.physical.client.common.*;
import org.openprovenance.templates.physical.client.integrator.*;
import org.openprovenance.templates.responsibility.client.common.HandingoverBean;
import org.openprovenance.templates.responsibility.client.common.HandingoverBuilder;
import org.openprovenance.templates.responsibility.client.integrator.HandingoverInputs;
import org.openprovenance.templates.responsibility.client.integrator.HandingoverOutputs;

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

    public LocalEnactor() {
        super();
    }


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

    /*

    public Packing_compositeOutputs process(Packing_compositeInputs bean) {

        Packing_compositeOutputs out=new Packing_compositeOutputs();
        Map<String,Map<Integer,Integer>> map= new HashMap<>() {{
            put("container1", new HashMap<>());
            put("adding", new HashMap<>());
        }};

        bean.__elements.forEach( in1 -> out.__addElements(process(in1,map)));
        return out;
    }

    public Unpacking_compositeOutputs process(Unpacking_compositeInputs bean) {

        Unpacking_compositeOutputs out=new Unpacking_compositeOutputs();
        Map<String,Map<Integer,Integer>> map= new HashMap<>() {{
            put("container1", new HashMap<>());
            put("removing", new HashMap<>());
        }};

        bean.__elements.forEach( in1 -> out.__addElements(process(in1,map)));
        return out;
    }


     */


    public PackingOutputs process(PackingInputs_1 input, Map<String, Map<Integer, Integer>> map) {

        PackingOutputs out=super.process(input,map);

        /*

        PackingOutputs out=new PackingOutputs();
        // for each field in shared list.
        // container1
        if (map.get("container1")==null) {
            out.container1 = newIdentifier("container1","item");
        } else if (map.get("container1").containsKey(input.container0)) {
            out.container1=map.get("container1").get(input.container0);
        } else {
            out.container1=newIdentifier("container1","item");
            map.get("container1").put(input.container0, out.container1);
        }
        // same for adding
        if (map.get("adding")==null) {
            out.adding = newIdentifier("adding","activity");
        } else if (map.get("adding").containsKey(input.adding)) {
            out.adding=map.get("adding").get(input.adding);
        } else {
            out.adding=newIdentifier("adding","activity");
            map.get("adding").put(input.adding, out.adding);
        }
        out.item1=newIdentifier("item1","item"); // field, not in shared list
        out.ID = newIdentifier("template/packing","template/packing");

         */

        PackingBean packingBean=merge(input, out);
        history.add(packingBean);
        id2object.put(out.ID, packingBean);
        id2array.put(out.ID, packingBean.process(new PackingBuilder().aArgs2RecordConverter()));
        csv.add(packingBean.process(new PackingBuilder().aArgs2CsVConverter));

        return out;
    }



    public UnpackingOutputs process(UnpackingInputs_1 input, Map<String, Map<Integer, Integer>> map) {

        UnpackingOutputs out=super.process(input,map);

        /*
        UnpackingOutputs out=new UnpackingOutputs();
        if (map.get("container1")==null) {
            out.container1 = newIdentifier("container1","item");
        } else if (map.get("container1").containsKey(input.container1)) {
            out.container1=map.get("container1").get(input.container1);
        } else {
            out.container1=newIdentifier("container1","item");
            map.get("container1").put(input.container1, out.container1);
        }
        // same for removing
        if (map.get("removing")==null) {
            out.removing = newIdentifier("removing","activity");
        } else if (map.get("removing").containsKey(input.removing)) {
            out.removing=map.get("removing").get(input.removing);
        } else {
            out.removing=newIdentifier("removing","activity");
            map.get("removing").put(input.removing, out.removing);
        }
        out.item0 = newIdentifier("item0","item");
        out.item1 = newIdentifier("item1","item");
        out.ID = newIdentifier("template/unpacking","template/unpacking");

        out.ID = newIdentifier("template/unpacking","template/unpacking");

         */

        UnpackingBean unpackingBean=merge(input, out);
        history.add(unpackingBean);
        id2object.put(out.ID, unpackingBean);
        id2array.put(out.ID, unpackingBean.process(new UnpackingBuilder().aArgs2RecordConverter()));
        csv.add(unpackingBean.process(new UnpackingBuilder().aArgs2CsVConverter));

        /*
        try {
            System.out.println(new ObjectMapper().writeValueAsString(unpackingBean));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

         */

        return out;
    }


    @Override
    public PackingOutputs process(PackingInputs bean) {
        PackingOutputs out = super.process(bean);
        PackingBean packingBean=merge(bean, out);
        history.add(packingBean);
        id2object.put(out.ID, packingBean);
        id2array.put(out.ID, packingBean.process(new PackingBuilder().aArgs2RecordConverter()));
        csv.add(packingBean.process(new PackingBuilder().aArgs2CsVConverter));
        return out;
    }

    @Override
    public UnpackingOutputs process(UnpackingInputs bean) {
        UnpackingOutputs out = super.process(bean);
        UnpackingBean unpackingBean=merge(bean, out);
        history.add(unpackingBean);
        id2object.put(out.ID, unpackingBean);
        id2array.put(out.ID, unpackingBean.process(new UnpackingBuilder().aArgs2RecordConverter()));
        csv.add(unpackingBean.process(new UnpackingBuilder().aArgs2CsVConverter));
        return out;
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
    public HandingoverOutputs process(HandingoverInputs bean) {
        HandingoverOutputs out = super.process(bean);
        HandingoverBean handingoverBean=merge(bean, out);
        history.add(handingoverBean);
        id2object.put(out.ID, handingoverBean);
        id2array.put(out.ID, handingoverBean.process(new HandingoverBuilder().aArgs2RecordConverter()));
        csv.add(handingoverBean.process(new HandingoverBuilder().aArgs2CsVConverter));
        return out;
    }

    @Override
    public Agent_initOutputs process(Agent_initInputs bean) {
        Agent_initOutputs out = super.process(bean);
        Agent_initBean agent_initBean=merge(bean, out);
        history.add(agent_initBean);
        id2object.put(out.ID, agent_initBean);
        id2array.put(out.ID, agent_initBean.process(new Agent_initBuilder().aArgs2RecordConverter()));
        csv.add(agent_initBean.process(new Agent_initBuilder().aArgs2CsVConverter));
        return out;
    }

    @Override
    public Item_initOutputs process(Item_initInputs bean) {
        Item_initOutputs out = super.process(bean);
        Item_initBean     item_initBean=merge(bean, out);
        history.add(item_initBean);
        id2object.put(out.ID, item_initBean);
        id2array.put(out.ID, item_initBean.process(new Item_initBuilder().aArgs2RecordConverter()));
        csv.add(item_initBean.process(new Item_initBuilder().aArgs2CsVConverter));
        return out;
    }



    @Override
    public WeighingOutputs process(WeighingInputs bean) {
        WeighingOutputs out = super.process(bean);
        WeighingBean weighingBean=merge(bean, out);
        history.add(weighingBean);
        id2object.put(out.ID, weighingBean);
        id2array.put(out.ID, weighingBean.process(new WeighingBuilder().aArgs2RecordConverter()));
        csv.add(weighingBean.process(new WeighingBuilder().aArgs2CsVConverter));
        return out;
    }

    private WeighingBean merge(WeighingInputs weighingInputs, WeighingOutputs weighingOutputs) {
        WeighingBuilder builder=new WeighingBuilder();
        Object[] weighingIn= weighingInputs.process(builder.aArgs2RecordConverter());
        Object[] weighingOut= weighingOutputs.process(builder.aArgs2RecordConverter());
        Object[] weighing=merge(weighingIn, weighingOut);
        return builder.toBean(weighing);
    }

    private Agent_initBean merge(Agent_initInputs bean, Agent_initOutputs out) {
        Agent_initBuilder builder=new Agent_initBuilder();
        Object[] agentIn= bean.process(builder.aArgs2RecordConverter());
        Object[] agentOut= out.process(builder.aArgs2RecordConverter());
        Object[] agent=merge(agentIn, agentOut);
        return builder.toBean(agent);
    }

    private Item_initBean merge(Item_initInputs bean, Item_initOutputs out) {
        Item_initBuilder builder=new Item_initBuilder();
        Object[] itemIn= bean.process(builder.aArgs2RecordConverter());
        Object[] itemOut= out.process(builder.aArgs2RecordConverter());
        Object[] item=merge(itemIn, itemOut);
        return builder.toBean(item);
    }


    private TransportingBean merge(TransportingInputs transportingInputs, TransportingOutputs transportingOutputs) {
        TransportingBuilder builder=new TransportingBuilder();
        Object[] transportingIn= transportingInputs.process(builder.aArgs2RecordConverter());
        Object[] transportingOut= transportingOutputs.process(builder.aArgs2RecordConverter());
        Object[] transporting=merge(transportingIn, transportingOut);
        return builder.toBean(transporting);
    }

    private HandingoverBean merge(HandingoverInputs handoverInputs, HandingoverOutputs handoverOutputs) {
        HandingoverBuilder builder=new HandingoverBuilder();
        Object[] handoverIn= handoverInputs.process(builder.aArgs2RecordConverter());
        Object[] handoverOut= handoverOutputs.process(builder.aArgs2RecordConverter());
        Object[] handover=merge(handoverIn, handoverOut);
        return builder.toBean(handover);
    }

    private PackingBean merge(PackingInputs packingInputs, PackingOutputs packingOutputs) {
        PackingBuilder builder=new PackingBuilder();
        Object[] packingIn= packingInputs.process(builder.aArgs2RecordConverter());
        Object[] packingOut= packingOutputs.process(builder.aArgs2RecordConverter());
        Object[] packing=merge(packingIn, packingOut);
        return builder.toBean(packing);
    }

    private PackingBean merge(PackingInputs_1 packingInputs, PackingOutputs packingOutputs) {
        PackingBuilder builder = new PackingBuilder();
        Object[] packingIn = packingInputs.process(builder.aArgs2RecordConverter());
        Object[] packingOut = packingOutputs.process(builder.aArgs2RecordConverter());
        Object[] packing = merge(packingIn, packingOut);
        return builder.toBean(packing);
    }

    private UnpackingBean merge(UnpackingInputs unpackingInputs, UnpackingOutputs unpackingOutputs) {
        UnpackingBuilder builder=new UnpackingBuilder();
        Object[] unpackingIn= unpackingInputs.process(builder.aArgs2RecordConverter());
        Object[] unpackingOut= unpackingOutputs.process(builder.aArgs2RecordConverter());
        Object[] unpacking=merge(unpackingIn, unpackingOut);
        return builder.toBean(unpacking);
    }


    private UnpackingBean merge(UnpackingInputs_1 unpackingInputs, UnpackingOutputs unpackingOutputs) {
        UnpackingBuilder builder=new UnpackingBuilder();
        Object[] unpackingIn= unpackingInputs.process(builder.aArgs2RecordConverter());
        Object[] unpackingOut= unpackingOutputs.process(builder.aArgs2RecordConverter());
        Object[] unpacking=merge(unpackingIn, unpackingOut);
        return builder.toBean(unpacking);
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