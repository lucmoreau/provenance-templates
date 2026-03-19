


const { BeanLocalEnactor2 } = require('org/openprovenance/templates/catalogue/transport/integrator/BeanLocalEnactor2.js');
const { TransportingBuilder } = require('org/openprovenance/book/physical/client/common/TransportingBuilder.js');
const { WeighingBuilder } = require('org/openprovenance/book/physical/client/common/WeighingBuilder.js');
const { HandingoverBuilder } = require('org/openprovenance/book/responsibility/client/common/HandingoverBuilder.js');
const { Agent_initBuilder } = require('org/openprovenance/book/physical/client/common/Agent_initBuilder.js');
const { Item_initBuilder } = require('org/openprovenance/book/physical/client/common/Item_initBuilder.js');
const { PackingBuilder } = require('org/openprovenance/book/physical/client/common/PackingBuilder.js');
const { Packing_compositeBuilder } = require('org/openprovenance/book/physical/client/common/Packing_compositeBuilder.js');
const { Packing_compositeBean } = require('org/openprovenance/book/physical/client/common/Packing_compositeBean.js');
const { Unpacking_compositeBean } = require('org/openprovenance/book/physical/client/common/Unpacking_compositeBean.js');
const { UnpackingBuilder } = require('org/openprovenance/book/physical/client/common/UnpackingBuilder.js');

class LocalEnactor extends BeanLocalEnactor2 {
    constructor() {
        super();
        this.negative = false;
        this.counterInitialValue = this.sign() * 10000;
        this.counterMap = new Map();
        this.recordedValues = new Map();
        this.history = [];
    }

    sign() {
        return this.negative ? -1 : 1;
    }

    newIdentifier(field, counter) {
        if (!this.counterMap.has(counter)) {
            this.counterInitialValue = this.counterInitialValue + this.sign() * 10000;
            this.counterMap.set(counter, {value: this.counterInitialValue});
        }
        const entry = this.counterMap.get(counter);
        const newValue = this.negative ? entry.value-- : entry.value++;
        if (!this.recordedValues.has(field)) {
            this.recordedValues.set(field, []);
        }
        this.recordedValues.get(field).push(newValue);
        return newValue;
    }


    process_transporting_inputs(bean) {
        const out = super.process_transporting_inputs(bean);
        const builder = new TransportingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_transformingBean = builder.record2bean(item);
        this.history.push(file_transformingBean);
        return out;
    }

    process_handingover_inputs(bean) {
        const out = super.process_handingover_inputs(bean);
        const builder = new HandingoverBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_filteringBean = builder.record2bean(item);
        this.history.push(file_filteringBean);
        return out;
    }

    process_weighing_inputs(bean) {
        const out = super.process_weighing_inputs(bean);
        const builder = new WeighingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_splittingBean = builder.record2bean(item);
        this.history.push(file_splittingBean);
        return out;
    }

    process_agent_init_inputs(bean) {
        const out = super.process_agent_init_inputs(bean);
        const builder = new Agent_initBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_validatingBean = builder.record2bean(item);
        this.history.push(file_validatingBean);
        return out;
    }

    process_item_init_inputs(bean) {
        const out = super.process_item_init_inputs(bean);
        const builder = new Item_initBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_trainingBean = builder.record2bean(item);
        this.history.push(file_trainingBean);
        return out;
    }

    process_packing_inputs(bean) {
        const out = super.process_packing_inputs(bean);
        const builder = new File_approvingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_approvingBean = builder.record2bean(item);
        this.history.push(file_approvingBean);
        return out;
    }

    process_packing_composite_inputs(bean) {
        const out = super.process_packing_composite_inputs(bean);
        const packing_compositeBean = this.merge_packing_composite(bean, out);
        this.history.add(packing_compositeBean);
        return out;
    }

    merge_packing_composite (bean, out) {
        const res = new Packing_compositeBean();
        let build = new PackingBuilder();
        for (let i = 0; i < bean.__elements.size(); i++) {
            let packingIn = bean.__elements.get(i).process(build.aArgs2RecordConverter());
            let packingOut = out.__elements.get(i).process(build.aArgs2RecordConverter());
            const packing = this.merge_array(packingIn, packingOut);
            res.addElements(build.record2bean(packing));
        }
        return res;
    }


    merge_array(itemIn, itemOut) {
        const item = new Array(itemIn.length);
        for (let i = 0; i < itemIn.length; i++) {
            if (itemIn[i] != null) item[i] = itemIn[i];
        }
        for (let i = 0; i < itemOut.length; i++) {
            if (itemOut[i] != null) item[i] = itemOut[i];
        }
        return item;
    }


}


module.exports = { LocalEnactor };


