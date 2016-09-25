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
        require(['plugins/uidemo/' + item.label], function(UIDemoComponent) { 
          thisNav.setWorkspace(UIDemoComponent);
        }) ;
      };
      var menu1 = this.addMenu("core", "Core Demo", false/*collapse*/);
      menu1.addItem("UITableDemo", onClick);
      menu1.addItem("UITableTreeDemo", onClick);
      menu1.addItem("UITabbedPaneDemo", onClick);
      menu1.addItem("UIBeanDemo", onClick);
      menu1.addItem("UIComplexBeanDemo", onClick);
      menu1.addItem("UIUploadDemo", onClick);
      menu1.addItem("UINavigationDemo", onClick);
      menu1.addItem("UIBorderLayoutDemo", onClick);

      var menu2 = this.addMenu("nvchart", "NV Chart Demo", false/*collapse*/);
      menu2.addItem("UINVBarChartDemo", onClick);
      menu2.addItem("UINVMultiChartDemo", onClick);
      menu2.addItem("UINVLinePlusBarChartDemo", onClick);

      var thisNav = this;
      require(['plugins/uidemo/UIBorderLayoutDemo'], function(UIDemoComponent) { 
        thisNav.setWorkspace(UIDemoComponent);
      }) ;
    }
  });

  return new UIBody() ;
});
