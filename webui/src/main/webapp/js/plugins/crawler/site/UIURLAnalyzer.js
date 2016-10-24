define([
  'jquery',
  'underscore', 
  'ui/UIView',
  'plugins/crawler/site/uicomp',
  'text!plugins/crawler/site/UIURLAnalyzer.jtpl'
], function($, _, UIView, uicomp, Template) {
  var UIURLAnalyzer = UIView.extend({
    label: "URL Analyzer",

    initialize: function(options) {
      this.siteConfig = options.siteConfig;
      this.urlInfo    = options.urlInfo;
      this.uiWebPageTypePattern = new uicomp.site.UIWebpageTypePattern().setSiteConfig(this.siteConfig);
    },
    
    _template: _.template(Template),

    render: function() {
      var params = {
        siteConfig: this.siteConfig, urlInfo:  this.urlInfo 
      } ;
      $(this.el).html(this._template(params));
      this.uiWebPageTypePattern.setElement(this.$('.UIWebPageTypePattern')).render();
    },

    events: {
      'click .onIgnorePattern': 'onIgnorePattern',
      'click .onDetailPattern': 'onDetailPattern',
      'click .onListPattern':   'onListPattern',

      "click .onPopupAction": "onPopupAction"
    },
    
    onIgnorePattern: function(evt) { this._onURLPattern('ignore', evt); },

    onDetailPattern: function(evt) { this._onURLPattern('detail', evt); },

    onListPattern: function(evt) { this._onURLPattern('list', evt); },

    onPopupAction: function(e) {
      var uiPopup = $(e.target).parent().find('.ui-widget-popup').first();
      if(uiPopup.css('display') == 'none') {
        var height = uiPopup.height();
        var leftVal = e.pageX + "px";
        var topVal  = e.pageY - height + "px";
        uiPopup.css({left: leftVal, top: topVal}).show().delay(1500).fadeOut("fast");
      } else {
        uiPopup.hide();
      }
    },

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

        this.getAncestorOfType("UISiteConfigBreadcumbs").broadcastSiteConfigChange(this.siteConfig) ;
        this.render();
      }
    }
  });
  
  return UIURLAnalyzer ;
});
