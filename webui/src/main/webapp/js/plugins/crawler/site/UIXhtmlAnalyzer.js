define([
  'jquery',
  'underscore', 
  'backbone',
  'util/XPath',
  'ui/UIBean',
  'ui/UIPopup',
  'ui/UIUtil',
  'plugins/crawler/site/UIExtractConfig',
  'plugins/crawler/site/IFrameTool'
], function($, _, Backbone, XPath, UIBean, UIPopup, UIUtil, UIExtractConfig, IFrameTool) {

  var UIXPathBean = UIBean.extend({
    label: "XPath",
    config: {
      beans: {
        bean: {
          label: 'XPath',
          fields: [
            { 
              field: "name",   label: "Name",
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'title', value: 'title' },
                    { label: 'description', value: 'description' },
                    { label: 'content', value: 'content' },
                  ];
                  return options ;
                }
              }
            },
            { field: "xpath",   label: "XPath", required: true }
          ],
          edit: {
            actions: [ 
              {
                action:'save', label: "Save", icon: "check",
                onClick: function(thisUI, beanConfig, beanState) { 
                  var bean = beanState.bean;
                  thisUI.uiExtractConfig.addExtractXPath(bean);
                  UIPopup.closePopup() ;
                }
              },
              {
                action:'cancel', label: "Cancel",
                onClick: function(thisUI, beanConfig, beanState) { 
                  UIPopup.closePopup() ;
                }
              },
            ],
          },
          view: {
            actions: [ ]
          }
        }
      }
    }
  });

  var UIXhtmlAnalyzer = Backbone.View.extend({
    label: "Xhtml Analyzer",

    initialize: function(options) {
      this.urlData = options.urlData;
      this.siteConfig   = options.siteConfig;
      this.uiExtractConfig = new UIExtractConfig({siteConfig: this.siteConfig, updateUISiteConfigOnChange: true}) ;
      this.uiExtractConfig.uiParent = this ;

      _.bindAll(this, 'render') ;
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },
    
    _template: _.template(
      "<div>" +
      "  <div class='UIExtractConfig'></div>" +
      "  <div><a class='ui-action onHighlightExtractContent'>Highlight Extract Content</a></div>" +
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
        var xpathBean = { name: "title", xpath: xpath.getJSoupXPathSelectorExp()} ;
        var uiXPathBean = new UIXPathBean();
        uiXPathBean.bind('bean', xpathBean, true);
        uiXPathBean.getBeanState('bean').editMode = true ;
        uiXPathBean.uiExtractConfig = uiExtractConfig ;
        var popupConfig = { title: "Add XPath", minWidth: 600, modal: true} ;
        UIPopup.activate(uiXPathBean, popupConfig) ;
      };
      iframeTool.on('onmouseup', onSelectText);
      this.iframeTool = iframeTool;
    },

    events: {
      'click .onHighlightExtractContent':   'onHighlightExtractContent'
    },
    
    onHighlightExtractContent: function(evt) { 
      var extractXPathConfigs = this.uiExtractConfig.getXPathConfigs();
      for(var i = 0; i < extractXPathConfigs.length; i++) {
        var extractXPathConfig = extractXPathConfigs[i];
        var color = "lightgray";
        if('title' ==  extractXPathConfig.name) color = "#ddc"
        else if('description' ==  extractXPathConfig.name) color = "#ddf"
        var xpath = extractXPathConfig.xpath;
        for(var j = 0; j < xpath.length; j++) {
          $(this.iframeTool.getIFrameDocument()).find(xpath[j]).css("background-color", color);
        }
      }
    }

  });
  
  return UIXhtmlAnalyzer ;
});
