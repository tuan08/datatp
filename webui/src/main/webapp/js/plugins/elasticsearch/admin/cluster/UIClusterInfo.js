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
  
  var UIClusterHealth = UIBean.extend({
    label: "Cluster Health",
    config: {
      beans: {
        bean: {
          label: 'Cluster Health',
          fields: [
            { field: "cluster_name", label: "Cluster Name"},
            { field: "status", label: "Status"},
            { field: "timedOut", label: "Time Out"},
            { field: "number_of_nodes", label: "Num Of Nodes"},
            { field: "number_of_data_nodes", label: "Num Of Data Nodes"},
            { field: "active_primary_shards", label: "Active Primary Shards"},
            { field: "active_shards", label: "Active Shards"},
            { field: "relocating_shards", label: "Recolating Shards"},
            { field: "initializing_shards", label: "Initializing Shards"},
            { field: "unassigned_shards", label: "Unassigned Shards"},
            { field: "delayed_unassigned_shards", label: "Delayed Unassigned Shards"},
            { field: "number_of_pending_tasks", label: "Number Of Pending Tasks"},
            { field: "number_of_in_flight_fetch", label: "Number Of In Flight Fetch"},
            { field: "task_max_waiting_in_queue_millis", label: "Task Max Waiting In Queue Millis"},
            { field: "active_shards_percent_as_number", label: "Active Shards Percent As Number"}
          ]
        }
      }
    },

    onInit: function(options) {
      var cStatus = Rest.cluster.getStatus();
      this.bind('bean', cStatus);
      this.setReadOnly(true);
    }
  });

  var UIClusterNodes = UITable.extend({
    label: "Cluster Nodes",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "refresh", label: "Refresh", 
              onClick: function(thisTable) { 
              } 
            }
          ]
        }
      },
      
      bean: {
        label: 'Cluster Nodes',
        fields: [
          { 
            field: "id",   label: "Id", toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              var uiClusterInfo = thisTable.getAncestorOfType('UIClusterInfo');
              var uiContent = new UIContent({ content: JSON.stringify(bean, null, 2), highlightSyntax: "json" });
              uiContent.label = "Cluster Node " + bean.http_address;
              uiClusterInfo.push(uiContent);
            }
          },
          { field: "name",   label: "Name", toggled: true, filterable: true },
          { field: "transport_address",   label: "Transport Addr", toggled: true, filterable: true },
          { field: "http_address",   label: "Http Addr", toggled: true, filterable: true },
        ]
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
      this.setBeans(nodes);
    }
  });

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
  
  return new UIClusterInfo() ;
});
