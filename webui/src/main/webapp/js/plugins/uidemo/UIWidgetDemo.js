define([
  'jquery', 
  'underscore', 
  'ui/UIView',
  'text!plugins/uidemo/UIWidgetDemo.jtpl'
], function($, _, UIView, Template) {
  var UIWidgetDemo = UIView.extend({
    label: 'Widget Demo',

    config: {
      label: "Widget Demo",
    },

    _template: _.template(Template),

    render: function() {
      var params = { config: this.config };
      $(this.el).html(this._template(params));
    },

    events: {
      'mouseover .onPopupFadeout': 'onPopupFadeout',
      "mouseover .popup-trigger":   "onMouseOverPopup",
      "mouseout  .hide-trigger":   "onMouseOutFadeout",
    },

    onPopupFadeout: function(e) {
      var uiPopup = $(e.target).parent().find('.ui-widget-popup').first();
      var height = uiPopup.height();
      var width  = uiPopup.width();
      var leftVal = e.pageX - (width/2) + "px";
      var topVal  = e.pageY - (height/2) + "px";
      uiPopup.css({left: leftVal, top: topVal}).show().delay(1500).fadeOut(500);
    },

    onMouseOverPopup: function(e) {
      $(e.target).removeClass("popup-trigger");
      var uiPopup = $(e.target).parent().find('.ui-widget-popup').first();
      if(uiPopup.css('display') != 'none') return;
      var height = uiPopup.height();
      var leftVal = e.pageX + "px";
      var topVal  = e.pageY - height + "px";
      uiPopup.css({left: leftVal, top: topVal}).show();
    },

    onMouseOutFadeout: function(e) {
      var uiPopup = $(e.target).parent().find('.ui-widget-popup').first();
      var height = uiPopup.height();
      var leftVal = e.pageX + "px";
      var topVal  = e.pageY - height + "px";
      //uiPopup.css({left: leftVal, top: topVal}).fadeOut(1500);
      uiPopup.css({left: leftVal, top: topVal}).hide();
      $(e.target).addClass("popup-trigger");
    },

  });

  return new UIWidgetDemo() ;
});
