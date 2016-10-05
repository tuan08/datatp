define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  'util/PageList',
  "ui/UIDialog",
  "ui/bean/UIBean",
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  "ui/bean/UITableCtrlPlugin",
  "ui/bean/UITableWS"
], function($, _, Backbone, util, PageList, UIDialog, UIBean, UIBorderLayout, UITabbedPane, UITableCtrlPlugin, UITableWS) {
  var UITableCtrl = UITabbedPane.extend({
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
        control: { header: "Table Control" },
        table:   { header: "Table" },
        page:   { 
          size: 10, 
          options: [ 10, 25, 50, 100, 200, 500, 1000 ] ,
          more: [  50, 100, 200, 500 ] 
        }, 
        filter: { field: "", expression: "" },
        field:  { hidden: {} }
      };

      //clone config to isolate the modification
      if(this.config)  $.extend(tConfig, this.config);
      this.config = tConfig;


      var centerConfig = {};
      this.setUI('center', new UITableWS(), centerConfig);
    },


    set: function(beanInfo, beans) {
      this.beanInfo = $.extend({}, beanInfo);
      this.beanInfo.fieldNames = [];
      for(var name in beanInfo.fields) {
        this.beanInfo.fieldNames.push(name);
      }
      return this.setBeans(beans);
    },

    setBeans: function(beans) {
      this.beans    = beans;
      this.beanStates = [] ;
      for(var i = 0; i < beans.length; i++) {
        var bState = { bean: beans[i] };
        this.beanStates.push(bState);
      }
      this.beanStatePageList = new PageList(this.config.page.size, this.beanStates) ;
      return this;
    },
    
    addControlPluginUI: function(name, plugin) {
      var uiTableCtrl = this.getUI("west"); 
      if(uiTableCtrl == null) {
        var westConfig = { width: "250px"};
        uiTableCtrl = new UITableCtrl();
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

    toggleControl: function() { this.toggleUISplit('west'); },

    setFieldVisible: function(field, visible) { 
      this.config.field.hidden[field] = !visible;
      this.refreshUIPanel('center');
    },

    setSelectPage: function(page) { 
      this.beanStatePageList.getPage(page) ;
      this.refreshUIPanel('center');
    },

    filter: function(field, exp) { 
      if(exp == null || exp == '') return;
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

      var holder = [] ;
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
        if(match) holder.push(beanState) ;
      }
      this.beanStatePageList = new PageList(this.config.page.size, holder) ;
      this.refreshUIPanel('center');
    },

    updateDisplayRow: function(pageSize) { 
      this.config.page.size = pageSize ;
      this.beanStatePageList.setPageSize(pageSize) ;
      this.refreshUIPanel('center');
    },
  });

  return UITable;
});
