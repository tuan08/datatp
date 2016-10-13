define([
  'ui/table/UITable',
  'ui/UICollapsible',
  'ui/UIBreadcumbs',
  'ui/UIContent',
  'ui/UIProperties',
  'plugins/elasticsearch/Rest'
], function(UITable, UICollapsible, UIBreadcumbs, UIContent, UIProperties, Rest) {
  
  var UIClusterHealth = UIProperties.extend({
    label: "Cluster Health",
     
    config: {
      width: "600px",
      label: { width: "250px" }
    },

    onInit: function(options) {
      var cStatus = Rest.cluster.getStatus();
      this.setBean(cStatus);
    },
  });

  var NodeModel = {
    label: 'Cluster Node Model',
    fields: {
      "name": { label: "Name", datatype: "string"},
      "transport_address": { label: "Transport Address", datatype: "string"},
      "http_address": { label: "Http Address", datatype: "string"},
    }
  };

  var UIClusterNodes = UITable.extend({
    label: "Cluster Nodes", 

    config: {
      control: { header: "Cluster Nodes"},
      table: { header: "Cluster Nodes"},

      actions: {
        toolbar: {
          refresh: {
            label: "Refresh",
            onClick: function(uiTable) { }
          }
        },

        bean: {
          detail: {
            label: "Detail",
            onClick: function(uiTable, beanState) {
              var bean = beanState.bean;
              var uiClusterInfo = uiTable.getAncestorOfType('UIClusterInfo');
              var uiContent = new UIContent({ content: JSON.stringify(bean, null, 2), highlightSyntax: "json" });
              uiContent.label = "Cluster Node " + bean.http_address;
              uiClusterInfo.push(uiContent);
            }
          }
        }
      }
    },
    
    onInit: function(options) {
      var result = Rest.cluster.getNodes();
      var nodes = [];
      for(var key in result.nodes) {
        var node = result.nodes[key];
        node.id = key;
        nodes.push(node);
      }
      this.set(NodeModel, nodes);
    }
  }) ;

  var UIClusterInfoGeneric = UICollapsible.extend({
    label: "Cluster Info Generic", 
    config: {
      actions: [ ]
    },
    
    onInit: function(options) {
      this.add(new UIClusterHealth()) ;
      this.add(new UIClusterNodes()) ;
    }
  }) ;

  var UIClusterInfo = UIBreadcumbs.extend({
    type:  "UIClusterInfo",
    label: 'Cluster Info',

    onInit: function(result) {
      this.clear();
      this.uiClusterInfoGeneric = new UIClusterInfoGeneric();
      this.push(this.uiClusterInfoGeneric);
    }
  });
  
  return UIClusterInfo ;
});
