define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/ESQueryContext',
  'plugins/elasticsearch/search/UISearchResult',
  'text!plugins/elasticsearch/search/UISearch.jtpl',
  'css!plugins/elasticsearch/search/UISearch.css'
], function($, _, Backbone, ESQueryContext, UISearchResult, Template) {

  var UISearch = Backbone.View.extend({
    type: 'UISearch',
    
    initialize: function () {
      this.queryString = "";
      this.esQueryCtx = new ESQueryContext("http://localhost:9200", ["xdoc"], { "match_all": {} });
      this.uiSearchResult = new UISearchResult({ esQueryContext: this.esQueryCtx });
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { queryString: this.queryString } ;
      $(this.el).html(this._template(params));
      this.uiSearchResult.setElement($(this.el).find('.UISearchResult')).render();
    },

    events: {
      'click .onSearch': 'onSearch',
      'keydown .onSearchInputEnter' : 'onSearchInputEnter'
    },
    
    onSearch: function(evt) {
      var queryString = $(evt.target).parent().find("input").val() ;
      this._doSearch(queryString) ;
    },

    onSearchInputEnter: function(evt) {
      if(evt.keyCode == 13){
        var queryString = $(evt.target).parent().find("input").val() ;
        this._doSearch(queryString) ;
      }
    },

    _doSearch: function(queryString) {
      this.queryString = queryString ;
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
      this.uiSearchResult.onSearch(this.esQueryCtx);
      this.render();
    }
  });
  
  return UISearch ;
});
