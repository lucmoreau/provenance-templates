
const fs = require('fs');
const { FetchWorker } = require('./fetch-worker');
const { TemplateInvoker } = require('org/openprovenance/templates/catalogue/fs/integrator/TemplateInvoker.js');
const { File_transforming_compositeBean } = require('org/openprovenance/book/fs/client/common/File_transforming_compositeBean.js');
const { File_initBean } = require('org/openprovenance/book/fs/client/common/File_initBean.js');
const { File_validatingBean } = require('org/openprovenance/book/fs/client/common/File_validatingBean.js');
const { File_transformingBean } = require('org/openprovenance/book/fs/client/common/File_transformingBean.js');
const { File_trainingBean } = require('org/openprovenance/book/fs/client/common/File_trainingBean.js');
const { File_filteringBean } = require('org/openprovenance/book/fs/client/common/File_filteringBean.js');
const { File_approvingBean } = require('org/openprovenance/book/fs/client/common/File_approvingBean.js');
const { File_splittingBean } = require('org/openprovenance/book/fs/client/common/File_splittingBean.js');
const { BeanMerger } = require('org/openprovenance/templates/catalogue/fs/integrator/BeanMerger.js');
const { BeanHistory } = require('org/openprovenance/templates/catalogue/fs/integrator/BeanHistory.js');


class ServiceInvoker {

    constructor() {
        this.fetcher = new FetchWorker();
    }

    postInstructionsInOut(url, body, accessToken) {
        //console.log('Posting to', url);
        const token = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
        //console.log('Using token:', token);
        return this.fetcher.post(url, [body], token);
    }

}





class WebTemplateInvoker extends TemplateInvoker {
    constructor(url,token) {
        super();
        this.negative = false;
        this.counterMap = new Map();
        this.recordedValues = new Map();
  //      this.history = [];
        this.si = new ServiceInvoker();
        this.accessToken =token;
        this.url = url;
        this.merger = new BeanMerger();
    }

    generic_post_and_return(cl, inputs0, completer) {
        //console.log("Invoking " + cl.constructor.name + " with inputs: " + inputs0);
        //console.log(inputs0);
        let result0 = this.si.postInstructionsInOut(this.url, inputs0, this.accessToken);
        let result1 = result0[0];

        let map=new Map();
        // for all keys in result1, add to map
        for (let key in result1) {
            if (result1.hasOwnProperty(key)) {
                map.put(key, result1[key]);
            }
        }
        let val=completer(map,  cl);
        //console.log("Received response: ");
        //console.log(val);
        //console.log("After postInstructionsInOut call");
        return val;
    }

/*
    getHistory() {
        return this.history;
    }


    process_file_init_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_init_inputs(inputBean);
        let bean = new File_initBean();
        this.merger.process_file_init_bean_file_init_inputs(bean, inputBean);
        this.merger.process_file_init_bean_file_init_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_transforming_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_transforming_inputs(inputBean);
        let bean = new File_transformingBean();
        this.merger.process_file_transforming_bean_file_transforming_inputs(bean, inputBean);
        this.merger.process_file_transforming_bean_file_transforming_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_filtering_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_filtering_inputs(inputBean);
        let bean = new File_filteringBean();
        this.merger.process_file_filtering_bean_file_filtering_inputs(bean, inputBean);
        this.merger.process_file_filtering_bean_file_filtering_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_training_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_training_inputs(inputBean);
        let bean = new File_trainingBean();
        this.merger.process_file_training_bean_file_training_inputs(bean, inputBean);
        this.merger.process_file_training_bean_file_training_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_validating_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_validating_inputs(inputBean);
        let bean = new File_validatingBean();
        this.merger.process_file_validating_bean_file_validating_inputs(bean, inputBean);
        this.merger.process_file_validating_bean_file_validating_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_approving_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_approving_inputs(inputBean);
        let bean = new File_approvingBean();
        this.merger.process_file_approving_bean_file_approving_inputs(bean, inputBean);
        this.merger.process_file_approving_bean_file_approving_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_splitting_inputs(inputBean) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 135
        let outputBean = super.process_file_splitting_inputs(inputBean);
        let bean = new File_splittingBean();
        this.merger.process_file_splitting_bean_file_splitting_inputs(bean, inputBean);
        this.merger.process_file_splitting_bean_file_splitting_outputs(bean, outputBean);
        this.history.push(bean);
        return outputBean;
    }

    process_file_transforming_composite_inputs(inputComposite) {
        // PAST generator method. Created by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 176
        // Generated by class org.openprovenance.prov.template.compiler.CompilerBeanHistory, method generateBeanHistory
        // in file CompilerBeanHistory.java, at line 183
        let outputComposite = super.process_file_transforming_composite_inputs(inputComposite);
        let bean = new File_transforming_compositeBean();
        for (const composee of outputComposite.__elements) {
            bean.addElements(new File_transformingBean());
        }
        this.merger.process_file_transforming_composite_bean_file_transforming_composite_inputs(bean, inputComposite);
        this.merger.process_file_transforming_composite_bean_file_transforming_composite_outputs(bean, outputComposite);
        this.history.push(bean);
        return outputComposite;
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

 */


}

module.exports = { WebTemplateInvoker };
