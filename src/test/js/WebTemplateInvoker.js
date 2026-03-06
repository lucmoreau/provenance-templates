
const fs = require('fs');
const { FetchWorker } = require('./fetch-worker');

const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;


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





class WebTemplateInvoker extends org.openprovenance.templates.catalogue.fs.integrator.TemplateInvoker {
    constructor(url,token) {
        super();
        this.negative = false;
        this.counterMap = new Map();
        this.recordedValues = new Map();
        this.history = [];
        this.si = new ServiceInvoker();
        this.accessToken =token;
        this.url = url;
    }

    generic_post_and_return(cl, inputs0, completer) {
        //console.log("Invoking " + cl.constructor.name + " with inputs: " + inputs0);
        //console.log(inputs0);
        let result0 = this.si.postInstructionsInOut(this.url, inputs0, this.accessToken);
        let result1 = result0[0];
        //console.log("Received raw response: ");
        //console.log(result1);

        // convert to java map to work with the transpiler code
        let map=new java.util.HashMap();
        // for all keys in result1, add to map
        for (let key in result1) {
            if (result1.hasOwnProperty(key)) {
                map.put(key, result1[key]);
            }
        }
        let val=completer(map, new cl());
        //console.log("Received response: ");
        //console.log(val);
        //console.log("After postInstructionsInOut call");
        return val;
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

module.exports = { WebTemplateInvoker };
