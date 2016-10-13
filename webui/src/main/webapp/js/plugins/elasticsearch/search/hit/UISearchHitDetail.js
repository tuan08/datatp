define([
  'jquery',
  'underscore', 
  'backbone',
  'util/util',
  'text!plugins/elasticsearch/search/hit/UISearchHitDetail.jtpl'
], function($, _, Backbone, util, Template) {
  var UISearchHitDetail = Backbone.View.extend({
    label: 'Hit Detail',
    
    withHit: function (hit) {
      this.hit = hit;
      return this;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { 
        hit:  this.hit, util: util
      } ;
      $(this.el).html(this._template(params));
    }
  });
  
  return UISearchHitDetail ;
});
