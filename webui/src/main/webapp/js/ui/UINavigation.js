define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIBorderLayout',
  'text!ui/UINavigation.jtpl'
], function($, _, Backbone, UIBorderLayout, Template) {
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

    _template: _.template(Template),

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
      this.set('west', this.uiMenus, westConfig);
    },

    addMenu: function(name, label, collapse) { return this.uiMenus.addMenu(name, label, collapse); },

    setWorkspace: function(uiComponent) {
      var centerConfig = { };
      this.set('center', uiComponent, centerConfig, true);
    }

  });
  
  return UINavigation ;
});