define([
  'jquery', 'underscore', 'backbone',
  "ui/bean/widget",
  'util/util',
  "ui/UIDialog",
  "ui/bean/UIBeanEditor",
  "ui/bean/UIBean",
  "text!ui/bean/UITabArrayBean.jtpl",
  "text!ui/bean/UITableArrayBean.jtpl"
], function($, _, Backbone, widget, util, UIDialog, UIBeanEditor, UIBean,  TabTmpl, TableTmpl) {

  var UIBeanEdit = UIBean.extend({
    label: 'Modify Bean',

    config: {
      header: "Modify Bean",
    },
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
          title: "Modify Bean", footerMessage: "Modify bean",
          width: "600px", height: "400px",
          actions: {
            save: {
              label: "Save",
              onClick: function(thisUI) {
                console.log("call save.................") ;
              }
            }
          }
        }
        var uiBean    = $(evt.target).closest(".ui-bean");
        var beanInfo  = uiBeanArray.__getBeanInfo(uiBean);
        var beanState = uiBeanArray.__getBeanState(uiBean);
        var bean      = uiBeanArray.__getBean(uiBean);
        var uiBeanEdit = new UIBeanEdit();
        uiBeanEdit.init(beanInfo, bean, beanState);
        UIDialog.activate(uiBeanEdit, config);
      },

      renderFieldValue: function(uiFieldValue, beanInfo, beanState) {
        widget.readonly.field(uiFieldValue, beanInfo, beanState);
      }
    },


    initialize: function (options) {
      if(!this.config) this.config = { };
      if(this.onInit) this.onInit(options);
      $.extend(this.events, this.UIBeanEditorEvents);
      if(this.config.layout == 'table') {
        this.layout = this.tableLayout;
      } else {
        this.layout = this.tabLayout;
      }
    },


    set: function(bInfo, beans) { 
      this.beanInfo = bInfo; 
      this.beans = beans;
      this.state    = { editMode: false, select:   0, beanStates: [] };
      for(var i = 0; i < beans.length; i++) {
        this.state.beanStates[i] =  this.__createBeanState(bInfo, beans[i]);
      }
    },

    onViewMode: function() {
      var readonly = false;
      if(this.config.layout == 'table') readonly = true;
      var uiBeans = $(this.el).find('.ui-bean');
      var beanInfo = this.beanInfo;
      for(var i = 0; i < uiBeans.length; i++) {
        var uiBean = $(uiBeans[i]);
        var idx = parseInt(uiBean.attr("idx"));
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
        var idx = parseInt(uiBean.attr("idx"));
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

      //var actionsBlk = $(this.el).find(".actions");
      //widget.actions(actionsBlk, this.config.actions);
    },
    
    events: {
      //Handle by UIBean
      'click      .actions .onAction' : 'onAction',
      'click      .toggle-mode .onToggleMode' : 'onToggleMode',

      'click      .onSelectTab' : 'onSelectTab',
    },

    onAction: function(evt) {
      var name = $(evt.target).attr('name');
      var action = this.config.actions[name];
      action.onClick(this);
    },

    onToggleMode: function(evt) {
      this.layout.onToggleMode(this, evt);
    },

    onSelectTab: function(evt) {
      var uiTab = $(evt.target).closest("li");
      var tabIdx = parseInt(uiTab.attr("tab"));
      $(evt.target).closest(".ui-tabs").find("a.active").removeClass("active").addClass("onSelectTab"); 
      uiTab.find("a").addClass("active").removeClass("onSelectTab");
      
      var uiTabContents = $(this.el).find(".ui-tab-contents");
      uiTabContents.find("div[idx=" + this.state.select + "]").css("display", "none");
      uiTabContents.find("div[idx=" + tabIdx + "]").css("display", "block");
      this.state.select = tabIdx;
    },

    __getBean: function(fv) { 
      var idx = fv.closest(".ui-bean").attr("idx");
      return this.beans[parseInt(idx)]; 
    },

    __getBeanState: function(triggerEle) { 
      var idx = triggerEle.closest(".ui-bean").attr("idx");
      return this.state.beanStates[parseInt(idx)]; 
    },

    __getBeanInfo: function(fv) { return this.beanInfo; },

  });

  return UIBeanArray ;
});
