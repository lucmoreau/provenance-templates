
const fs = require('fs');
const { FetchWorker } = require('./fetch-worker');
const { TemplateInvoker } = require('org/openprovenance/templates/catalogue/fs/integrator/TemplateInvoker.js');


class ServiceInvoker {

    constructor() {
        this.fetcher = new FetchWorker();
    }

    postInstructionsInOut(url, body, accessToken) {
        const token = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
        return this.fetcher.post(url, [body], token);
    }

}



class WebTemplateInvoker extends TemplateInvoker {
    constructor(url,token) {
        super();
        this.si = new ServiceInvoker();
        this.accessToken =token;
        this.url = url;
    }

    generic_post_and_return(cl, inputs0, completer) {

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

        return val;
    }



}

module.exports = { WebTemplateInvoker };
