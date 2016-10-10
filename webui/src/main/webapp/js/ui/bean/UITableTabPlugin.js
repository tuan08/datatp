define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  "ui/widget",
  "ui/UIDialog",
  "text!ui/bean/UITableTabPlugin.jtpl",
  "text!ui/bean/UITableTabPluginTableView.jtpl",
  "text!ui/bean/UITableTabPluginGroupByView.jtpl"
], function($, _, Backbone, util, widget, UIDialog, UITableTabPluginTmpl, TableView, GroupByView) {

  var UITableTabPlugin = Backbone.View.extend({
    _tableTemplate: _.template(TableView),

    _groupbyTemplate: _.template(GroupByView),

    _template: _.template(UITableTabPluginTmpl),

    render: function() {
      var config = this.uiTable.config;
      var params = { 
        config: config, 
        beanInfo: this.uiTable.beanInfo,
        util: util,
        widget: widget
      };
      $(this.el).html(this._template(params));
      this.onRefresh();
    },

    onRefresh: function() {
      var config = this.uiTable.config;
      var params = { 
        config: config, 
        beanInfo: this.uiTable.beanInfo,
        util: util,
        widget: widget
      };
      var tableView = $(this.el).find(".table-view");
      if(config.table.view == 'groupby') {
        params.groupByModel = this.uiTable.viewModel.groupBy;
        tableView.html(this._groupbyTemplate(params));
      } else {
        params.tableModel = this.uiTable.viewModel.table;
        tableView.html(this._tableTemplate(params));
      }
    },

    events: {
      'click    .onToolbarAction':  'onToolbarAction',

      'change select.onSelectTablePageSize': 'onSelectTablePageSize',
      'keyup  .onFilter': 'onFilter',
      'change .onFilter': 'onFilter',
      'blur   .onFilter': 'onFilter',

      'click    .onSelectPage' :    'onSelectPage',
      'click    .onToggleBucket' :  'onToggleBucket',
      'click    .onTableRowAction': 'onTableRowAction',
      'click    .onTreeTableRowAction': 'onTreeTableRowAction'
    },

    onToolbarAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      this.uiTable.onToolbarAction(actionName);
    },

    onSelectTablePageSize: function(evt) {
      var pageSize = $(evt.target, ".onSelectTablePageSize").find(":selected").attr("value") ;
      this.uiTable.setTablePageSize(pageSize, true);
    },

    onFilter: function(evt) {
      console.log("on filter...");
      var filterBlk = $(evt.target).closest(".filter");
      var field = filterBlk.find("select.onFilter").val();
      var exp   = filterBlk.find("input.onFilter").val();
      this.uiTable.filter(field, exp, true);
    },

    onSelectPage: function(evt) { 
      var page = widget.page.iterator.getSelectPage(evt);
      this.uiTable.setTableSelectPage(page, true);
    },

    onToggleBucket: function(evt) { 
      var bucketPath = $(evt.target).attr("bucket");
      var bucket = this.uiTable.viewModel.groupBy.findBucketByPath(bucketPath);
      bucket.collapse = !bucket.collapse ;
      this.render();
    },

    onTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var row = $(evt.target).closest("tr").attr("row");
      row = parseInt(row);
      var beanState = this.uiTable.viewModel.table.getItemOnCurrentPage(row);
      this.uiTable.onBeanAction(actionName, beanState);
    },


    onTreeTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var tr = $(evt.target).closest("tr"); 
      var row = parseInt(tr.attr("row"));
      var bucketPath = tr.attr("bucket");
      var bucket = this.uiTable.viewModel.groupBy.findBucketByPath(bucketPath);
      var beanState = bucket.objects[row];
      this.uiTable.onBeanAction(actionName, beanState);
    }
  });

  return UITableTabPlugin;
});
