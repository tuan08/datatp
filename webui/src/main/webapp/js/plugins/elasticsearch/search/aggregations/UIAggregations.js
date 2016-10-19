define([
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  'ui/UIContent',
  'plugins/elasticsearch/search/aggregations/UICtrlPlugin',
  'plugins/elasticsearch/search/aggregations/UITableView',
  'plugins/elasticsearch/search/aggregations/UIChartView',
  'plugins/elasticsearch/search/aggregations/AggContext',
  'plugins/elasticsearch/search/aggregations/model',
], function(UIBorderLayout, UITabbedPane, UIContent, UICtrlPlugin, UITableView, UIChartView, AggContext, model) {

  var UIAggregationTabbedPane = UITabbedPane.extend({
    label: 'Aggregations Tabs',

    config: { style: "ui-tabs" },

    withContext: function(aggContext) {
      this.uiAggChartView = new UIChartView();
      this.addTab("chart", "Chart", this.uiAggChartView, false, true);

      this.uiAggTableView = new UITableView().withContext(aggContext);
      this.addTab("aggregations", "Aggregations", this.uiAggTableView, false, false);

      this.uiQuery = new UIContent({ content: "", highlightSyntax: "json" });
      this.addTab("query", "Query", this.uiQuery, false, false);

      return this;
    },

    onUpdateResult: function(aggContext) {
      this.uiAggTableView.onUpdateResult(aggContext);
      this.uiAggChartView.onUpdateResult(aggContext);

      var json = JSON.stringify(aggContext.getQuery(), null, 2);
      this.uiQuery.setContent(json);
    },

    getChartView: function() { return this.uiAggChartView; }
  });

  var UIAggregations = UIBorderLayout.extend({
    type: "UIAggregations",

    getChartView: function() { return this.uiTabbedPane.getChartView(); },
    
    onInit: function(options) {
      var esQueryContext   = options.esQueryContext;
      var searchHitModel = esQueryContext.getSearchHitModel();
      this.aggContext = new AggContext();
      this.aggContext.setESQueryContext(esQueryContext);
      var aggs = new model.Aggregations(searchHitModel);
      aggs.setAggregation(new model.agg.DateHistogram('_source.timestamp', searchHitModel, '1m', null/*format*/));
      this.aggContext.setAggregations(aggs);

      this.uiCtrlPlugin = new UICtrlPlugin().withContext(this.aggContext);
      var westConfig = { width: "300px"};
      this.setUI('west',this. uiCtrlPlugin, westConfig);

      var centerConfig = {};
      this.uiTabbedPane = new UIAggregationTabbedPane().withContext(this.aggContext);
      this.setUI('center', this.uiTabbedPane, centerConfig);
    },

    onSearch: function(esQueryCtx) {
      var searchHitModel = esQueryCtx.getSearchHitModel();
      this.aggContext.setESQueryContext(esQueryCtx);
      this.aggContext.updateResult();
      this.uiTabbedPane.onUpdateResult(this.aggContext);
    },

    updateAggResult: function() {
      this.aggContext.updateResult();
      this.uiTabbedPane.onUpdateResult(this.aggContext);
      this.render();
    },

    changeAggField: function(aggId, fieldName) {
      var aggs = this.aggContext.getAggregations();
    },

    addSubAgg: function(type) {
      var searchHitModel = this.aggContext.getESQueryContext().getSearchHitModel();
      var aggs = this.aggContext.getAggregations();
      if(type == "DateHistogram") {
      } else if(type == "TopTerms") {
        aggs.addSubAggregation(new model.agg.TopTerms("", searchHitModel, 10));
      } else {
      }
    },

    removeSubAgg: function(aggId) {
      this.aggContext.getAggregations().rmSubAggregation(aggId);
    }

  });

  return UIAggregations ;
});
