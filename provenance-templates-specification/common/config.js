// Shared ReSpec configuration for the PROV-TEMPLATE family of documents.
//
// Each document loads this file before its own inline script and writes
//
//     var respecConfig = ptmConfig({ shortName: "...", subtitle: "...", ... });
//
// The overrides are merged over the defaults below; localBiblio entries are merged
// key by key, so a document can add references without repeating the shared ones.

var PTM_BASE = "https://openprovenance.org/";
var PTM_PUBLISH_DATE = "2026-10-16";

var ptmEditors = [{
    name: "Luc Moreau",
    company: "University of Sussex",
}];

// Editors of PROV-TEMPLATE 2014-06-07, the previous version of this work.
var ptmFormerEditors = [{
    name: "Danius Michaelides",
    company: "University of Southampton",
    note: "editor of the 2014-06-07 version",
}, {
    name: "Trung Dong Huynh",
    company: "University of Southampton",
    url: "http://trungdong.github.io/",
    note: "editor of the 2014-06-07 version",
}];

var ptmPrevious = "https://openprovenance.org/prov-template-2014-06-07/";

// The header link to the previous version (ReSpec shows prevVersion only for W3C statuses).
var ptmPreviousLink = [{
    key: "Previous version",
    data: [{ value: "PROV-TEMPLATE, Unofficial Draft 07 June 2014", href: ptmPrevious }],
}];

var ptmBiblio = {
    // The family itself.
    "PROV-TEMPLATE-OVERVIEW": {
        title: "PROV-TEMPLATE-OVERVIEW: An Overview of Provenance Templates",
        href: "../prov-template-overview/",
        authors: ["Luc Moreau"],
        status: "Unofficial Draft",
        publisher: "openprovenance.org",
    },
    "PROV-TEMPLATE-PRIMER": {
        title: "PROV-TEMPLATE-PRIMER: A Primer for Provenance Templates",
        href: "../prov-template-primer/",
        authors: ["Luc Moreau"],
        status: "Unofficial Draft",
        publisher: "openprovenance.org",
    },
    "PROV-TEMPLATE": {
        title: "PROV-TEMPLATE: Provenance Templates, Bindings and Template Transformation Files",
        href: "../prov-template/",
        authors: ["Luc Moreau"],
        status: "Unofficial Draft",
        publisher: "openprovenance.org",
    },
    "PROV-TEMPLATE-ALGORITHMS": {
        title: "PROV-TEMPLATE-ALGORITHMS: Instantiation and Merge of Provenance Templates",
        href: "../prov-template-algorithms/",
        authors: ["Luc Moreau"],
        status: "Unofficial Draft",
        publisher: "openprovenance.org",
    },
    "PROV-TEMPLATE-LIBRARY": {
        title: "PROV-TEMPLATE-LIBRARY: A Library of Provenance Templates",
        href: "../prov-template-library/",
        authors: ["Luc Moreau"],
        status: "Unofficial Draft",
        publisher: "openprovenance.org",
    },
    // The previous version.
    "PROV-TEMPLATE-2014": {
        title: "PROV-TEMPLATE: A Template System for PROV Documents",
        href: "https://openprovenance.org/prov-template-2014-06-07/",
        authors: ["Danius Michaelides", "Trung Dong Huynh", "Luc Moreau"],
        status: "Unofficial Draft 07 June 2014",
        publisher: "openprovenance.org",
    },
    // Related work by the same group.
    "PROV-JSONLD": {
        title: "The PROV-JSONLD Serialization. A JSON-LD Representation for the PROV Data Model",
        href: "https://www.w3.org/submissions/prov-jsonld/",
        authors: ["Luc Moreau", "Trung Dong Huynh"],
        status: "W3C Member Submission 25 August 2024",
        publisher: "W3C",
    },
    "PROV-JSON": {
        title: "The PROV-JSON Serialization. A JSON Representation for the PROV Data Model",
        href: "https://www.w3.org/submissions/prov-json/",
        status: "W3C Member Submission 24 April 2013",
        publisher: "W3C",
    },
    "PROV-TEMPLATE-TSE": {
        title: "A Templating System to Generate Provenance",
        authors: ["Luc Moreau", "Belfrit Victor Batlajery", "Trung Dong Huynh", "Danius Michaelides", "Heather Packer"],
        href: "https://doi.org/10.1109/TSE.2017.2659745",
        status: "IEEE Transactions on Software Engineering 44(2), 2018",
        publisher: "IEEE",
    },
    "PROVTOOLBOX": {
        title: "ProvToolbox: a Java library to create and convert W3C PROV data model representations",
        href: "https://lucmoreau.github.io/ProvToolbox/",
        authors: ["Luc Moreau"],
        publisher: "openprovenance.org",
    },
    "TMPL-VOCAB": {
        title: "Provenance template vocabulary",
        href: "https://openprovenance.org/ns/tmpl.html",
        publisher: "openprovenance.org",
    },
    "OPENPROV": {
        title: "The openprov vocabulary",
        href: "https://openprovenance.org/ns/openprov",
        publisher: "openprovenance.org",
    },
    "JSON-SCHEMA": {
        title: "JSON Schema: A Media Type for Describing JSON Documents",
        authors: ["A. Wright", "H. Andrews", "B. Hutton", "G. Dennis"],
        href: "https://json-schema.org/draft/2020-12/json-schema-core",
        status: "Internet-Draft draft-bhutton-json-schema-01",
        publisher: "IETF",
    },
};

function ptmConfig(overrides) {
    var config = {
        specStatus: "unofficial",
        publishDate: PTM_PUBLISH_DATE,
        copyrightStart: "2014",
        editors: ptmEditors,
        latestVersion: null,
        github: null,
        license: "cc-by",
        lint: { "no-unused-dfns": false },
        localBiblio: {},
    };
    for (var key in overrides) {
        if (key !== "localBiblio") config[key] = overrides[key];
    }
    var biblio = Object.assign({}, ptmBiblio, overrides.localBiblio || {});
    config.localBiblio = biblio;
    if (config.shortName && !config.latestVersion) {
        config.latestVersion = PTM_BASE + config.shortName + "/";
    }
    return config;
}
