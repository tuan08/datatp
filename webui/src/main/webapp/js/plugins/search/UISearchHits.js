define([
  'jquery',
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'plugins/search/rest/Rest',
  'text!plugins/search/UISearchHits.jtpl'
], function($, _, Backbone, UIUtil,  Rest, Template) {

  var UISearchHits = Backbone.View.extend({
    el: $("#UISearchHits"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },

    setXDocQuery: function(xdocQuery) { this.xdocQuery = xdocQuery; },
    
    _template: _.template(Template),

    render: function() {
      var params = { xdocQuery: this.xdocQuery } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectPage': 'onSelectPage',
      'click .onSelectHit': 'onSelectHit'
    },
    
    onSelectPage: function(evt) {
      var page = $(evt.target).attr("page") ;
      this.xdocQuery.goToPage(parseInt(page));
      this.render();
    },

    onSelectHit: function(evt) {
      var index = $(evt.target).attr("index") ;
      var hits  = this.xdocQuery.getHits();
      var hit = hits[parseInt(index)];
      var uiSearch = UIUtil.getAncestorOfType(this, 'UISearch');
      uiSearch.addHitView(hit);
    }

  });
  
  return UISearchHits ;
});
