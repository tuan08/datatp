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
        xhtmlUrl: "http://localhost:8080/crawler/site/analyzed-url-xhtml?url=" + encodeURIComponent(this.urlStructure.urlAnalyzer.url)
      } ;
      $(this.el).html(this._template(params));

      var iframeWin = this.getXhtmlContentIframeWindow();
      iframeWin.onmouseup = function(evt){
        evt = evt ? evt : event;
        var elem = null;
        if (evt.srcElement)  elem = evt.srcElement;
        else if (evt.target) elem = evt.target;
        var ele = $(elem);
        console.log("Tag: " + ele.get(0).nodeName);
        console.log(ele.html());
      };
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },

    events: {
      'click .onFindSelectXPath': 'onFindSelectXPath',
    },
   
    getHighlightText: function(iframe) {
      var frameWindow = iframe && iframe.contentWindow; 
      var frameDocument = frameWindow && frameWindow.document; 
      var text = "???";
      if (frameDocument) { 
        if (frameDocument.getSelection) { 
          // Most browsers 
          text = String(frameDocument.getSelection()); 
        } else if (frameDocument.selection) { 
          // Internet Explorer 8 and below 
          text = frameDocument.selection.createRange().text; 
        } else if (frameWindow.getSelection) { 
          // Safari 3 
          text = String(frameWindow.getSelection()); 
        } 
      }
      return text; 
    },

    getXhtmlContentIframeWindow: function() {
      var iframe= document.getElementById('XhtmlContentIFrame');
      return iframe.contentWindow ;
    },

    getXhtmlContentIframeDoc: function() {
      var iframe= document.getElementById('XhtmlContentIFrame');
      var iframeDoc = null;
      if(iframe.contentWindow) iframeDoc = iframe.contentWindow.document ;
      else iframeDoc =  iframe.contentDocument;
      return iframeDoc;
    }
  });
  
  return UIXhtmlAnalyzer ;
});
