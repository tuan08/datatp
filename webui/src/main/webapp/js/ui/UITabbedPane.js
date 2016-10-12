define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil',
], function($, _, Backbone, UIUtil) {
  var TEMPLATE = `
    <div class="ui-tab-container">
      <%if(config.header) { %>
        <div class="box-layout-left-right box-border-bottom" style="margin-bottom: 5px">
          <h6><%=config.header.title%></h6>
        </div>
      <%}%>

      <%var style = config.style ? config.style : "ui-tabs"; %>
      <ul class="<%=style%>">
        <%for(var name in tabs) { %>
        <%  var tab  = tabs[name]; %>
        <%  var active = tab.name == state.tabConfig.name ? "active" : ""; %>
            <li class="<%=active%>" tab="<%=tab.name%>">
              <a class="onSelectTab"><%=tab.label%></a>
              <%if(active && tab.closable) {%>
                  <a class="remove onCloseTab">x</a>
              <%}%>
            </li>
        <%}%>
      </ul>
      
      <div class="ui-tab-content"></div>
    </div>
  `;
  /**@type ui.UITabbedPane */
  var UITabbedPane = Backbone.View.extend({
    initialize: function(options) {
      var defaultConfig = { 
        style: "ui-tabs", 
        tabs: [ ] 
      };
      if(this.config) $.extend(true, defaultConfig, this.config);
      this.config = defaultConfig;
      this.tabs = {};
      for(var i = 0; i < this.config.tabs.length; i++) {
        var name = this.config.tabs[i].name;
        this.tabs[name] = this.config.tabs[i];
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
      this.tabs[name] = tabConfig;
      if(active) {
        this.setSelectedTabUIComponent(tabConfig.name, tabConfig.uicomponent) ;
      }
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },
    
    _template: _.template(TEMPLATE),
    
    render: function() {
      if(this.state == null) {
        var tabConfig = this.tabs[Object.keys(this.tabs)[0]];
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
      delete this.tabs[tabName];
      this.state = null;
      this.render() ;
    },
    
    _getTabConfig: function(name) { return this.tabs[name]; }
  });
  
  return UITabbedPane ;
});
