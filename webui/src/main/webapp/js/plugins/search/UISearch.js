define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UIContent',
  'plugins/search/rest/Rest',
  'text!plugins/search/UISearch.jtpl'
], function($, _, Backbone, UITabbedPane, UIContent, Rest, Template) {
  var UISearchResult = UITabbedPane.extend({
    label: 'Search',

    config: {
      tabs: [
        { 
          label: "Serarch Result",  name: "searchResult",
          onSelect: function(thisUI, tabConfig) {
            var uiTab1 = new UIContent( { content: "Tab 1" }) ;
            thisUI.setSelectedTab(tabConfig.name, uiTab1) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
    }
  });

  var UISearch = Backbone.View.extend({
    el: $("#UISearch"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
      this.uiSearchResult = new UISearchResult();
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { xdocQuery: this.xdocQuery } ;
      $(this.el).html(this._template(params));
      var block = $('#UISearchResult') ;
      console.log(block.html());
      this.uiSearchResult.setElement($('#UISearchResult')).render();
    },

    events: {
      'click .onSearch': 'onSearch',
      'click .onSelectPage': 'onSelectPage'
    },
    
    onSearch: function(evt) {
      var query = $(evt.target).parent().find("input").val() ;
      this.xdocQuery = Rest.searcher.createXDocQuery(query, 10);
      this.xdocQuery.execute();
      this.render();
    },

    onSelectPage: function(evt) {
      var page = $(evt.target).attr("page") ;
      this.xdocQuery.goToPage(parseInt(page));
      this.render();
    },
  });
  
  return UISearch ;
});
