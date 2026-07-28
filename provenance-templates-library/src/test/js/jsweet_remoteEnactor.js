
const fs = require('fs');

const j4ts = require('../resources/j4ts-bundle.js');
global.java = j4ts.java;
global.javaemul = j4ts.javaemul;

const provfs = require('../../../target/js/bundle.js');

var org = provfs.org;




const { WebTemplateInvoker } = require('./jsweet_WebTemplateInvoker.js');

class RemoteEnactor extends org.openprovenance.templates.catalogue.fs.integrator.BeanHistory {
  constructor(url, accessToken) {
    super(new WebTemplateInvoker(url, accessToken), new java.util.LinkedList());

  }
}

module.exports = { RemoteEnactor, };
