define([
  'jquery', 'underscore',
  'util/util',
  'util/PageList',
  'ui/UIUtil',
  "ui/UIDialog",
  "ui/bean/UIBean",
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  "ui/bean/UITableCtrlPlugin",
  "ui/bean/UITableWorkspace",
  "ui/bean/bucket"
], function($, _, util, PageList, UIUtil, UIDialog, UIBean, UIBorderLayout, UITabbedPane, UITableCtrlPlugin, UITableWorkspace, bucket) {
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

    _onInit: function(options) {
      var tConfig = { 
        control: { 
          header: "Table Control" 
        },
        table: { 
          header: "Table",
          page:   { 
            size: 10, 
            options: [ 10, 25, 50, 100, 200, 500, 1000 ]
          }, 
          column:  { },
          border: { type: "default" },
          actions: { visible: true },
          groupBy: { fields: {} },
          view: "table"
        },
        actions: { },
        filter: { field: "__all", expression: "" },
        load: { more: [  50, 100, 200, 500 ] }
      };

      //clone config to isolate the modification
      if(this.config)  $.extend(true, tConfig, this.config);
      this.config = tConfig;


      var centerConfig = {};
      this.uiTableWS = new UITableWorkspace({ uiTable: this });
      this.setUI('center', this.uiTableWS, centerConfig);
    },


    set: function(beanInfo, beans) {
      this.beanInfo = $.extend({}, beanInfo);
      this.beanInfo.fieldNames = [];
      this.beanInfo.fieldLabels = [];
      for(var name in beanInfo.fields) {
        this.beanInfo.fieldNames.push(name);
        this.beanInfo.fieldLabels.push(beanInfo.fields[name].label);
      }
      this.__createBeanStates(beans);
      this.__filter(this.config.filter.field, this.config.filter.expression);
      this.__createViewModel();
      return this;
    },

    setBeans: function(beans, refresh) {
      this.__createBeanStates(beans);
      this.__filter(this.config.filter.field, this.config.filter.expression);
      if(refresh) this.__refreshTable();
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
      this.uiTableWS.addTabPlugin(name, label, uiPlugin, closable, active);
      return this;
    },

    toggleControl: function() { 
      this.toggleUISplit('west'); 
    },

    setTableView: function(view, refresh) { 
      this.config.table.view = view;
      if(refresh) this.__refreshTable();
    },

    getTableColumns: function() { return this.beanInfo.fieldNames; },

    setTableColumnVisible: function(field, visible, refresh) { 
      if(!this.config.table.column[field]) this.config.table.column[field] = {}; 
      this.config.table.column[field].hidden = !visible;
      if(refresh) this.__refreshTable();
    },

    setTableColumnsVisible: function(fields, visible, refresh) { 
      for(var i = 0; i < fields.length; i++) {
        var field = fields[i]
        if(!this.config.table.column[field]) this.config.table.column[field] = {}; 
        this.config.table.column[field].hidden = !visible;
      }
      if(refresh) this.__refreshTable();
    },

    setTableSelectPage: function(page, refresh) { 
      this.viewModel.table.getPage(page) ;
      if(refresh) this.__refreshTable();
    },

    setTablePageSize: function(pageSize, refresh) { 
      this.config.table.page.size = pageSize ;
      this.viewModel.table.setPageSize(pageSize) ;
      if(refresh) this.__refreshTable();
    },

    addTableGroupByField: function(field, refresh) { 
      this.config.table.groupBy.fields[field] = {} ;
      this.viewModel.groupBy = null;
      if(refresh) this.__refreshTable();
    },

    setTableGroupByFields: function(fields, refresh) { 
      this.config.table.groupBy.fields = {} ;
      for(var i = 0; i < fields.length; i++) {
        this.config.table.groupBy.fields[fields[i]] = {} ;
      }
      if(this.viewModel) this.viewModel.groupBy = null;
      if(refresh) this.__refreshTable();
    },

    rmTableGroupByField: function(field, refresh) { 
      delete this.config.table.groupBy.fields[field] ;
      this.viewModel.groupBy = null;
      if(refresh) this.__refreshTable();
    },

    removeBeanState: function(beanState, refresh) {
      var idx = this.beanStates.indexOf(beanState);
      this.beanStates.splice(idx, 1);
      if(this.viewModel) {
        if(this.viewModel.table) {
          var idx = this.filterBeanStates.indexOf(beanState) ;
          if(idx >= 0) this.filterBeanStates.splice(idx, 1);
          var currPage = this.viewModel.table.getCurrentPage() ;
          this.viewModel.table = new PageList(this.config.table.page.size, this.filterBeanStates) ;
          this.viewModel.table.getPage(currPage);
        }
        if(this.viewModel.groupBy) this.viewModel.groupBy = null;
      }
      if(refresh) this.__refreshTable();
    },
    
    onToolbarAction: function(actionName) {
      this.config.actions.toolbar[actionName].onClick(this);
    },

    onBeanAction: function(actionName, beanState) {
      this.config.actions.bean[actionName].onClick(this, beanState);
    },

    getAncestorOfType: function(type) {
      return UIUtil.getAncestorOfType(this, type) ;
    },

    filter: function(field, exp, refresh) { 
      this.config.filter.field      = field;
      this.config.filter.expression = exp;
      this.__filter(field, exp);
      if(refresh) this.__refreshTable();
    },

    refreshWS: function() {
      this.__createViewModel();
      this.uiTableWS.render();
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

    __createAggregationModel: function() {
      var getValue = function(beanState, field) {
        return util.reflect.getFieldValue(beanState.bean, field); 
      };
      var root = new bucket.Bucket(null, "All");
      root.setObjects(this.filterBeanStates);
      var aggs = [];
      for(var fieldName in this.config.table.groupBy.fields) {
        var fieldValueAggregation = new bucket.aggregation.FieldValueAggregation(fieldName);
        fieldValueAggregation.getValue = getValue;
        aggs.push(fieldValueAggregation);
      }
      root.aggregate(aggs);
      return root;
    },


    __createViewModel: function() {
      if(!this.viewModel) this.viewModel = {} ;
      if(this.config.table.view == 'groupby') {
        if(!this.viewModel.groupBy) {
          this.viewModel.groupBy = this.__createAggregationModel();
        }
      } else {
        if(!this.viewModel.table) {
          this.viewModel.table = new PageList(this.config.table.page.size, this.filterBeanStates) ;
        }
      }
    },

    __refreshTable: function() {
      this.__createViewModel();
      this.uiTableWS.onRefresh();
    }

  });

  return UITable;
});
