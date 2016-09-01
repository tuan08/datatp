define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'plugins/crawler/site/UIWebPageTypePattern',
  'text!plugins/crawler/site/UIURLAnalyzer.jtpl'
], function($, _, Backbone, UIUtil, UIWebPageTypePattern, Template) {
  var UIURLAnalyzer = Backbone.View.extend({
    label: "URL Analyzer",

    initialize: function(options) {
      this.siteConfig = options.siteConfig;
      this.urlInfo    = options.urlInfo;
      this.uiWebPageTypePattern = new UIWebPageTypePattern(); 
      this.uiWebPageTypePattern.setBeans(this.siteConfig.webPageTypePatterns) ;
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = {
        siteConfig: this.siteConfig, 
        urlInfo:    this.urlInfo 
      } ;
      $(this.el).html(this._template(params));

      this.uiWebPageTypePattern.setElement(this.$('.UIWebPageTypePattern')).render();
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
      
      var webPageTypePatterns = this.siteConfig.webPageTypePatterns;
      var selWebPageTypePattern = null;
      //find URLPattern config with the same type
      for(var i = 0; i < webPageTypePatterns.length; i++) {
        if(webPageTypePatterns[i].type == type) {
          selWebPageTypePattern = webPageTypePatterns[i];
          break;
        }
      }

      //Find an URLPattern with type is not set
      if(selWebPageTypePattern == null) {
        for(var i = 0; i < webPageTypePatterns.length; i++) {
          if(webPageTypePatterns[i].type == null) {
            selWebPageTypePattern = webPageTypePatterns[i];
            selWebPageTypePattern.type = type;
            break;
          }
        }
      }

      //If not exist URLPattern config with the same type, create a new one
      if(selWebPageTypePattern == null) {
        selWebPageTypePattern = { type: type };
        webPageTypePatterns.push(selWebPageTypePattern);
      } 
      if(selWebPageTypePattern.pattern == null) selWebPageTypePattern.pattern = [];
      var patternAlreadyExist = false;
      for(var i = 0; i < selWebPageTypePattern.pattern.length; i++) {
        var selPattern = selWebPageTypePattern.pattern[i];
        if(selPattern == pattern) {
          var patternAlreadyExist = true;
          break;
        }
      }
      if(!patternAlreadyExist) {
        selWebPageTypePattern.pattern.push(pattern);

        this.uiWebPageTypePattern.setBeans(this.siteConfig.webPageTypePatterns) ;

        var uiSiteConfig = this.getAncestorOfType('UISiteConfig') ;
        uiSiteConfig.onChangeSiteConfig(this.siteConfig);

        this.render();
      }
    }
  });
  
  return UIURLAnalyzer ;
});
