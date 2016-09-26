define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UINavigation'
], function($, _, Backbone, UIContent, UINavigation) {
  var UINavigationDemo = UINavigation.extend({
    label: 'Navigation Demo',

    config: {
      label: "Navigation Demo",
      nav: { width: 250 }
    },
    
    onInit: function(options) {
      var onClick = function(thisNav, menu, item) {
        var uiContent = new UIContent({ content: 'on click menu = ' + menu.name + ", item = " + item.label});
        thisNav.setWorkspace(uiContent);
      };
      var menu1 = this.addMenu("menu-1", "Menu 1", { collapse: false });
      menu1.addItem("Item 1", {}, onClick);
      menu1.addItem("Item 2", {}, onClick);

      var menu2 = this.addMenu("menu-2", "Menu 2", { collapse: false });
      menu2.addItem("Item 1", {}, onClick);
      menu2.addItem("Item 2", {}, onClick);
    }
  });

  return new UINavigationDemo() ;
});
