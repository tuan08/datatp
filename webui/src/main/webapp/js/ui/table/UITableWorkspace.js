define([
  "ui/table/UITableTabPlugin"
], function(UITableTabPlugin) {

  var UITableWorkspace = Backbone.View.extend({

    initialize: function(options) {
      this.plugins = {
        tabs: { }
      };

      this.uistate = {
        plugin: { 
          active: "default",
          tabState: function(name) { return this.active == name ? "ui-disabled" : ""; },
          isClosable: function(plugin) { return plugin.closable && this.active == plugin.name ; }
        }
      };

      this.uiTable = options.uiTable;
      this.addTabPlugin("default", "Default", new UITableTabPlugin(), false, true);
    },

    addTabPlugin: function(name, label, uiComp, closable, active) {
      uiComp.uiTable = this.uiTable;
      this.plugins.tabs[name] = { name: name, label: label, uiComponent: uiComp, closable: closable };
      if(active) this.uistate.plugin.active = name;
    },

    _template: _.template(`
      <ul class="ui-toolbar-16px">
        <li><a class="ui-icon ui-icon-action ui-icon-gear onToggleControl"/></li>
        <li><span class="ui-icon ui-icon-grip-dotted-vertical"/></li>
        <%for(var name in plugins.tabs) { %>
          <%var plugin = plugins.tabs[name]; %>
          <li>
            <a class="ui-action <%=uistate.plugin.tabState(name)%> onSelectPluginTab" plugin="<%=name%>"><%=plugin.label%></a>
            <%if(uistate.plugin.isClosable(plugin)) { %>
              <a class="ui-action box-display-ib onRemovePluginTab" plugin="<%=name%>">x</a>
            <%}%>
          </li>
          <li><span class="ui-icon ui-icon-grip-dotted-vertical"/></li>
        <%}%>
      </ul>
      <div class="ui-table-ws-plugin"></div>
    `),

    render: function() {
      var params = {
        uistate: this.uistate,
        plugins: this.plugins
      };
      $(this.el).html(this._template(params));
      var uiPlugin = this.$('.ui-table-ws-plugin') ;
      uiPlugin.unbind() ;
      var activePlugin = this.plugins.tabs[this.uistate.plugin.active];
      activePlugin.uiComponent.setElement(uiPlugin).render();
    },

    onRefresh: function() {
      var activePlugin = this.plugins.tabs[this.uistate.plugin.active];
      if(activePlugin.uiComponent.onRefresh) {
        activePlugin.uiComponent.onRefresh();
      }
    },

    events: {
      'click    .onToggleControl':  'onToggleControl',
      'click    .onSelectPluginTab':  'onSelectPluginTab',
      'click    .onRemovePluginTab':  'onRemovePluginTab',
    },

    onToggleControl: function(evt) { this.uiTable.toggleControl(); },

    onSelectPluginTab: function(evt) { 
      var plugin = $(evt.target).attr("plugin");
      this.uistate.plugin.active = plugin;
      this.render();
    },

    onRemovePluginTab: function(evt) { 
      var plugin = $(evt.target).attr("plugin");
      delete this.plugins.tabs[plugin];
      this.uistate.plugin.active = Object.keys(this.plugins.tabs)[0];
      this.render();
    },

    fireDataChange: function() {
      for(var name in this.plugins.tabs) {
        var plugin = this.plugins.tabs[name];
        if(plugin.uiComponent.fireDataChange) {
          plugin.uiComponent.fireDataChange();
        }
      }
    },

    firePropertyChange: function(object, op, property, value) { 
      for(var name in this.plugins.tabs) {
        var plugin = this.plugins.tabs[name];
        if(plugin.uiComponent.firePropertyChange) {
          plugin.uiComponent.firePropertyChange(object, op,  property, value); 
        }
      }
    },

  });

  return UITableWorkspace;
});
