define([
  'jquery',
  'underscore', 
  'backbone',
  'util/XPath',
  'ui/UIUtil',
  'plugins/crawler/site/UIExtractConfig',
  'plugins/crawler/site/IFrameTool'
], function($, _, Backbone, XPath, UIUtil, UIExtractConfig, IFrameTool) {
 
  var UIXhtmlAnalyzer = Backbone.View.extend({
    label: "Xhtml Analyzer",

    initialize: function(options) {
      this.urlStructure = options.urlStructure;
      this.siteConfig   = options.siteConfig;
      this.uiExtractConfig = new UIExtractConfig({siteConfig: this.siteConfig}) ;

      _.bindAll(this, 'render') ;
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },
    
    _template: _.template(
      "<div>" +
      "  <div class='UIExtractConfig'></div>" +
      "  <iframe id='XhtmlContentIFrame'  width='100%' height='700px' src='data:text/html;charset=utf-8,<html></html>'></iframe>" +
      "</div>"
     ),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
      this.uiExtractConfig.setElement(this.$('.UIExtractConfig')).render();
      this.addIFrameEventListener();
    },

    addIFrameEventListener: function() {
      var uiExtractConfig = this.uiExtractConfig;
      var iframe= document.getElementById('XhtmlContentIFrame');

      var iframeTool = new IFrameTool(iframe);
      try {
        iframeTool.html(this.urlStructure.xhtml);
      } catch(err) {
        console.error(err);
      }
 
      var onSelectText = function(evt){
        var hlText = iframeTool.getHighlightText();
        if(hlText == null || hlText == '') return;
        evt = evt ? evt : event;
        var elem = null;
        if (evt.srcElement)  elem = evt.srcElement;
        else if (evt.target) elem = evt.target;
        var xpath = new XPath($(elem)[0]);
        uiExtractConfig.addExtractXPath({ name: "", xpath: xpath.getJSoupXPathSelectorExp()}) ;
      };
      iframeTool.on('onmouseup', onSelectText);
    }
  });
  
  return UIXhtmlAnalyzer ;
});
