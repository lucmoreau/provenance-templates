


const { BeanHistory } = require('org/openprovenance/templates/catalogue/transport/integrator/BeanHistory.js');
const { WebTemplateInvoker } = require('./box_WebTemplateInvoker.js');

class RemoteEnactor extends BeanHistory {
  constructor(url, accessToken) {
    super(new WebTemplateInvoker(url, accessToken), []);

  }
}

module.exports = { RemoteEnactor };
