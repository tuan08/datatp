define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIBorderLayout',
], function($, _, Backbone, UIBorderLayout) {
  var UIMenusTmpl = `
    <div class="ui-navigation">
      <div class="box-border-bottom box-layout-left-right" style="padding: 10px 5px 0px 5px">
        <h6>Navigation Menu</h6>
        <a class="ui-icon ui-icon-action ui-icon-caret-2-e-w onToggleControl"/>
      </div>

      <div class="ui-navigation-ctrl-menu" style="padding: 0px 5px">
        <%for(var key in state.nav.menus) {%>
        <%  var menu = state.nav.menus[key]; %>
          <div class="ui-card">
            <h6 class="box-bottom-border">
              <a class="ui-action"> - </a>
              <%=menu.label%>
            </h6>
            <div class="content">
              <%for(var i = 0; i < menu.items.length; i++) {%>
              <%  var item = menu.items[i]; %>
                  <div><a class="ui-action onSelectMenuItem" menu="<%=menu.name%>" itemIdx="<%=i%>"><%=item.label%></a></div>
              <%}%>
            </div>
          </div>
        <%}%>
      </div>
    </div>
  `;

  var Menu = function(menu) {
    this.menu = menu;

    this.addItem = function(label, config, onClick) {
      var item = { label: label, config: config, onClick: onClick };
      this.menu.items.push(item);
    };
  };

  var UIMenus = Backbone.View.extend({
    initialize: function(options) {
      this.state = {
        nav: { 
          width: 200, collapse: false, menus: {},
        },
      };
      if(this.onInit) this.onInit(options);
    },

    addMenu: function(name, label, config) {
      var menu = { name: name, label: label, config: config, items: [] };
      this.state.nav.menus[name] = menu;
      return new Menu(menu);
    },

    _template: _.template(UIMenusTmpl),

    render: function() {
      var params = { state:  this.state } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectMenuItem': 'onSelectMenuItem'
    },

    onSelectMenuItem: function(evt) {
      var menuName = $(evt.target).attr("menu") ;
      var itemIdx = $(evt.target).attr("itemIdx") ;
      var menu = this.state.nav.menus[menuName];
      var menuItem = menu.items[itemIdx];
      menuItem.onClick(this.uiParent, menu, menuItem);
    }
  });

  var UINavigation = UIBorderLayout.extend({
    _onInit: function(options) {
      this.uiMenus = new UIMenus();
      this.uiMenus.config = this.config;
      var westConfig = { width: "250px"};
      this.setUI('west', this.uiMenus, westConfig);
    },

    addMenu: function(name, label, collapse) { return this.uiMenus.addMenu(name, label, collapse); },

    setWorkspace: function(uiComponent) {
      var centerConfig = { };
      this.setUI('center', uiComponent, centerConfig, true);
    }

  });
  
  return UINavigation ;
});
