define([
  'jquery',
  'underscore', 
  'ui/UIView',
  'util/XPath',
  'plugins/crawler/site/IFrameTool'
], function($, _, UIView, XPath, IFrameTool) {

  var UIXhtmlIFrame = UIView.extend({
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
        <iframe id="XhtmlContentIFrame" style="width: 100%; height: calc(100% - 25px);"></iframe>
      </div>
     `),

    getCurrentSelectXPath: function() { return this.currentSelectXPath; } ,

    render: function() {
      var params = { url: this.urlData.urlInfo.url } ;
      $(this.el).html(this._template(params));
      this.addIFrameEventListener();
    },

    addIFrameEventListener: function() {
      var uiExtractConfig = this.uiExtractConfig;

      var iframeTool = new IFrameTool(document.getElementById('XhtmlContentIFrame'));
      try {
        iframeTool.html(this.urlData.xhtml);
      } catch(err) {
        console.error(err);
      }
 
      var uiIFrame  = this;
      var onSelectText = function(evt){
        var hlText = iframeTool.getHighlightText();
        if(hlText == null || hlText == '') return;
        evt = evt ? evt : event;
        var elem = null;
        if (evt.srcElement)  elem = evt.srcElement;
        else if (evt.target) elem = evt.target;
        uiIFrame.currentSelectXPath = new XPath($(elem)[0]);
      };

      iframeTool.on('onmouseup', onSelectText);
      this.iframeTool = iframeTool;
    },

    events: {
    },
  });
  
  return UIXhtmlIFrame ;
});
