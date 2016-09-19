define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/hit/UISearchHitControl',
  'plugins/elasticsearch/search/hit/UISearchHitResult',
], function($, _, Backbone, UISearchHitControl, UISearchHitResult) {

  var UISearchHit = Backbone.View.extend({
    label: 'Search Hit',
    
    initialize: function () {
      this.uiSearchHitControl = new UISearchHitControl({ uiSearchHit: this });
      this.uiSearchHitResult = new UISearchHitResult({ uiSearchHit: this});
    },
   
    _template: _.template(`
      <div style="padding: 10px 5px">
        <div style="width: 200px; float: left" class="UISearchHitControl"></div>
        <div style="margin-left: 220px" class="UISearchHitResult"></div>
        <div class="clearfix"><span/></div>
      </div>
    `),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
      this.uiSearchHitControl.setElement($(this.el).find('.UISearchHitControl')).render();
      this.uiSearchHitResult.setElement($(this.el).find('.UISearchHitResult')).render();
    },

    events: {
      "click     .onToggleField": "onToggleField",
    },

    onResult: function(queryResult) {
      this.uiSearchHitControl.onResult(queryResult);
      this.uiSearchHitResult.onResult(queryResult);
    }
  });
  
  return UISearchHit ;
});
