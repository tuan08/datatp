define([
  'jquery',
], function($) {

  var AnalyticContext = function() {
    this.esQueryContext = null;
    this.chartModel     = null;

    this.getChartModel = function() { return this.chartModel; }
    this.setChartModel = function(model) { this.chartModel = model; }

    this.getESQueryContext = function() { return this.esQueryContext; }
    this.setESQueryContext = function(esQueryCtx) { this.esQueryContext = esQueryCtx; };
  };

  return AnalyticContext ;
});
