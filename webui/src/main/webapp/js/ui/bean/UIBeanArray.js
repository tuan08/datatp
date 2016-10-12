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
    label: 'Modify',

    config: { header: "Modify" },
  });

  var UIBeanArray = UIBeanEditor.extend({
    tabLayout: {
      onToggleMode: function(thisUI, evt) {
        var uiBean = $(evt.target).closest(".ui-bean");
        thisUI.__toggle(uiBean) ;
      },

      renderFieldValue: function(uiFieldValue, beanInfo, beanState) {
        widget.view.field(uiFieldValue, beanInfo, beanState);
      }

    },

    tableLayout: {
      onToggleMode: function(uiBeanArray, evt) {
        var config = {
          title: "Modify Bean", 
          footerMessage: "Modify bean",
          width: "600px", height: "400px",
          actions: {
            save: {
              label: "Save",
              onClick: function(thisUI) {
              }
            }
          }
        }
        var uiBean    = $(evt.target).closest(".ui-bean");
        var beanInfo  = uiBeanArray.__getBeanInfo(uiBean);
        var beanState = uiBeanArray.__getBeanState(uiBean);
        var bean      = uiBeanArray.__getBean(uiBean);
        var uiBeanEdit = new UIBeanEdit().init(beanInfo, bean, beanState).setEditMode(true);
        UIDialog.activate(uiBeanEdit, config);
      },

      renderFieldValue: function(uiFieldValue, beanInfo, beanState) {
        widget.readonly.field(uiFieldValue, beanInfo, beanState);
      }
    },


    initialize: function (options) {
      var defaultConfig = {
        //header: "a title",
        //label: function(bean, idx) {},
      }
      //clone config to isolate the modification
      if(this.config) $.extend(defaultConfig, this.config);
      this.config = defaultConfig;

      $.extend(this.events, this.UIBeanEditorEvents);

      if(this.config.layout == 'table') {
        this.layout = this.tableLayout;
      } else {
        this.layout = this.tabLayout;
      }
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

    setBeans: function(beans, refresh) { 
      this.beans = beans;
      this.state = { editMode: false, select:   0, beanStates: [] };
      for(var i = 0; i < beans.length; i++) {
        this.state.beanStates[i] =  this.__createBeanState(this.beanInfo, beans[i]);
      }
      if(refresh) this.render();
    },

    add: function(bean, refresh) {
      this.beans.push(bean);
      var beanIdx = this.beans.length - 1;
      this.state.beanStates.push(this.__createBeanState(this.beanInfo, bean));
      this.state.select = beanIdx;
      if(refresh) this.render();
    },

    onViewMode: function() {
      var readonly = false;
      if(this.config.layout == 'table') readonly = true;
      var uiBeans = $(this.el).find('.ui-bean');
      var beanInfo = this.beanInfo;
      for(var i = 0; i < uiBeans.length; i++) {
        var uiBean = $(uiBeans[i]);
        var idx = parseInt(uiBean.attr("beanIdx"));
        var beanState = this.state.beanStates[idx];

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
        var idx = parseInt(uiBean.attr("beanIdx"));
        var beanState = this.state.beanStates[idx];

        var fieldBlks = uiBean.find('.field');
        fieldBlks.each(function(idx, ele) {
          var field = $(ele) ;
          var uiFieldValue = field.find('.field-value');
          widget.edit.field(uiFieldValue, beanInfo, beanState);
        });
      }

      this.editMode = true;
    },

    _tabTemplate: _.template(TabTmpl),

    _tableTemplate: _.template(TableTmpl),

    render: function() {
      var params = { 
        config: this.config, 
        beanInfo: this.beanInfo, 
        beans: this.beans,
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
      'click      .onAction' : 'onAction',
      'click      .onToggleMode' : 'onToggleMode',
      'click      .onRemove'     : 'onRemove',

      'click      .onSelect' : 'onSelect',
      'click      .add'      : 'onAdd',
    },

    onAction: function(evt) {
      var name = $(evt.target).attr('name');
      var action = this.config.actions[name];
      action.onClick(this);
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
      this.beans.splice(idx, 1);
      this.state.beanStates.splice(idx, 1);
      this.state.select = idx - 1;
      if(this.state.select < 0) this.state.select = 0;
      this.render();
    },

    onAdd: function(evt) {
      if(this.createDefaultBean) this.add(this.createDefaultBean(), true);
    },

    __getBean: function(fv) { 
      var idx = fv.closest(".ui-bean").attr("beanIdx");
      return this.beans[parseInt(idx)]; 
    },

    __getBeanState: function(triggerEle) { 
      var idx = triggerEle.closest(".ui-bean").attr("beanIdx");
      return this.state.beanStates[parseInt(idx)]; 
    },

    __getBeanInfo: function() { return this.beanInfo; },

  });

  return UIBeanArray ;
});
