


const { BeanLocalEnactor2 } = require('org/openprovenance/templates/catalogue/transport/integrator/BeanLocalEnactor2.js');
const { BeanHistory } = require('org/openprovenance/templates/catalogue/transport/integrator/BeanHistory.js');
const {IdentifierRegistry} = require("./IdentifierRegistry");



class BeanLocalEnactor3 extends BeanLocalEnactor2 {
    constructor(counterMap, recordedValues) {
        super();
        this.identifierRegistry=new IdentifierRegistry(counterMap, recordedValues);
    }
    newIdentifier(field, counter) {
        return this.identifierRegistry.newIdentifier(field, counter);
    }
    getCounterMap() {
        return this.identifierRegistry.counterMap
    }
    getRecordedValues(){
        return this.identifierRegistry.recordedValues
    }

}

class LocalEnactor extends BeanHistory {
    constructor() {
        super(new BeanLocalEnactor3(new Map(),new Map()), []);
    }

    getCounterMap() {
        return super.getDelegator().getCounterMap();
    }
    getRecordedValues() {
        return super.getDelegator().getRecordedValues();
    }
}




module.exports = { LocalEnactor };


