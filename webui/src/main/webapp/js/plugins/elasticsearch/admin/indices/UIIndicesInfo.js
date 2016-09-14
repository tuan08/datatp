define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'ui/UICollapsible',
  'ui/UIBreadcumbs',
  'ui/UIContent',
  'plugins/elasticsearch/Rest'
], function($, _, Backbone, UIBean, UITable, UICollapsible, UIBreadcumbs, UIContent, Rest) {
  
  var UIShardGenericInfo = UIBean.extend({
    label: "Shard Info",
    config: {
      beans: {
        bean: {
          label: 'Shard Info',
          fields: [
            { field: "total", label: "Total"},
            { field: "successful", label: "Successful"},
            { field: "failed", label: "Failed"},
          ]
        }
      }
    },

    onInit: function(options) {
      var stats = options.stats;
      this.bind('bean', stats._shards);
      this.setReadOnly(true);
    }
  });

  var UIOpGenericInfo = UIBean.extend({
    label: "Operation Info",
    config: {
      beans: {
        bean: {
          label: 'Operation Info',
          fields: [
            { field: "docs.count", label: "Docs Count"},
            { field: "docs.deleted", label: "Docs Deleted"},
            { field: "store.size_in_bytes", label: "Store Size In Bytes"},
            { field: "store.throttle_time_in_millis", label: "Throttle Time In Millis"},
          ]
        }
      }
    },

    onInit: function(options) {
      var stats = options.stats;
      this.bind('bean', stats._all.primaries);
      this.setReadOnly(true);
    }
  });

  var UIIndicesList = UITable.extend({
    label: "Indices",

    config: {
      toolbar: {
        dflt: {
          actions: [ ]
        }
      },
      
      bean: {
        label: 'Cluster Nodes',
        fields: [
          { 
            field: "name",   label: "Name", toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              var uiIndicesInfo = thisTable.getAncestorOfType('UIIndicesInfo');
              var uiContent = new UIContent({ content: JSON.stringify(bean, null, 2), highlightSyntax: "json" });
              uiContent.label = "Cluster Node " + bean.http_address;
              uiIndicesInfo.push(uiContent);
            }
          },
          { field: "info.docs.count",   label: "Docs Count", toggled: true, filterable: true },
          { field: "info.docs.deleted",   label: "Docs Deleted", toggled: true, filterable: true },
        ]
      }
    },

    onInit: function(options) {
      var stats   = options.stats;
      var indices = stats.indices;
      var beans   = [];
      for(var key in indices) {
        var index = indices[key];
        var bean = { name: key, info: index.total};
        beans.push(bean);
      }
      this.setBeans(beans);
    }
  });

  var UIIndicesGenericInfo = UICollapsible.extend({
    label: "Indices Generic Info", 
    config: {
      actions: [ ]
    },
    
    onInit: function() {
      var stats = Rest.indices.getStats();
      var options = { stats: stats } ;
      this.add(new UIShardGenericInfo(options)) ;
      this.add(new UIOpGenericInfo(options)) ;
      this.add(new UIIndicesList(options)) ;
    }
  }) ;

  var UIIndicesInfo = UIBreadcumbs.extend({
    type:  "UIIndicesInfo",
    label: 'Indices Info',

    onInit: function(result) {
      this.clear();
      this.uiIndicesGenericInfo = new UIIndicesGenericInfo();
      this.push(this.uiIndicesGenericInfo);
    }
  });
  
  return new UIIndicesInfo() ;
});
