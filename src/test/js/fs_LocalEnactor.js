


const { BeanLocalEnactor2 } = require('org/openprovenance/templates/catalogue/fs/integrator/BeanLocalEnactor2.js');
const { BeanHistory } = require('org/openprovenance/templates/catalogue/fs/integrator/BeanHistory.js');



class BeanLocalEnactor3 extends BeanLocalEnactor2 {
    constructor(counterMap, recordedValues) {
        super();
        this.negative = false;
        this.counterInitialValue = this.sign() * 10000;
        this.counterMap = counterMap;
        this.recordedValues =recordedValues;
    }

    newIdentifier(field, counter) {
        if (!this.counterMap.has(counter)) {
            this.counterInitialValue = this.counterInitialValue + this.sign() * 10000;
            this.counterMap.set(counter, {value: this.counterInitialValue});
        }
        const entry = this.counterMap.get(counter);
        const newValue = this.negative ? entry.value-- : entry.value++;
        if (!this.recordedValues.has(field)) {
            this.recordedValues.set(field, []);
        }
        this.recordedValues.get(field).push(newValue);
        console.log("newIdentifier " + field + " " + counter + " " + newValue)
        return newValue;
    }
    sign() {
        return this.negative ? -1 : 1;
    }
    getCounterMap() {
        return this.counterMap
    }
    getRecordedValues(){
        return this.recordedValues
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


