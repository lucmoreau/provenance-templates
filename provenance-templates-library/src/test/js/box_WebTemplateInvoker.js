
const fs = require('fs');
const { FetchWorker } = require('./fetch-worker');
const { TemplateInvoker } = require('org/openprovenance/templates/catalogue/transport/integrator/TemplateInvoker.js');


class ServiceInvoker {

    constructor() {
        this.fetcher = new FetchWorker();
    }

    postInstructionsInOut(url, body, accessToken, debug) {
        //const token = fs.readFileSync('/Users/luc/.keycloak_token', 'utf8').trim();
        return this.fetcher.post(url, [body], accessToken, debug);
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
        let map = this.makeMapFromObject(result1);
        let val=completer(map,  cl);


        return val;
    }


    makeMapFromObject(o) {
        let map = new Map();
        // for all keys in result1, add to map

        for (let key in o) {
            if (o.hasOwnProperty(key)) {
                let value = o[key];
                if (key === "__elements") {
                    let ll = []
                    var len = value.length;
                    for(var i = 0; i < len; i++) {
                        let subMap = new Map();
                        let entry=value[i]
                        for (let subKey in entry) {
                            if (entry.hasOwnProperty(subKey)) {
                                subMap.put(subKey, entry[subKey]);
                            }
                        }
                        ll.push(subMap);
                    }
                    map.put(key, ll);
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}

module.exports = { WebTemplateInvoker };
