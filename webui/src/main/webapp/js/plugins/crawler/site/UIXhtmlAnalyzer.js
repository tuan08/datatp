define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'text!plugins/crawler/site/UIXhtmlAnalyzer.jtpl'
], function($, _, Backbone, UIUtil,  Template) {
  var UIXhtmlAnalyzer = Backbone.View.extend({
    label: "Xhtml Analyzer",

    initialize: function(options) {
      this.urlStructure = options.urlStructure;
      this.siteConfig   = options.siteConfig;
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = {
        siteConfig:   this.siteConfig,
        urlStructure: this.urlStructure,
        //xhtmlUrl: "http://localhost:8080/crawler/site/analyzed-url-xhtml?url=" + encodeURIComponent(this.urlStructure.urlAnalyzer.url)
        xhtmlUrl: this.urlStructure.urlAnalyzer.url
        //xhtmlUrl: "http://localhost:8080/crawler/site/analyzed-url-xhtml?url=test"
      } ;
      $(this.el).html(this._template(params));
    },

    getAncestorOfType: function(type) {
      return UIUtil.getAncestorOfType(this, type) ;
    },

    events: {
      'click .onIgnorePattern': 'onIgnorePattern',
    }
  });
  
  return UIXhtmlAnalyzer ;
});
