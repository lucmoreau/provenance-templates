


const { BeanHistory } = require('org/openprovenance/templates/catalogue/fs/integrator/BeanHistory.js');
const { WebTemplateInvoker } = require('./fs_WebTemplateInvoker.js');

class RemoteEnactor extends BeanHistory {
  constructor(url, accessToken) {
    super(new WebTemplateInvoker(url, accessToken), []);

  }
}

module.exports = { RemoteEnactor, };
