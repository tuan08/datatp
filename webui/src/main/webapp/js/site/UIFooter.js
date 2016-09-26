define([
  'jquery',
  'underscore', 
  'backbone',
], function($, _, Backbone) {
  var UIFooter = Backbone.View.extend({
    el: $("#UIFooter"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template(`
      <div style="padding: 3px">
        <em>Copyright</em> © 2016.
      </div>
    `),
    
    render: function() {
      var params = { 
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create") ;
    },
    
    events: {
      'change select.onSelectLanguage': 'onSelectLanguage'
    },
    
    onSelectLanguage: function(evt) {
    }
  });
  
  return UIFooter ;
});
