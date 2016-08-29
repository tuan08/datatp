var JSLIBS = "libs" ;

"use strict";
require.config({
  urlArgs: "bust=" + (new Date()).getTime(), //prevent cache for development
  baseUrl: 'js',
  waitSeconds: 60,

  paths: {
    jquery:       JSLIBS + '/jquery/jquery-3.1.0.min',
    jqueryui:     JSLIBS + '/jquery/jquery-ui-1.12.0/jquery-ui',
    underscore:   JSLIBS + '/underscore/underscore-1.8.3.min',
    backbone:     JSLIBS + '/backbonejs/backbonejs-1.3.3.min',

    QUnit:        JSLIBS + '/qunit/qunit-2.0.1'
  },

  shim: {
    jquery: { exports: '$' },

    jqueryui: {
      deps: ["jquery"],
      exports: "jqueryui"
    },

    underscore: { exports: '_' },

    backbone: {
      deps: ["underscore", "jquery"],
      exports: "Backbone"
    },

    QUnit: {
      exports: 'QUnit',
      init: function() {
        QUnit.config.autoload = false;
        QUnit.config.autostart = false;
      }
    } 
  }
});

// require the unit tests.
require( [
  'QUnit', 
  'tests/dummyTest', 
  'tests/util/XPathTest', 
], function(QUnit, dummy, XPathTest) {
    // run the tests.
    dummy.run();
    XPathTest.run();

    // start QUnit.
    QUnit.load();
    QUnit.start();
});
