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

    nv:           JSLIBS + '/d3/nv/nv.d3.min',
    d3:           JSLIBS + '/d3/d3.min',
    plotly:       JSLIBS + '/plotly/plotly-latest.min'
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
    },

    plotly: {
      deps: ['jquery'],
      exports: 'plotly'
    }
  }
});

require([
  'jquery', 
  'util/console',
  'site/UIBanner',
  'site/UIFooter',
  'site/UIBody'
], function($, console, UIBanner, UIFooter, UIBody) {
  console.log("init main");
  var app = {
    view : {
      UIBanner: new UIBanner(),
      UIBody: UIBody,
      UIFooter: new UIFooter(),
    },

    initialize: function() {
      console.log("start initialize app in main") ;
      this.render() ;
      console.log("finish initialize app in main") ;
    },

    render: function() {
      this.view.UIBanner.render() ;
      this.view.UIBody.render() ;
      this.view.UIFooter.render() ;
    },

    reload: function() {
      var ROOT_CONTEXT = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/"));
      window.location = ROOT_CONTEXT + "/index.html" ;
    }
  } ;

  app.initialize() ;
});
