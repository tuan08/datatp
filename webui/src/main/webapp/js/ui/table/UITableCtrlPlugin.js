define([
  'jquery', 'underscore', 'backbone',
  'ui/widget',
  'text!ui/table/UITableCtrlPlugin.jtpl'
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
      'click  .onSelectTableView': 'onSelectTableView',

      'change select.onAddTableGroupByField': 'onAddTableGroupByField',
      'click  .onRmTableGroupByField':        'onRmTableGroupByField',

      "click  .onSelectFieldTabControl":  "onSelectFieldTabControl",
      'change input.onFieldToggle':       'onFieldToggle',
      'change input.onFieldWrap':         'onFieldWrap',


      "click  .onFieldOrderUp":  "onFieldOrderUp",
      "click  .onFieldOrderDown":  "onFieldOrderDown",


      'change .onSelectFirePropertyChange': 'onSelectFirePropertyChange',
      'click  .onFirePropertyChange': 'onFirePropertyChange',
    },

    onSelectTableView: function(evt) {
      var view = $(evt.target).attr("view") ;
      this.uiTable.setTableView(view, true);
      this.render();
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
      this.uiTable.__refreshTable();
      this.render();
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
      this.uiTable.__refreshTable();
      this.render();
    },

    onSelectFirePropertyChange: function(evt) {
      var ele      = $(evt.target, ".onSelectFirePropertyChange") ;
      var object   = ele.attr("object") ;
      var op       = ele.attr("op") ;
      var property = ele.attr("property") ;
      var value    = ele.val() ;
      console.log("UITableCtrlPlugin: object = " + object + ", op = " + op + ", property = " + property + ", value = " + value);
      this.uiTable.firePropertyChange(object, op, property, value);
      this.uiTable.refreshWorkspace();
      this.render();
    },

    onFirePropertyChange: function(evt) {
      var ele      = $(evt.target) ;
      var object   = ele.attr("object") ;
      var op       = ele.attr("op") ;
      var property = ele.attr("property") ;
      var value    = ele.attr("value") ;
      console.log("UITableCtrlPlugin: object = " + object + ", op = " + op + ", property = " + property + ", value = " + value);
      this.uiTable.firePropertyChange(object, op, property, value);
      this.uiTable.refreshWorkspace();
      this.render();
    }
  });

  return UITableCtrlPlugin ;
});
