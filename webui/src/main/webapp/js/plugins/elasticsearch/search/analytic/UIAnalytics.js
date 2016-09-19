define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/analytic/AnalyticContext',
  'plugins/elasticsearch/search/analytic/UIAnalyticControl',
  'plugins/elasticsearch/search/analytic/UIAnalyticWS'
], function($, _, Backbone, AnalyticContext, UIAnalyticControl, UIAnalyticWS) {

  var UIAnalytics = Backbone.View.extend({
    type:  'UIAnalytics',
    label: 'Search Hit',
    
    initialize: function(options) {
      var esQueryContext = options.esQueryContext;
      this.analyticContext = new AnalyticContext();
      this.analyticContext.setESQueryContext(esQueryContext);
      
      this.uiAnalyticControl = new UIAnalyticControl({analyticContext: this.analyticContext});
      this.uiAnalyticControl.uiParent = this;
      
      this.uiAnalyticWS      = new UIAnalyticWS({analyticContext: this.analyticContext});
    },
   
    _template: _.template(`
      <div style='padding: 10px 5px'> 
        <div style='width: 250px; float: left' class='UIAnalyticControl'></div>
        <div style='margin-left: 255px' class='UIAnalyticWS'></div>
        <div class='clearfix'><span/></div>
      </div>
    `),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
      this.uiAnalyticControl.setElement($(this.el).find('.UIAnalyticControl')).render();
      this.uiAnalyticWS.setElement($(this.el).find('.UIAnalyticWS')).render();
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
