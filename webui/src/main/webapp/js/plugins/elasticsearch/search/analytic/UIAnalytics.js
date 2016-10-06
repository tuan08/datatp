define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UIBorderLayout',
  'plugins/elasticsearch/search/analytic/AnalyticContext',
  'plugins/elasticsearch/search/analytic/UIAnalyticControl',
  'plugins/elasticsearch/search/analytic/UIAnalyticWS'
], function($, _, Backbone, UIContent, UIBorderLayout, AnalyticContext, UIAnalyticControl, UIAnalyticWS) {
  var UIAnalytics = UIBorderLayout.extend({
    label: 'Search Hits',

    onInit: function(options) {
      var esQueryContext = options.esQueryContext;
      this.analyticContext = new AnalyticContext();
      this.analyticContext.setESQueryContext(esQueryContext);
      
      this.uiAnalyticControl = new UIAnalyticControl({analyticContext: this.analyticContext});
      this.uiAnalyticWS      = new UIAnalyticWS({analyticContext: this.analyticContext});

      var westConfig = { width: "275px"};
      this.setUI('west', this.uiAnalyticControl, westConfig);

      var centerConfig = {};
      this.setUI('center', this.uiAnalyticWS, centerConfig);

      this.onSearch(esQueryContext);
    },

    onChangeChartModel: function(esQueryCtx) {
      this.uiAnalyticWS.onChangeChartModel();
      this.render();
    },
    
    onSearch: function(esQueryCtx) {
      this.analyticContext.setESQueryContext(esQueryCtx) ;
      this.uiAnalyticWS.onSearch(this.analyticContext);
    }
  });

  return UIAnalytics ;
});
