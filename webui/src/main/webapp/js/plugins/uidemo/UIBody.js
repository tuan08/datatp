define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UINavigation'
], function($, _, Backbone, UIContent, UINavigation) {
  var UIBody = UINavigation.extend({
    onInit: function(options) {

      var onClick = function(thisNav, menu, item) {
        var module = item.label;
        if(item.config.module) {
          module = item.config.module;
        }
        require(['plugins/uidemo/' + module], function(UIDemoComponent) { 
          thisNav.setWorkspace(UIDemoComponent);
        }) ;
      };

      var beanMenu = this.addMenu("bean", "Bean Demo", { collapse: false });
      beanMenu.addItem("UIBeanDemo", { module: "bean/UIBeanDemo" }, onClick);

      var menu1 = this.addMenu("core", "Core Demo", { collapse: false });
      menu1.addItem("UITableDemo", {}, onClick);
      menu1.addItem("UITableTreeDemo", {},  onClick);
      menu1.addItem("UITabbedPaneDemo", {}, onClick);
      menu1.addItem("UIBeanDemo", {}, onClick);
      menu1.addItem("UIComplexBeanDemo", {}, onClick);
      menu1.addItem("UIUploadDemo", {}, onClick);
      menu1.addItem("UINavigationDemo", {}, onClick);
      menu1.addItem("UIBorderLayoutDemo", {}, onClick);
      menu1.addItem("UIPropertiesDemo", {}, onClick);
      menu1.addItem("UIDialogDemo", {}, onClick);

      var menu2 = this.addMenu("nvchart", "NV Chart Demo", { collapse: false });
      menu2.addItem("UINVBarChartDemo", { module: "chart/UINVBarChartDemo" }, onClick);
      menu2.addItem("UINVMultiChartDemo", {module: "chart/UINVMultiChartDemo" }, onClick);
      menu2.addItem("UINVLinePlusBarChartDemo", { module: "chart/UINVLinePlusBarChartDemo" }, onClick);

      var thisNav = this;
      require(['plugins/uidemo/bean/UIBeanDemo'], function(UIDemoComponent) { 
        thisNav.setWorkspace(UIDemoComponent);
      }) ;
    }
  });

  return new UIBody() ;
});
