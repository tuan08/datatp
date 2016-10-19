define([
  'jquery', 
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/aggregations/model',
  'text!plugins/elasticsearch/search/aggregations/UITableView.jtpl',
], function($, _, Backbone, model, Template) {

  var UITableView = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(Template),

    render: function() {
      var params = { 
        query: this.dslQuery,
        treeModel: this.treeModel,
        result: this.result
      };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onSetPageSize50': 'onSetPageSize50',
    },

    withContext: function(analyticCtx) {
      this.aggContext = analyticCtx;
      return this;
    },

    onUpdateResult: function(aggContext) {
      this.treeModel = aggContext.getAggregations().buildTreeModel(aggContext.getResult());
    }
  });

  return UITableView ;
});
