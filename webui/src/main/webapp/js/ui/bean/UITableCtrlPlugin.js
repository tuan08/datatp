define([
  'jquery', 'underscore', 'backbone',
  'ui/widget',
  'text!ui/bean/UITableCtrlPlugin.jtpl'
], function($, _, Backbone, widget, Template) {
  var UITableCtrlPlugin = Backbone.View.extend({
    initialize: function(options) {
    },

    _template: _.template(Template),

    render: function() {
      var params = { 
        config:   this.uiTable.config, 
        beanInfo: this.uiTable.beanInfo,
        plugins:  this.plugins,
        widget:   widget
      };
      $(this.el).html(this._template(params));
    },
    
    events: {
      'change select.onSelectDisplayRow': 'onSelectDisplayRow',

      'keyup  .onFilter': 'onFilter',
      'change .onFilter': 'onFilter',
      'blur   .onFilter': 'onFilter',

      "click  .onSelectFieldTabControl":  "onSelectFieldTabControl",
      'change input.onFieldToggle':       'onFieldToggle',
      'change input.onFieldWrap':         'onFieldWrap',
    },

    onSelectDisplayRow: function(evt) {
      var pageSize = $(evt.target, ".onSelectDisplayRow").find(":selected").attr("value") ;
      this.uiTable.updateDisplayRow(pageSize);
    },


    onFilter: function(evt) {
      var filterBlk = $(evt.target).closest(".filter");
      var field = filterBlk.find("select.input").val();
      var exp   = filterBlk.find("input.input").val();
      this.uiTable.filter(field, exp);
    },

    onFieldToggle: function(evt) {
      var fieldName = $(evt.target).attr("name") ;
      var checked = $(evt.target).is(":checked") ;
      this.uiTable.setFieldVisible(fieldName, checked);
    },

    onSelectFieldTabControl: function(evt) {
      var uiTabContents = $(evt.target).closest(".ui-card").find(".ui-tab-contents");

      var uiActiveTab   = $(evt.target).closest(".ui-tabs").find("li.active");
      uiActiveTab.removeClass("active");
      uiTabContents.children("[tab=" + uiActiveTab.attr("tab") + "]").css("display", "none");

      var uiTab = $(evt.target).closest("li");
      uiTab.addClass("active");
      uiTabContents.children("[tab=" + uiTab.attr("tab") + "]").css("display", "block");
    },

    onFieldWrap: function(evt) {
      console.log("on field wrap");
    }
  });


  return UITableCtrlPlugin ;
});
