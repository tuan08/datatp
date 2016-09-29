define([
  'jquery', 
  'underscore', 
  'backbone',
], function($, _, Backbone) {
  var TEMPLATE = `
    <div style="padding: 2px">
      <%var style = config.style ? config.style : "ui-tabs"; %>
      <ul class="<%=style%>">
        <%for(var i = 0; i < tabs.length; i++) { %>
        <%  var tab  = tabs[i]; %>
        <%  var active = tab.name == state.tabConfig.name ? "active" : ""; %>
            <li tab="<%=tab.name%>">
              <a class="onSelectTab <%=active%>"><%=tab.label%></a>
              <%if(active && tab.closable) {%>
                  <span class="ui-action onCloseTab">x</span>
              <%}%>
            </li>
        <%}%>
      </ul>
      
      <div class="ui-tab-content"> this is a test </div>
    </div>
  `;
  /**@type ui.UITabbedPane */
  var UITabbedPane = Backbone.View.extend({
    initialize: function(options) {
      if(!this.config) {
        this.config = { style: "ui-tabs", tabs: [ ] };
      }
      this.tabs = [];
      for(var i = 0; i < this.config.tabs.length; i++) {
        this.tabs[i] = this.config.tabs[i];
      }
      if(this.onInit) this.onInit(options) ;
    },

    setSelectedTabUIComponent: function(name, uicomponent) {
      var tabConfig = this._getTabConfig(name) ;
      this.state = { tabConfig: tabConfig, uicomponent: uicomponent }
    },

    setSelectedTab: function(name) {
      var tabConfig = this._getTabConfig(name) ;
      if(tabConfig.onSelect) {
        tabConfig.onSelect(this, tabConfig) ;
      } else {
        this.setSelectedTabUIComponent(tabConfig.name, tabConfig.uicomponent) ;
      }
    },

    addTab: function(name, label, uiComponent, closable, active) {
      uiComponent.uiParent = this;
      var tabConfig = {
        name: name, label: label, uicomponent: uiComponent,  closable: closable
      };
      this.tabs.push(tabConfig);
      if(active) {
        this.setSelectedTabUIComponent(tabConfig.name, tabConfig.uicomponent) ;
      }
    },
    
    _template: _.template(TEMPLATE),
    
    render: function() {
      if(this.state == null && this.tabs.length > 0) {
        var tabConfig = this.tabs[0] ;
        this.setSelectedTab(tabConfig.name);
      }
      var params = { tabs: this.tabs, state: this.state, config: this.config } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
      
      var uiTabContent = this.$('.ui-tab-content') ;
      uiTabContent.unbind() ;
      this.state.uicomponent.setElement(uiTabContent).render();
    },
    
    events: {
      'click .onSelectTab': 'onSelectTab',
      'click .onCloseTab': 'onCloseTab'
    },
    
    onSelectTab: function(evt) {
      var tabName = $(evt.target).closest("li").attr('tab') ;
      this.setSelectedTab(tabName);
      this.render() ;
    },

    onCloseTab: function(evt) {
      var tabName = $(evt.target).closest("li").attr('tab') ;
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
