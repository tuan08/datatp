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
      beanMenu.addItem("UIBean Demo", { module: "bean/UIBeanDemo" }, onClick);
      beanMenu.addItem("UITable Demo", { module: "bean/UITableDemo" }, onClick);
      beanMenu.addItem("UITable Bar Chart Demo", { module: "bean/UITableBarChartDemo" }, onClick);

      var menu1 = this.addMenu("core", "Core Demo", { collapse: false });
      menu1.addItem("UITabbedPaneDemo", {}, onClick);
      menu1.addItem("UIUploadDemo", {}, onClick);
      menu1.addItem("UINavigationDemo", {}, onClick);
      menu1.addItem("UIBorderLayoutDemo", {}, onClick);
      menu1.addItem("UIPropertiesDemo", {}, onClick);
      menu1.addItem("UIDialogDemo", {}, onClick);
      menu1.addItem("UITemplateLayoutDemo", {}, onClick);
      menu1.addItem("UIWidgetDemo", {}, onClick);

      var menu2 = this.addMenu("nvchart", "NV Chart Demo", { collapse: false });
      menu2.addItem("UINVBarChartDemo", { module: "chart/UINVBarChartDemo" }, onClick);
      menu2.addItem("UINVMultiChartDemo", {module: "chart/UINVMultiChartDemo" }, onClick);
      menu2.addItem("UINVLinePlusBarChartDemo", { module: "chart/UINVLinePlusBarChartDemo" }, onClick);

      var thisNav = this;
      require(['plugins/uidemo/UIWidgetDemo'], function(UIDemoComponent) { 
        thisNav.setWorkspace(UIDemoComponent);
      }) ;
    }
  });

  return new UIBody() ;
});
