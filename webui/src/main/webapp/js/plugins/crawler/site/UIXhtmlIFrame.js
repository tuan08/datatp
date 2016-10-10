define([
  'jquery',
  'underscore', 
  'backbone',
  'util/XPath',
  'plugins/crawler/site/IFrameTool'
], function($, _, Backbone, XPath, IFrameTool) {

  var UIXhtmlContent = Backbone.View.extend({
    label: "Xhtml",

    initialize: function(options) {
      this.urlData = options.urlData;
      this.siteConfig   = options.siteConfig;
    },

    _template: _.template(`
      <div style="height: 100%">
        <div class="ui-tabs" style="margin: 2px 0px">
          <strong>URL:</strong> <%=url%>
        </div>
        <iframe id='XhtmlContentIFrame' style="width: 100%; height: calc(100% - 25px);" src='data:text/html;charset=utf-8,<html></html>'></iframe>
      </div>
     `),

    render: function() {
      var params = { url: this.urlData.urlInfo.url } ;
      $(this.el).html(this._template(params));
      this.addIFrameEventListener();
    },

    addIFrameEventListener: function() {
      var uiExtractConfig = this.uiExtractConfig;
      var iframe= document.getElementById('XhtmlContentIFrame');

      var iframeTool = new IFrameTool(iframe);
      try {
        iframeTool.html(this.urlData.xhtml);
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
        console.printJSON(xpath);
      };
      iframeTool.on('onmouseup', onSelectText);
      this.iframeTool = iframeTool;
    },

    events: {
    },
  });
  
  return UIXhtmlContent ;
});
