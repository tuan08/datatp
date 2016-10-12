var JSLIBS = "libs" ;

require.config({
  urlArgs: "bust=" + (new Date()).getTime(), //prevent cache for development
  baseUrl: 'js',
  waitSeconds: 60,
  
  paths: {
    jquery:       JSLIBS + '/jquery/jquery-3.1.0.min',
    jqueryui:     JSLIBS + '/jquery/jquery-ui-1.12.0/jquery-ui',
    underscore:   JSLIBS + '/underscore/underscore-1.8.3.min',
    backbone:     JSLIBS + '/backbonejs/backbonejs-1.3.3.min',

    nv:           JSLIBS + '/d3/nv/1.8.4-dev/nv.d3.min',
    d3:           JSLIBS + '/d3/d3.min'
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

    d3: {
      deps: [],
      exports: 'd3'
    },

    nv: {
      deps: ['d3'],
      exports: 'nv'
    }
  }
});

require([
  'jquery', 
  'underscore', 
  'backbone',
  'util/console',
  'plugins/uidemo/UIBody',
], function($, _, Backbone, console, UIBody) {

  var UIDemo = Backbone.View.extend({
    el: $("#UIDemoWS"),
    
    initialize: function () { this.uiBody = UIBody },
    
    render: function() {
      $(this.el).empty();
      $(this.el).unbind();
      this.uiBody.setElement($('#UIDemoWS')).render();
    }
  });

  var app = {
    view : { uiDemo: new UIDemo(), },

    initialize: function() { this.render() ; },

    render: function() { this.view.uiDemo.render() ; }
  } ;

  app.initialize() ;
});
