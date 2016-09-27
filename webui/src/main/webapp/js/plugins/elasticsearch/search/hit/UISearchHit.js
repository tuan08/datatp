define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UIBorderLayout',
  'plugins/elasticsearch/search/hit/UISearchHitControl',
  'plugins/elasticsearch/search/hit/UISearchHitResult',
], function($, _, Backbone, UIContent, UIBorderLayout, UISearchHitControl, UISearchHitResult) {
  var UISearchHit = UIBorderLayout.extend({
    label: 'Search Hits',

    onInit: function(options) {
      this.uiSearchHitControl = new UISearchHitControl({ uiSearchHit: this });
      this.uiSearchHitResult = new UISearchHitResult({ uiSearchHit: this});

      var westConfig = { width: "250px"};
      this.set('west', this.uiSearchHitControl, westConfig);

      var centerConfig = {};
      this.set('center', this.uiSearchHitResult, centerConfig);

      this.onSearch(options.esQueryContext);
    },

    onSearch: function(esQueryCtx) {
      var result = esQueryCtx.getQueryResult();
      this.uiSearchHitControl.onResult(result);
      this.uiSearchHitResult.onResult(result);
    }
  });

  return UISearchHit ;
});
