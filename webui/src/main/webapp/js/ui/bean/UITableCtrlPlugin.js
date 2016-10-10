define([
  'jquery', 'underscore', 'backbone',
  'ui/widget',
  'text!ui/bean/UITableCtrlPlugin.jtpl'
], function($, _, Backbone, widget, Template) {
  var UITableCtrlPlugin = Backbone.View.extend({
    initialize: function(options) {
    },

    uistate: {
      field: {
        control: { 
          active: "toggle", 
          tabSelect: function(name) { return this.active == name ? "active" : ""; },
          tabContent: function(name) { return this.active == name ? "display" : "none"; }
        }
      }
    },

    _template: _.template(Template),

    render: function() {
      var params = { 
        config:   this.uiTable.config, 
        beanInfo: this.uiTable.beanInfo,
        uistate:  this.uistate,
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

      "click  .onSelectFieldTabControl":  "onSelectFieldTabControl",
      'change input.onFieldToggle':       'onFieldToggle',
      'change input.onFieldWrap':         'onFieldWrap',


      "click  .onFieldOrderUp":  "onFieldOrderUp",
      "click  .onFieldOrderDown":  "onFieldOrderDown",
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

    onFieldToggle: function(evt) {
      var fieldName = $(evt.target).attr("name") ;
      var checked = $(evt.target).is(":checked") ;
      this.uiTable.setTableColumnVisible(fieldName, checked, true);
    },

    onSelectFieldTabControl: function(evt) {
      var tab = $(evt.target).closest("li").attr("tab");
      this.uistate.field.control.active = tab;
      this.render();
    },

    onFieldOrderUp: function(evt) {
      var field = $(evt.target).attr("field");
      var fieldNames = this.uiTable.beanInfo.fieldNames;
      for(var i = 0; i < fieldNames.length; i++) {
        if(field == fieldNames[i]) {
          if(i == 0) return ;
          fieldNames[i] = fieldNames[i - 1];
          fieldNames[i - 1] = field;
          break;
        }
      }
      this.render();
      this.uiTable.__refreshTable();
    },

    onFieldOrderDown: function(evt) {
      var field = $(evt.target).attr("field");
      var fieldNames = this.uiTable.beanInfo.fieldNames;
      for(var i = 0; i < fieldNames.length; i++) {
        if(field == fieldNames[i]) {
          if(i == fieldNames.length - 1) return ;
          fieldNames[i] = fieldNames[i + 1];
          fieldNames[i + 1] = field;
          break;
        }
      }
      this.render();
      this.uiTable.__refreshTable();
    }
  });


  return UITableCtrlPlugin ;
});
