define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  'util/PageList',
  "ui/widget",
  "ui/nvd3",
  "ui/UIDialog",
  "ui/table/bucket",
  "text!ui/table/UITableTabPlugin.jtpl",
  "text!ui/table/UITableTabPluginTableView.jtpl",
  "text!ui/table/UITableTabPluginGroupByView.jtpl"
], function($, _, Backbone, util, PageList, widget, nvd3, UIDialog, bucket, UITableTabPluginTmpl, TableView, GroupByView) {

  var createGroupByModel = function(uiTable) {
    var getValue = function(beanState, field) {
      return util.reflect.getFieldValue(beanState.bean, field); 
    };
    var root = new bucket.Bucket(null, "All");
    root.setObjects(uiTable.filterBeanStates);
    var aggs = [];
    for(var fieldName in uiTable.config.table.groupBy.fields) {
      var fieldValueAggregation = new bucket.aggregation.FieldValueAggregation(fieldName);
      fieldValueAggregation.getValue = getValue;
      aggs.push(fieldValueAggregation);
    }
    root.aggregate(aggs);
    return root;
  };

  var UITableTabPlugin = Backbone.View.extend({
    initialize: function(options) {
      this.view = {
        table: {
          _template: _.template(TableView),

          render: function(uiTable, blkView, params) {
            if(this.model == null) this.model = new PageList(uiTable.config.table.page.size, uiTable.filterBeanStates) ;
            this.model.setPageSize(uiTable.config.table.page.size);
            this.model.getPage(uiTable.config.table.page.select);
            params.tableModel = this.model;
            blkView.html(this._template(params));
          },

          firePropertyChange: function(uiTable, object, op, property, value) { 
           if(object == 'config' && property.startsWith("table.page")) this.model = null ; 
          }
        },

        groupby: {
          _template: _.template(GroupByView),
          render: function(uiTable, blkView, params) {
            if(this.model == null) this.model = createGroupByModel(uiTable) ;
            params.groupByModel = this.model;
            blkView.html(this._template(params));
          },

          firePropertyChange: function(uiTable, object, op, property, value) { 
           if(object == 'config' && property.startsWith("table.groupBy")) this.model = null ; 
          }
        },

        chart: {
          render: function(uiTable, blkView, params) {
            if(this.uiChart == null) {
              var type = uiTable.config.table.chart.type;
              if(type == "LinePlusBarChart") {
                this.uiChart = new nvd3.UILinePlusBarChart();
              } else {
                this.uiChart = new nvd3.UIBarChart();
              }
              this.uiChart.useDataFromUITable(uiTable);
            }
            this.uiChart.setElement(blkView).render();
          },

          firePropertyChange: function(uiTable, object, op, property, value) { 
            if(object != 'config.table.chart') return;
            if(op == 'set' && property == 'type') {
              uiTable.config.table.chart.type = value;
              if(this.uiChart.chartType !=  value) this.uiChart = null;             
              return;
            }
            if(this.uiChart != null) this.uiChart.firePropertyChange(object, op, property, value);
          }
        }
      };
    },

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
      tableView.unbind() ;
      tableView.empty() ;
      this.view[config.table.view].render(this.uiTable, tableView, params);
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
      var bucket = this.view.groupby.model.findBucketByPath(bucketPath);
      bucket.collapse = !bucket.collapse ;
      this.render();
    },

    onTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var row = $(evt.target).closest("tr").attr("row");
      row = parseInt(row);
      var beanState = this.view.table.model.getItemOnCurrentPage(row);
      this.uiTable.onBeanAction(actionName, beanState);
    },


    onTreeTableRowAction: function(evt) { 
      var actionName = $(evt.target).attr("action");
      var tr = $(evt.target).closest("tr"); 
      var row = parseInt(tr.attr("row"));
      var bucketPath = tr.attr("bucket");
      var bucket = this.view.groupby.model.findBucketByPath(bucketPath);
      var beanState = bucket.objects[row];
      this.uiTable.onBeanAction(actionName, beanState);
    },

    fireDataChange: function() {
      this.view.table.model = null;
      this.view.groupby.model = null;
      this.view.chart.uiChart = null;
    },

    firePropertyChange: function(object, op, property, value) { 
      for(var name in this.view) {
        this.view[name].firePropertyChange(this.uiTable, object, op, property, value);
      }
    },
  });

  return UITableTabPlugin;
});
