const fs = require('fs');

past={};
past.util={}
past.exception={}
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
past.exception.UnsupportedOperationException = class UnsupportedOperationException extends Error {
    constructor(message) {
        super(message);
        this.name = "UnsupportedOperationException";
    }
}
past.exception.IllegalArgumentException = class IllegalArgumentException extends Error {
    constructor(message) {
        super(message);
        this.name = "IllegalArgumentException";
    }
}


Map.prototype.put = function (x,y){
    this.set(x,y)
}
Map.prototype.containsKey = function (x){
    return this.has(x)
}

Array.prototype.size = function (){
    return this.length;
}
Array.prototype.get = function (i){
    return this[i];
}
Array.prototype.add = function (x){
    this.push(x);
}
global.AtomicInteger=class AtomicInteger {
    constructor(value) {
        this.value=value;
    }
    getAndIncrement() {
        let tmp=this.value
        this.value=tmp+1;
        return tmp
    }
    getAndDecrement() {
        let tmp=this.value
        this.value=tmp-1;
        return tmp
    }
}



const { PleadWorkflow } = require('org/openprovenance/book/workflows/PleadWorkflow.js');
const { LocalEnactor } = require('org/openprovenance/templates/catalogue/fs/integrator/LocalEnactor.js');
const { RemoteEnactor } = require('./fs_remoteEnactor.js');


var url="http://localhost:7075/book/provapi/statements";

const mode = (process.argv[2] || 'notdefined').toLowerCase();
let templateInstantion;

if (mode === 'local') {
    templateInstantion = new LocalEnactor(false);
} else if (mode === 'remote') {
    var accessToken2 = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
    templateInstantion = new RemoteEnactor(url, accessToken2);
} else {
    console.error('Usage: node run-fs-workflow.js [local|remote]');
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

let inputs=[];
let outputs=[];

const pleadWorkflow=new ThisWorkflow(templateInstantion,inputs,outputs);

pleadWorkflow.workflow(111,333,"inputfile", 123, 56, 78, 456, 768,'/home/bob',"2026-03-01T09:03:51.168987Z", "2026-03-01T09:03:51.168987Z");


console.log(templateInstantion.getHistory());
console.log("ID of last element in history " + outputs[outputs.length-1].ID);
console.log("last element in history: "); console.log(outputs[outputs.length-1]);

if (mode === 'local') {
    console.log(templateInstantion.getCounterMap());
    console.log(templateInstantion.getRecordedValues());
}