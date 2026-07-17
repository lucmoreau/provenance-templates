

const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;


class LocalEnactor extends org.openprovenance.templates.catalogue.fs.integrator.BeanLocalEnactor2 {
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


    process$org_openprovenance_book_fs_client_integrator_File_transformingInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_transformingInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_transformingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);


        const file_transformingBean = builder.record2bean(item);
        this.history.push(file_transformingBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_filteringInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_filteringInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_filteringBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_filteringBean = builder.record2bean(item);
        this.history.push(file_filteringBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_splittingInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_splittingInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_splittingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_splittingBean = builder.record2bean(item);
        this.history.push(file_splittingBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_validatingInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_validatingInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_validatingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_validatingBean = builder.record2bean(item);
        this.history.push(file_validatingBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_trainingInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_trainingInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_trainingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_trainingBean = builder.record2bean(item);
        this.history.push(file_trainingBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_approvingInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_approvingInputs(bean);
        const builder = new org.openprovenance.book.fs.client.common.File_approvingBuilder();
        const itemIn = bean.process(builder.aArgs2RecordConverter());
        const itemOut = out.process(builder.aArgs2RecordConverter());
        const item = this.merge_array(itemIn, itemOut);
        const file_approvingBean = builder.record2bean(item);
        this.history.push(file_approvingBean);
        return out;
    }

    process$org_openprovenance_book_fs_client_integrator_File_transforming_compositeInputs(bean) {
        const out = super.process$org_openprovenance_book_fs_client_integrator_File_transforming_compositeInputs(bean);
        const file_transforming_compositeBean = merge_composite(bean, out);
        this.history.add(unpackingCompositeBean);
        return out;
    }

    merge_composite (bean, out) {
        const res = new Unpacking_compositeBean();
        let build = new UnpackingBuilder();
        for (let i = 0; i < bean.__elements.size(); i++) {
            let unpackingIn = bean.__elements.get(i).process(build.aArgs2RecordConverter());
            let unpackingOut = out.__elements.get(i).process(build.aArgs2RecordConverter());
            const unpacking = this.merge_array(unpackingIn, unpackingOut);
            res.addElements(build.record2bean(unpacking));
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


