define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UINavigation'
], function($, _, Backbone, UIContent, UINavigation) {
  var UIAdmin = UINavigation.extend({

    onInit: function(options) {
      var onClick = function(thisNav, menu, item) {
        require(['plugins/elasticsearch/admin/' + item.config.module], function(uiComp) { 
          thisNav.setWorkspace(uiComp);
        }) ;
      };

      var clusterMenu = this.addMenu("cluster", "Cluster", { collapse: false });
      clusterMenu.addItem("Info", { module: "cluster/UIClusterInfo" }, onClick);

      var indicesMenu = this.addMenu("indices", "Indices", { collapse: false });
      indicesMenu.addItem("Info", { module: "indices/UIIndicesInfo" }, onClick);

      var thisNav = this;
      require(['plugins/elasticsearch/admin/cluster/UIClusterInfo'], function(uiComp) { 
        thisNav.setWorkspace(uiComp);
      }) ;
    }
  });

  return UIAdmin ;
});
