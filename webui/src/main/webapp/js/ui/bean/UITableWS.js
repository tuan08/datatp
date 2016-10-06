define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  "ui/widget",
  "ui/UIDialog",
  "ui/bean/UIBean",
  "text!ui/bean/UITable.jtpl",
  "text!ui/bean/UITreeTable.jtpl"
], function($, _, Backbone, util, widget, UIDialog, UIBean, TableTmpl, TreeTableTmpl) {

  var UITableWS = Backbone.View.extend({

    _tableTemplate: _.template(TableTmpl),

    _treeTemplate: _.template(TreeTableTmpl),

    _template: _.template(`
    
    `),

    render: function() {
      var config = this.uiParent.config;
      var params = { 
        config: config, 
        beanInfo: this.uiParent.beanInfo,
        util: util,
        widget: widget
      };
      if(config.table.view == 'groupby') {
        params.groupByModel = this.uiParent.viewModel.groupBy;
        $(this.el).html(this._treeTemplate(params));
      } else {
        params.tableModel = this.uiParent.viewModel.table;
        $(this.el).html(this._tableTemplate(params));
      }
    },

    events: {
      'click    .onToggleControl':  'onToggleControl',
      'click    .onSelectPage' :    'onSelectPage',
      'click    .onToggleBucket' :  'onToggleBucket',
      'click    .onTableRowAction': 'onTableRowAction',
      'click    .onTreeTableRowAction': 'onTreeTableRowAction'
    },

    onToggleControl: function(evt) { this.uiParent.toggleControl(); },

    onSelectPage: function(evt) { 
      var page = widget.page.iterator.getSelectPage(evt);
      this.uiParent.setTableSelectPage(page, true);
    },

    onToggleBucket: function(evt) { 
      var bucketPath = $(evt.target).attr("bucket");
      var bucket = this.uiParent.viewModel.groupBy.findBucketByPath(bucketPath);
      bucket.collapse = !bucket.collapse ;
      this.render();
    },

    onTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var row = $(evt.target).closest("tr").attr("row");
      row = parseInt(row);
      var beanState = this.uiParent.viewModel.table.getItemOnCurrentPage(row);
      this.uiParent.onAction(actionName, beanState);
    },


    onTreeTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var tr = $(evt.target).closest("tr"); 
      var row = parseInt(tr.attr("row"));
      var bucketPath = tr.attr("bucket");
      var bucket = this.uiParent.viewModel.groupBy.findBucketByPath(bucketPath);
      var beanState = bucket.objects[row];
      this.uiParent.onAction(actionName, beanState);
    }
  });

  return UITableWS;
});
