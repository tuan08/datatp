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
      'change select.onSelectTableView': 'onSelectTableView',
      'change select.onSelectTablePageSize': 'onSelectTablePageSize',

      'change select.onAddTableGroupByField': 'onAddTableGroupByField',
      'click  .onRmTableGroupByField':        'onRmTableGroupByField',

      'keyup  .onFilter': 'onFilter',
      'change .onFilter': 'onFilter',
      'blur   .onFilter': 'onFilter',

      "click  .onSelectFieldTabControl":  "onSelectFieldTabControl",
      'change input.onFieldToggle':       'onFieldToggle',
      'change input.onFieldWrap':         'onFieldWrap',
    },

    onSelectTableView: function(evt) {
      var view = $(evt.target, ".onSelectTableView").find(":selected").attr("value") ;
      var uiCard = $(evt.target).closest(".ui-card") ;
      var uiViews = uiCard.find(".views").first() ;
      uiViews.children("div").css("display", "none");
      uiViews.find("." + view + "-view").css("display", "block");
      this.uiTable.setTableView(view, true);
    },

    onSelectTablePageSize: function(evt) {
      var pageSize = $(evt.target, ".onSelectTablePageSize").find(":selected").attr("value") ;
      this.uiTable.setTablePageSize(pageSize, true);
    },

    onAddTableGroupByField: function(evt) {
      var fieldName = $(evt.target, ".onAddTableGroupByField").val() ;
      this.uiTable.addTableGroupByField(fieldName, true);
      this.render();
    },

    onRmTableGroupByField: function(evt) {
      var fieldName = $(evt.target).attr("field") ;
      this.uiTable.rmTableGroupByField(fieldName, true);
      this.render();
    },

    onFilter: function(evt) {
      var filterBlk = $(evt.target).closest(".filter");
      var field = filterBlk.find("select.input").val();
      var exp   = filterBlk.find("input.input").val();
      this.uiTable.filter(field, exp, true);
    },

    onFieldToggle: function(evt) {
      var fieldName = $(evt.target).attr("name") ;
      var checked = $(evt.target).is(":checked") ;
      this.uiTable.setTableColumnVisible(fieldName, checked, true);
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
