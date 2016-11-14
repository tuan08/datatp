define([
  'jquery', 'underscore', 'backbone',
  "ui/bean/widget",
  'util/util',
  "ui/UIDialog",
  "ui/bean/UIBeanEditor",
  "ui/bean/UIBean",
  "text!ui/bean/UIBeanArrayTab.jtpl",
  "text!ui/bean/UIBeanArrayTable.jtpl"
], function($, _, Backbone, widget, util, UIDialog, UIBeanEditor, UIBean,  TabTmpl, TableTmpl) {

  var UIBeanEdit = UIBean.extend({
    config: { header: "Modify" },
  });

  var UIBeanArray = UIBeanEditor.extend({
    tabLayout: {
      onToggleMode: function(thisUI, evt) {
        var uiBean = $(evt.target).closest(".ui-bean");
        thisUI.__toggle(uiBean) ;
      },

      onAdd: function(thisUI, evt) { thisUI.add(thisUI.createDefaultBean(), true); },

      renderFieldValue: function(uiFieldValue, beanInfo, beanState) { widget.view.field(uiFieldValue, beanInfo, beanState); }
    },

    tableLayout: {
      onToggleMode: function(uiBeanArray, evt) {
        var config = {
          title: "Edit", 
          footerMessage: "Edit",
          width: "600px", height: "400px",
          actions: {
            save: {
              label: "Save",
              onClick: function(thisUI) {
                thisUI.onViewMode();
                UIDialog.close()
                thisUI.uiBeanArray.render();
              }
            }
          }
        }
        var uiBean    = $(evt.target).closest(".ui-bean");
        var beanInfo  = uiBeanArray.__getBeanInfo(uiBean);
        var beanState = uiBeanArray.__getBeanState(uiBean);
        var uiBeanEdit = new UIBeanEdit().init(beanInfo, beanState).setEditMode(true);
        uiBeanEdit.uiBeanArray = uiBeanArray;
        UIDialog.activate(uiBeanEdit, config);
      },

      onAdd: function(uiBeanArray, evt) {
        var config = {
          title: "Add", 
          footerMessage: "Add",
          width: "600px", height: "400px",
          actions: {
            save: {
              label: "Save",
              onClick: function(thisUI) {
                thisUI.onViewMode();
                UIDialog.close()
                var bean = thisUI.getBeanState().getBeanWithCommitChange();
                console.printJSON(bean);
                thisUI.uiBeanArray.add(bean, true);
                thisUI.uiBeanArray.render();
              }
            }
          }
        }
        var beanInfo  = uiBeanArray.__getBeanInfo();
        var bean      = uiBeanArray.createDefaultBean();
        var uiBeanEdit = new UIBeanEdit().set(beanInfo, bean).setEditMode(true);
        uiBeanEdit.uiBeanArray = uiBeanArray;
        UIDialog.activate(uiBeanEdit, config);
      },

      renderFieldValue: function(uiFieldValue, beanInfo, beanState) {
        widget.readonly.field(uiFieldValue, beanInfo, beanState);
      }
    },


    initialize: function (options) {
      var defaultConfig = {
        header: "Array",
        label: function(bean, idx) { return "Bean " + (idx + 1); } ,
      }
      //clone config to isolate the modification
      if(this.config) $.extend(defaultConfig, this.config);
      this.config = defaultConfig;

      $.extend(this.events, this.UIBeanEditorEvents);

      if(this.config.layout == 'table')  this.layout = this.tableLayout;
      else                               this.layout = this.tabLayout;

      if(this.onInit) this.onInit(options);
    },

    configure: function(newConfig) { 
      $.extend(this.config, newConfig); 
      return this;
    },

    set: function(bInfo, beans) { 
      this.beanInfo = bInfo; 
      this.setBeans(beans, false);
      return this;
    },

    addAction: function(name, label, onClick) {
      var actionConfig = { label: label, onClick: onClick };
      if(!this.config.actions) this.config.actions = {};
      this.config.actions[name] = actionConfig;
      return this;
    },

    setBeans: function(beans, refresh) { 
      this.beans = beans;
      this.state = { editMode: false, select:   0, beanStates: [] };
      for(var i = 0; i < beans.length; i++) {
        this.state.beanStates[i] =  this.__createBeanState(this.beanInfo, beans[i]);
      }
      if(refresh) this.render();
    },

    add: function(bean, refresh) {
      this.state.beanStates.push(this.__createBeanState(this.beanInfo, bean));
      var beanIdx = this.state.beanStates.length - 1;
      this.state.select = beanIdx;
      if(refresh) this.render();
    },

    commitChange: function() {
      this.beans.splice(0, this.beans.length);
      var bStates = this.state.beanStates;
      for(var i = 0; i < bStates.length; i++) {
        this.beans.push(bStates[i].getBeanWithCommitChange());
      }
    },

    onViewMode: function() {
      var uiBeans = $(this.el).find('.ui-bean');
      var beanInfo = this.beanInfo;
      for(var i = 0; i < uiBeans.length; i++) {
        var uiBean = $(uiBeans[i]);
        var idx = parseInt(uiBean.attr("beanIdx"));
        var beanState = this.state.beanStates[idx];
        if(beanState.editMode) this.__commitUIBeanChange(uiBean);
        var fieldBlks = uiBean.find('.field');
        for(var j = 0; j < fieldBlks.length; j++) {
          var field = $(fieldBlks[j]) ;
          var uiFieldValue = field.find('.field-value');
          this.layout.renderFieldValue(uiFieldValue, beanInfo, beanState);
        }
      }
      this.editMode = false;
    },

    onEditMode: function() {
      var uiBeans = $(this.el).find('.ui-bean');
      var beanInfo = this.beanInfo;
      for(var i = 0; i < uiBeans.length; i++) {
        var uiBean = $(uiBeans[i]);
        this.__setEditMode(uiBean);
      }

      this.editMode = true;
    },

    _tabTemplate: _.template(TabTmpl),

    _tableTemplate: _.template(TableTmpl),

    render: function() {
      var params = { 
        config: this.config, 
        beanInfo: this.beanInfo, 
        state: this.state
      };

      if(this.config.layout == 'table') {
        $(this.el).html(this._tableTemplate(params));
      } else {
        $(this.el).html(this._tabTemplate(params));
      }
      this.onViewMode();

      var uiToggleMode = $(this.el).find(".ui-beans").find(".toggle-mode");
      widget.toggle(uiToggleMode);
    },
    
    events: {
      //Handle by UIBean
      'click      .onBeanAction' : 'onBeanAction',
      'click      .onToggleMode' : 'onToggleMode',
      'click      .onRemove'     : 'onRemove',

      'click      .onSelect' : 'onSelect',
      'click      .add'      : 'onAdd',
    },

    onBeanAction: function(evt) {
      var name = $(evt.target).attr('name');
      var idx = $(evt.target).closest(".ui-bean").attr("beanIdx");
      idx = parseInt(idx); 
      var beanState = this.state.beanStates[idx];
      var action = this.config.actions[name];
      action.onClick(this, beanState);
    },

    onToggleMode: function(evt) {
      this.layout.onToggleMode(this, evt);
    },

    onSelect: function(evt) {
      evt.preventDefault();
      var uiTab = $(evt.target).closest("[beanIdx]");
      var tabIdx = parseInt(uiTab.attr("beanIdx"));

      var uiActiveTab  = $(evt.target).closest(".ui-tabs").find("li.active"); 
      uiActiveTab.removeClass("active");

      uiTab.addClass("active");

      var uiTabContents = $(evt.target).closest('.ui-beans').find(".ui-tab-contents");
      uiTabContents.children("[beanIdx=" + this.state.select + "]").css("display", "none");
      uiTabContents.children("[beanIdx=" + tabIdx + "]").css("display", "block");
      this.state.select = tabIdx;
    },

    onRemove: function(evt) {
      var idx = $(evt.target).closest("[beanIdx").attr("beanIdx");
      this.state.beanStates.splice(idx, 1);
      this.state.select = idx - 1;
      if(this.state.select < 0) this.state.select = 0;
      if(this.autoCommit) this.commitChange();
      this.render();
    },

    onAdd: function(evt) {
      evt.stopPropagation();
      if(this.createDefaultBean) this.layout.onAdd(this, evt);
      if(this.autoCommit) this.commitChange();
    },

    __getBeanState: function(triggerEle) { 
      var idx = triggerEle.closest(".ui-bean").attr("beanIdx");
      return this.state.beanStates[parseInt(idx)]; 
    },

    __getBeanInfo: function() { return this.beanInfo; },

  });

  return UIBeanArray ;
});
