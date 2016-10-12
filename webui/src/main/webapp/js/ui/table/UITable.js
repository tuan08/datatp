define([
  'jquery', 'underscore',
  'util/util',
  "ui/UIDialog",
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  "ui/bean/UIBean",
  "ui/table/UITableCtrlPlugin",
  "ui/table/UITableWorkspace",
], function($, _, util, UIDialog, UIBorderLayout, UITabbedPane, UIBean, UITableCtrlPlugin, UITableWorkspace) {
  var UITableCtrlTabbedPane = UITabbedPane.extend({
    label: 'Tabbed Pane Demo',

    config: {
      style: "ui-tabs",
      header: { title: "Table Control" }
    },

    addPluginUI: function(name, plugin, active) {
      this.addTab(name, name, plugin, false, active);
    },
  });

  var UITable = UIBorderLayout.extend({
    label: 'UITable Demo',
    defaultConfig: { 
      control: { 
        header: "Table Control" 
      },
      table: { 
        header: "Table",
        page:   { 
          size: 10, 
          select: 1, 
          options: [ 10, 25, 50, 100, 200, 500, 1000 ]
        }, 
        column:  { },
        border: { type: "default" },
        actions: { visible: true },
        groupBy: { fields: {} },
        chart: { type: "BarChart" },
        view: "table"
      },
      actions: { },
      filter: { field: "__all", expression: "" },
      load: { more: [  50, 100, 200, 500 ] }
    },

    _onInit: function(options) {
      this.config = this.mergeConfig(null);

      var centerConfig = {};
      this.uiTableWorkspace = new UITableWorkspace({ uiTable: this });
      this.setUI('center', this.uiTableWorkspace, centerConfig);
    },


    set: function(beanInfo, beans) {
      this.beanInfo = $.extend(true, {}, beanInfo);
      this.beanInfo.fieldNames = [];
      this.beanInfo.fieldLabels = [];
      for(var name in beanInfo.fields) {
        this.beanInfo.fieldNames.push(name);
        this.beanInfo.fieldLabels.push(beanInfo.fields[name].label);
      }
      this.__createBeanStates(beans);
      this.__filter(this.config.filter.field, this.config.filter.expression);
      this.fireDataChange();
      return this;
    },

    setBeans: function(beans, refresh) {
      this.__createBeanStates(beans);
      this.__filter(this.config.filter.field, this.config.filter.expression);
      this.fireDataChange();
      if(refresh) this.uiTableWorkspace.onRefresh();
      return this;
    },
    
    addControlPluginUI: function(name, plugin) {
      var uiTableCtrl = this.getUI("west"); 
      if(uiTableCtrl == null) {
        var westConfig = { width: "250px"};
        uiTableCtrl = new UITableCtrlTabbedPane();
        uiTableCtrl.config.header.title = this.config.control.header;
        this.setUI('west', uiTableCtrl, westConfig);
      }
      plugin.uiTable = this;
      uiTableCtrl.addPluginUI(name, plugin);
      return this;
    },

    addDefaultControlPluginUI: function() {
      this.addControlPluginUI("Table", new UITableCtrlPlugin(), true);
    },

    addWorkspaceTabPluginUI: function(name, label, uiPlugin, closable, active) {
      this.uiTableWorkspace.addTabPlugin(name, label, uiPlugin, closable, active);
      return this;
    },

    toggleControl: function() { 
      this.toggleUISplit('west'); 
    },

    setTableView: function(view, refresh) { 
      this.config.table.view = view;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    getTableColumns: function() { return this.beanInfo.fieldNames; },

    setTableColumnVisible: function(field, visible, refresh) { 
      if(!this.config.table.column[field]) this.config.table.column[field] = {}; 
      this.config.table.column[field].hidden = !visible;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    setTableColumnsVisible: function(fields, visible, refresh) { 
      for(var i = 0; i < fields.length; i++) {
        var field = fields[i]
        if(!this.config.table.column[field]) this.config.table.column[field] = {}; 
        this.config.table.column[field].hidden = !visible;
      }
      this.firePropertyChange("config", "change", "table.column.fields", fields) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    setTableSelectPage: function(page, refresh) { 
      this.config.table.page.select = page ;
      this.firePropertyChange("config", "set", "table.page.select", page) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    setTablePageSize: function(pageSize, refresh) { 
      this.config.table.page.size = pageSize ;
      this.firePropertyChange("config", "set", "table.page.size", pageSize) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    addTableGroupByField: function(field, refresh) { 
      this.config.table.groupBy.fields[field] = {} ;
      this.firePropertyChange("config", "add", "table.groupBy.fields", field) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    setTableGroupByFields: function(fields, refresh) { 
      this.config.table.groupBy.fields = {} ;
      for(var i = 0; i < fields.length; i++) {
        this.config.table.groupBy.fields[fields[i]] = {} ;
      }
      this.firePropertyChange("config", "set", "table.groupBy.fields", fields) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    rmTableGroupByField: function(field, refresh) { 
      delete this.config.table.groupBy.fields[field] ;
      this.firePropertyChange("config", "remove", "table.groupBy.fields", field) ;
      if(refresh) this.uiTableWorkspace.onRefresh();
    },

    removeBeanState: function(beanState, refresh) {
      var idx = this.beanStates.indexOf(beanState);
      this.beanStates.splice(idx, 1);
      var idx = this.filterBeanStates.indexOf(beanState) ;
      if(idx >= 0) this.filterBeanStates.splice(idx, 1);
      this.fireDataChange();
      if(refresh) this.uiTableWorkspace.onRefresh();
    },
    
    onToolbarAction: function(actionName) {
      this.config.actions.toolbar[actionName].onClick(this);
    },

    onBeanAction: function(actionName, beanState) {
      this.config.actions.bean[actionName].onClick(this, beanState);
    },


    filter: function(field, exp, refresh) { 
      this.config.filter.field      = field;
      this.config.filter.expression = exp;
      this.__filter(field, exp);
      this.fireDataChange();
      if(refresh) this.uiTableWorkspace.onRefresh();;
    },

    refreshWorkspace: function() { this.uiTableWorkspace.render(); },

    fireDataChange: function() { this.uiTableWorkspace.fireDataChange(); },

    firePropertyChange: function(object, op, property, value) { 
      this.uiTableWorkspace.firePropertyChange(object, op,  property, value); 
    },

    __filter: function(field, exp) { 
      if(exp == null || exp == '') {
        this.filterBeanStates = this.beanStates ;
        this.viewModel = null;
        return;
      }
      
      var selectFields = null;
      if("__all" == field) {
        selectFields = this.beanInfo.fieldNames;
      } else if("__visible" == field) {
        selectFields = [];
        for(var i = 0; i < this.beanInfo.fieldNames.length; i++) {
          var fName = this.beanInfo.fieldNames[i];
          if(!this.config.field.hidden[fName]) selectFields.push(fName);
        }
      } else {
        selectFields = [field];
      }

      this.filterBeanStates = [] ;
      for(var i = 0;i < this.beanStates.length; i++) {
        var beanState = this.beanStates[i] ;
        var bean = beanState.bean ;
        var match = false;
        for(var j = 0; j < selectFields.length; j++) {
          var fName = selectFields[j];
          var fieldVal = util.reflect.getFieldValue(bean, fName) ;
          if(fieldVal == null) continue;
          if (typeof fieldVal === 'string' || fieldVal instanceof String) {
          } else {
            fieldVal = String(fieldVal);
          }
          if(fieldVal.indexOf(exp) >= 0) {
            match = true;
            break;
          }
        }
        if(match) this.filterBeanStates.push(beanState) ;
      }
      this.viewModel = null;
    },

    __createBeanStates: function(beans) {
      this.beanStates = [] ;
      for(var i = 0; i < beans.length; i++) {
        var bState = { bean: beans[i] };
        this.beanStates.push(bState);
      }
    },

    __refreshTable: function() {
      this.uiTableWorkspace.onRefresh();
    }
  });

  return UITable;
});
