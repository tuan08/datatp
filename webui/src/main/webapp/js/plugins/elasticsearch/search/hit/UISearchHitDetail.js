define([
  'jquery',
  'underscore', 
  'backbone',
  'util/util',
  'text!plugins/elasticsearch/search/hit/UISearchHitDetail.jtpl'
], function($, _, Backbone, util, Template) {
  var UISearchHitDetail = Backbone.View.extend({
    label: 'Hit Detail',
    
    initialize: function (options) {
      this.hit = options.hit;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { 
        hit:  this.hit,
        util: util
      } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectPage': 'onSelectPage'
    }
  });
  
  return UISearchHitDetail ;
});
