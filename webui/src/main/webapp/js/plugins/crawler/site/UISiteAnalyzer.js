define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBorderLayout',
  'plugins/crawler/site/UISiteAnalyzerWS',
  'plugins/crawler/Rest'
], function($, _, Backbone, UIBorderLayout, UISiteAnalyzerWS, Rest) {

  var UISiteAnalyzerControl = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(`
      <div style="padding: 5px">
        <div>
          <h6 class="box-border-bottom">Control</h6>
          <div>
            <a class="ui-action" style="display: block">Refresh</a>
            <a class="ui-action" style="display: block">Reanalyze</a>
          </div>
        </div>

        <div>
          <h6 class="box-border-bottom">Page Type</h6>
          <div>
            <a class="ui-action" style="display: block">uncategorized</a>
            <a class="ui-action" style="display: block">list</a>
            <a class="ui-action" style="display: block">detail</a>
            <a class="ui-action" style="display: block">ignore</a>
          </div>
        </div>

        <div>
          <h6 class="box-border-bottom">Fields</h6>
          <div>
            <a class="ui-action" style="display: block">uncategorized</a>
          </div>
        </div>
      </div>
    `),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onOpen': 'onOpen'
    },

    configure: function(siteConfig) {
      return this;
    }
  });

  var UISiteAnalyzer = UIBorderLayout.extend({
    label: 'Site Structure Analyzer',
    
    configure: function(siteConfig) {
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(siteConfig, 250, false);

      var westConfig = { width: "250px"};
      this.set('west', new UISiteAnalyzerControl().configure(siteConfig), westConfig);

      var centerConfig = {};
      this.set('center', new UISiteAnalyzerWS().set(urlSiteStructure), centerConfig);
      return this;
    }
  });

  return UISiteAnalyzer ;
});
