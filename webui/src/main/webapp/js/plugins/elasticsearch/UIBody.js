define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/ESQueryContext',
  'plugins/elasticsearch/search/hit/UISearchHit',
  'plugins/elasticsearch/search/analytic/UIAnalytics',
  'plugins/elasticsearch/admin/UIAdmin',
  'text!plugins/elasticsearch/UIBody.jtpl',
  'css!plugins/elasticsearch/stylesheet.css'
], function($, _, Backbone, ESQueryContext, UISearchHit, UIAnalytics, UIAdmin, Template) {

  var UISearch = Backbone.View.extend({
    type: 'UISearch',
    

    initialize: function () {
      this.queryString = "";
      this.esQueryCtx = new ESQueryContext("http://localhost:9200", ["xdoc"], { "match_all": {} });

      this.state = {
        views: {
          Search:    {  uiComponent: new UISearchHit({esQueryContext: this.esQueryCtx}) },
          Analytics: { uiComponent: new UIAnalytics({esQueryContext: this.esQueryCtx}) },
          Admin:     { uiComponent: new UIAdmin() }
        },
        selectView: 'Search',
        visibleSearchInput: true
      };
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { state: this.state, queryString: this.queryString };
      $(this.el).html(this._template(params));
      var view = this.state.views[this.state.selectView];
      view.uiComponent.setElement($(this.el).find('.UISearchView')).render();
    },

    events: {
      'click .onSelectSearch':    'onSelectSearch',
      'click .onSelectAnalytics': 'onSelectAnalytics',
      'click .onSelectAdmin':     'onSelectAdmin',

      'click .onSearch': 'onSearch',

      'keydown .onSearchInputEnter' : 'onSearchInputEnter'
    },

    onSelectSearch: function(evt) {
      this.state.selectView = 'Search';
      this.state.visibleSearchInput = true;
      this.render();
    },

    onSelectAnalytics: function(evt) {
      this.state.selectView = 'Analytics';
      this.state.visibleSearchInput = true;
      this.render();
    },

    onSelectAdmin: function(evt) {
      this.state.selectView = 'Admin';
      this.state.visibleSearchInput = false;
      this.render();
    },
    
    onSearch: function(evt) {
      var queryString = $(evt.target).parent().find("input").val();
      this._doSearch(queryString);
    },

    onSearchInputEnter: function(evt) {
      if(evt.keyCode == 13){
        var queryString = $(evt.target).parent().find("input").val();
        this._doSearch(queryString);
      }
    },

    _doSearch: function(queryString) {
      this.queryString = queryString;
      var query = {
        "query_string" : {
          "fields" : ["entity.content.content^3", "entity.content.content^2", "entity.content.content"],
          "query" : queryString
        }
      };
      if(queryString == "*") {
        query = { "match_all": {} };
      }

      this.esQueryCtx.setQuery(query);
      this.esQueryCtx.retrieve(0, 100);
      
      var view = this.state.views[this.state.selectView];
      view.uiComponent.onSearch(this.esQueryCtx);
      this.render();
    }
  });
  
  return new UISearch();
});
