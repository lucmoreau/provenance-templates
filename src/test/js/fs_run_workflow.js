const fs = require('fs');

/*
const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;
*/

//const { LocalEnactor } = require('./LocalEnactor');

//const { WebTemplateInvoker } = require('./WebTemplateInvoker');
past={};
past.util={}
global.past = past;

past.util.StringBuilder = class StringBuilder {
    constructor() {
        this.contents=[]
    }
    append(o) {
        this.contents.push(o)
    }
    toString() {
        return this.contents.join("");
    }
}

Map.prototype.put = function (x,y){
    this.set(x,y)
}

console.log(new Map())

console.log("luc1")
const { PleadWorkflow } = require('org/openprovenance/book/workflows/PleadWorkflow.js');

console.log("luc2")
const { LocalEnactor } = require('./fs_LocalEnactor.js');

//var templateInstantion=new LocalEnactor();
var inputs0=[];
var outputs0=[];

var url="http://localhost:7075/book/provapi/statements";

const mode = (process.argv[2] || 'notdefined').toLowerCase();
let templateInstantion2;

if (mode === 'local') {
    templateInstantion2 = new LocalEnactor();
} else if (mode === 'remote') {
    var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
    templateInstantion2 = new WebTemplateInvoker(url, accessToken);
} else {
    console.error('Usage: node run-plead-workflow.js [local|remote]');
    process.exit(1);
}


class ThisWorkflow extends PleadWorkflow {
    constructor(templateInstantion, inputs, outputs) {
        super(templateInstantion, inputs, outputs);
    }
    time() {
        // get now time as ISO string
        return new Date().toISOString();
    }
}

const pleadWorkflow=new ThisWorkflow(templateInstantion2,inputs0,outputs0);

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