define([
  'jquery',
  'underscore', 
  'backbone',
  'util/util',
  'util/PageList',
  'ui/UIUtil',
  'ui/UIBreadcumbs',
  'plugins/elasticsearch/search/hit/UISearchHitDetail',
  'text!plugins/elasticsearch/search/hit/UISearchHitList.jtpl'
], function($, _, Backbone, util, PageList, UIUtil, UIBreadcumbs, UISearchHitDetail, Template) {
  var UISearchHitList = Backbone.View.extend({
    label: 'Search Hits',
    
    initialize: function (options) {
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { 
        util: util,
        fieldStates: this.fieldStates,
        hitPageList: this.hitPageList 
      } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectPage': 'onSelectPage',
      'click .onViewDetail': 'onViewDetail'
    },

    onSelectPage: function(evt) {
      var page = $(evt.target).attr("page") ;
      this.hitPageList.getPage(parseInt(page));
      this.render();
    },

    onViewDetail: function(evt) {
      var row = $(evt.target).closest("td").attr("row") ;
      var hit = this.hitPageList.getItemOnCurrentPage(parseInt(row));
      var uiSearchHitResult = UIUtil.getAncestorOfType(this, 'UISearchHitResult');
      uiSearchHitResult.push(new UISearchHitDetail({ hit: hit}));
    },
    
    onResult: function(queryResult) {
      this.queryResult = queryResult;
      this.hitPageList = new PageList(20, queryResult.hits);
      this.fieldStates  = queryResult.fieldStates;
    }
  });

  var UISearchHitResult = UIBreadcumbs.extend({
    type:  "UISearchHitResult",
    label: 'Search Hits',

    onResult: function(result) {
      this.clear();
      this.uiSearchHitList = new UISearchHitList();
      this.uiSearchHitList.onResult(result);
      this.push(this.uiSearchHitList);
    }
  });
  
  return UISearchHitResult ;
});
