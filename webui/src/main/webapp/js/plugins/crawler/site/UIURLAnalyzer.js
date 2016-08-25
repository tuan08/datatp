define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'plugins/crawler/site/UIURLPattern',
  'text!plugins/crawler/site/UIURLAnalyzer.jtpl'
], function($, _, Backbone, UIUtil, UIURLPattern, Template) {
  var UIURLAnalyzer = Backbone.View.extend({
    label: "URL Analyzer",

    initialize: function(options) {
      this.siteConfig = options.siteConfig;
      this.urlInfo    = options.urlInfo;
      this.uiURLPattern = new UIURLPattern(); 
      this.uiURLPattern.setBeans(this.siteConfig.urlPatterns) ;
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = {
        siteConfig: this.siteConfig, 
        urlInfo:    this.urlInfo 
      } ;
      $(this.el).html(this._template(params));

      this.uiURLPattern.setElement(this.$('.UIURLPattern')).render();
    },

    getAncestorOfType: function(type) {
      return UIUtil.getAncestorOfType(this, type) ;
    },

    events: {
      'click .onIgnorePattern': 'onIgnorePattern',
      'click .onDetailPattern': 'onDetailPattern',
      'click .onListPattern':   'onListPattern'
    },
    
    onIgnorePattern: function(evt) { this._onURLPattern('ignore', evt); },

    onDetailPattern: function(evt) { this._onURLPattern('detail', evt); },

    onListPattern: function(evt) { this._onURLPattern('list', evt); },
    
    _onURLPattern: function(type, evt) {
      var eleA = $(evt.target).closest("a") ;
      var pattern = eleA.attr("pattern") ;
      console.log("type = " + type + ", pattern = " + pattern);
      
      var urlPatterns = this.siteConfig.urlPatterns;
      var selUrlPattern = null;
      for(var i = 0; i < urlPatterns.length; i++) {
        if(urlPatterns[i].type == type) {
          selUrlPattern = urlPatterns[i];
          break;
        }
      }
      if(selUrlPattern == null) {
        for(var i = 0; i < urlPatterns.length; i++) {
          if(urlPatterns[i].type == null) {
            selUrlPattern = urlPatterns[i];
            selUrlPattern.type = type;
            break;
          }
        }
      }
      if(selUrlPattern == null) {
        selUrlPattern = { type: type };
        urlPatterns.push(selUrlPattern);
      } 
      if(selUrlPattern.pattern == null) selUrlPattern.pattern = [];
      var regexPattern = ".*" + pattern + ".*";
      selUrlPattern.pattern.push(regexPattern);
      var uiSiteConfig = this.getAncestorOfType('UISiteConfig') ;
      uiSiteConfig.onChangeSiteConfig(this.siteConfig);
      this.render();
    }
  });
  
  return UIURLAnalyzer ;
});
