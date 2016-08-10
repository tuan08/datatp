define([
  'jquery', 
  'underscore', 
  'backbone',
  'text!ui/UITabbedPane.jtpl',
  'css!ui/UITabbedPane.css'
], function($, _, Backbone, UITabbedPaneTmpl) {
  /**@type ui.UITabbedPane */
  var UITabbedPane = Backbone.View.extend({

    initialize: function(options) {
      this.tabs = [];
      for(var i = 0; i < this.config.tabs.length; i++) {
        this.tabs[i] = this.config.tabs[i];
      }
      this.onInit(options) ;
      _.bindAll(this, 'render', 'onSelectTab') ;
    },

    onInit: function(options) { },

    setSelectedTabUIComponent: function(name, uicomponent) {
      var tabConfig = this._getTabConfig(name) ;
      this.state = { tabConfig: tabConfig, uicomponent: uicomponent }
    },

    setSelectedTab: function(name) {
      var tabConfig = this._getTabConfig(name) ;
      tabConfig.onSelect(this, tabConfig) ;
    },

    addTab: function(name, label, uiComponent, closable) {
      var tabConfig = {
        name: name, label: label, closable: closable,
        onSelect: function(thisUI, tabConfig) {
          thisUI.setSelectedTabUIComponent(tabConfig.name, uiComponent) ;
        }
      };
      this.tabs.push(tabConfig);
      tabConfig.onSelect(this, tabConfig) ;
    },
    
    _template: _.template(UITabbedPaneTmpl),
    
    render: function() {
      if(this.state == null && this.tabs.length > 0) {
        var tabConfig = this.tabs[0] ;
        tabConfig.onSelect(this, tabConfig) ;
      }
      var params = { tabs: this.tabs, state: this.state } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
      
      this.$('.UITab').unbind() ;
      this.state.uicomponent.setElement(this.$('.UITab')).render();
    },
    
    events: {
      'click a.onSelectTab': 'onSelectTab',
      'click a.onCloseTab': 'onCloseTab'
    },
    
    onSelectTab: function(evt) {
      var tabName = $(evt.target).closest("a").attr('tab') ;
      var tabConfig = this._getTabConfig(tabName) ;
      tabConfig.onSelect(this, tabConfig) ;
      this.render() ;
    },

    onCloseTab: function(evt) {
      var tabName = $(evt.target).closest("a").attr('tab') ;
      console.log('on close tab ' + tabName);
      var tabIdx = -1;
      for(var i = 0; i < this.tabs.length; i++) {
        var tab = this.tabs[i] ;
        if(tabName == tab.name) {
          tabIdx = i;
          break;
        }
      }
      if(tabIdx > -1) {
        this.tabs.splice(tabIdx, 1);
        this.state = null;
      }
      this.render() ;
    },
    
    _getTabConfig: function(name) {
      for(var i = 0; i < this.tabs.length; i++) {
        var tab = this.tabs[i] ;
        if(name == tab.name) return tab ;
      }
      return null ;
    }
  });
  
  return UITabbedPane ;
});
