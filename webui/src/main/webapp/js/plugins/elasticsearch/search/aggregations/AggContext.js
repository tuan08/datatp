define([
  'jquery',
], function($) {

  var AggContext = function() {
    this.esQueryContext = null;
    this.aggregations   = null;

    this.query          = null;

    this.getAggregations = function() { return this.aggregations; }
    this.setAggregations = function(model) { this.aggregations = model; }

    this.getESQueryContext = function() { return this.esQueryContext; }
    this.setESQueryContext = function(esQueryCtx) { this.esQueryContext = esQueryCtx; };

    this.getQuery = function() { return this.query ; };

    this.getResult = function() { return this.result; }

    this.updateResult = function() {
      var esAggs  = this.aggregations.getESAggs();
      this.query  = { size:  0, query: this.esQueryContext.query, aggs : esAggs };
      this.result = this.esQueryContext.dslQuery(this.query);
    }
  };

  return AggContext ;
});
