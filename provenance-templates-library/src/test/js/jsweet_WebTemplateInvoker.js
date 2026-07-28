
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

        let result0 = this.si.postInstructionsInOut(this.url, inputs0, this.accessToken);
        let result1 = result0[0];

        let map=new java.util.HashMap();
        // for all keys in result1, add to map
        for (let key in result1) {
            if (result1.hasOwnProperty(key)) {
                map.put(key, result1[key]);
            }
        }
        let val=completer(map, cl);

        return val;
    }


}

module.exports = { WebTemplateInvoker };
