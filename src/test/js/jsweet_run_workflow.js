const fs = require('fs');

const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;


java.util.concurrent={};
java.util.concurrent.atomic={};
java.util.concurrent.atomic.AtomicInteger=class AtomicInteger {
    constructor(value) {
        this.value=value;
    }
    getAndIncrement() {
        let v=this.value;
        this.value++;
        return v;
    }
    getAndDecrement() {
        let v=this.value;
        this.value--;
        return v;
    }
}

//const { WebTemplateInvoker } = require('./jsweet_WebTemplateInvoker');
const { RemoteEnactor } = require('./jsweet_RemoteEnactor.js');



var inputs0=new java.util.LinkedList();
var outputs0=new java.util.LinkedList();

var url="http://localhost:7075/book/provapi/statements";

const mode = (process.argv[2] || 'notdefined').toLowerCase();
let templateInstantion2;

if (mode === 'local') {
    templateInstantion2 = new org.openprovenance.templates.catalogue.fs.integrator.LocalEnactor(false);
} else if (mode === 'remote') {
    var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
    templateInstantion2 = new RemoteEnactor(url, accessToken);
} else {
    console.error('Usage: node jsweet_run_workflow.js [local|remote]');
    process.exit(1);
}


class ThisWorkflow extends org.openprovenance.book.workflows.PleadWorkflow {
    constructor(templateInstantion, inputs, outputs) {
        super(templateInstantion, inputs, outputs);
    }
    time() {
        // get now time as ISO string
        return new Date().toISOString();
    }
}

const pleadWorkflow=new ThisWorkflow(templateInstantion2,inputs0,outputs0);

//  workflow(engineer, manager, filenameRoot, oldFileId, tmethod, fmethod, n_rows, n_cols, path, start, end)
pleadWorkflow.workflow(111,333,"inputfile", 123, 56, 78, 456, 768,'/home/bob',"2026-03-01T09:03:51.168987Z", "2026-03-01T09:03:51.168987Z");





var inputs=[];
var outputs=[];



inputs0.forEach(i => {
    inputs.push(i);
});
outputs0.forEach(o => {
    outputs.push(o);
});

console.log(templateInstantion2.history);

// last element of outputs

console.log("ID of last element in history " + outputs[outputs.length-1].ID);