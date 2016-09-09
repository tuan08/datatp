define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UIUtil',
  'plugins/search/UISearchHits',
  'plugins/search/UISearchHit',
  'plugins/search/rest/Rest',
  'text!plugins/search/UISearch.jtpl'
], function($, _, Backbone, UITabbedPane, UIUtil, UISearchHits, UISearchHit,  Rest, Template) {
  var UISearchResult = UITabbedPane.extend({
    label: 'Search',

    config: {
      tabs: [
        { 
          label: "Serarch Result",  name: "searchResult",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiSearchHits) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      this.uiSearchHits = new UISearchHits();
      this.uiSearchHits.uiParent = this;
    },
    
    onSearch: function(xdocQuery) {
      this.uiSearchHits.setXDocQuery(xdocQuery);
      this.setSelectedTab('searchResult');
      this.render();
    },

    addHitView: function(hit) {
      var label = "Unknown";
      if(hit._source.entity != null) {
        var entity = hit._source.entity;
        if(entity.content != null && entity.content.title != null) {
          label = entity.content.title.join();
        }
      }
      if(label.length > 30) label = label.substring(0, 30) + "...";
      var name = label.replace(/ |"|'/g, '');
      this.addTab(name, label, new UISearchHit({searchHit: hit}), true, true);
      this.render();
    }
  });

  var UISearch = Backbone.View.extend({
    type: 'UISearch',
    el:   $("#UISearch"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { xdocQuery: this.xdocQuery } ;
      $(this.el).html(this._template(params));
      $('#UISearchResult').unbind();
      $('#UISearchResult').empty();
      if(this.uiSearchResult != null) {
        this.uiSearchResult.setElement($('#UISearchResult')).render();
      }
    },

    events: {
      'click .onSearch': 'onSearch'
    },
    
    onSearch: function(evt) {
      var query = $(evt.target).parent().find("input").val() ;
      this.xdocQuery = Rest.searcher.createXDocQuery(query, 10);
      this.xdocQuery.execute();
      this.uiSearchResult = new UISearchResult();
      this.uiSearchResult.uiParent = this;
      this.render();
      this.uiSearchResult.onSearch(this.xdocQuery);
    },

    addHitView: function(hit) {
      this.uiSearchResult.addHitView(hit);
    }
  });
  
  return UISearch ;
});
