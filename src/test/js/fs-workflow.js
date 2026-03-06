
const fs = require('fs');

const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;


const { LocalEnactor } = require('./LocalEnactor');

const { WebTemplateInvoker } = require('./WebTemplateInvoker');



var templateInstantion=new LocalEnactor();
var inputs0=new java.util.LinkedList();
var outputs0=new java.util.LinkedList();

var url="http://localhost:7075/book/provapi/statements";
var accessToken = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();


var templateInstantion2=new WebTemplateInvoker(url, accessToken);


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