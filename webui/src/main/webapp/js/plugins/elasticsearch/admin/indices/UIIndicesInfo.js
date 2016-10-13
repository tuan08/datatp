define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/UIBean',
  'ui/table/UITable',
  'ui/UICollapsible',
  'ui/UIBreadcumbs',
  'ui/UIContent',
  'ui/UIProperties',
  'plugins/elasticsearch/Rest'
], function($, _, Backbone, UIBean, UITable, UICollapsible, UIBreadcumbs, UIContent, UIProperties, Rest) {
  
  var UIShardGenericInfo = UIProperties.extend({
    label: "Shard Info",
    config: {
      width: "600px"
    },

    onInit: function(options) {
      var stats = options.stats;
      this.setBean(stats._shards);
    }
  });

  var OpGenericInfoModel = {
    label: "Operation Info",
    fields: {
      "docs.count": { label: "Docs Count"},
      "docs.deleted": { label: "Docs Deleted"},
      "store.size_in_bytes": { label: "Store Size In Bytes"},
      "store.throttle_time_in_millis": { label: "Throttle Time In Millis"}
    }
  };

  var UIOpGenericInfo = UIBean.extend({
    label: "Operation Info",
    config: {
      header: "Operation Info",
      width:  "600px",
    },

    onInit: function(options) {
      var stats = options.stats;
      this.set(OpGenericInfoModel, stats._all.primaries);
    }
  });

  var IndexModel = {
    label: "Index",
    fields: {
      "name": {   label: "Name" },
      "info.docs.count": { label: "Docs Count" },
      "info.docs.deleted": { label: "Docs Deleted" },
    }
  };

  var UIIndicesList = UITable.extend({
    label: "Indices",

    config: {
      control: { header: "Indices"},
      table: { header: "Indices"},

      actions: {
        bean: {
          detail: {
            label: "Detail",
            onClick: function(uiTable, beanState) {
              var bean = beanState.bean;
              var uiIndicesInfo = uiTable.getAncestorOfType('UIIndicesInfo');
              var uiContent = new UIContent({ content: JSON.stringify(bean, null, 2), highlightSyntax: "json" });
              uiContent.label = "Cluster Node " + bean.http_address;
              uiIndicesInfo.push(uiContent);
            }
          }
        }
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
      this.set(IndexModel, beans);
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
  
  return UIIndicesInfo ;
});
