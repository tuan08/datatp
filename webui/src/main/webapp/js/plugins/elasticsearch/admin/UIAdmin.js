define([
  'jquery',
  'underscore', 
  'backbone',
  'text!plugins/elasticsearch/admin/UIAdmin.jtpl'
], function($, _, Backbone,  Template) {
  var UIAdmin = Backbone.View.extend({
    label: "Elasticsearch Admin",
    
    initialize: function () {
      this._loadUI('cluster/UIClusterInfo');
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectUI': 'onSelectUI'
    },

    onSelectUI: function(evt) {
      var name = $(evt.target).closest('.onSelectUI').attr('name') ;
      this._loadUI(name);
    },

    _loadUI: function(name) {
      console.log("load " + name) ;
      require(['plugins/elasticsearch/admin/' + name], function(uiComp) { 
        $('#UIAdminWS').empty();
        $('#UIAdminWS').unbind();
        uiComp.setElement($('#UIAdminWS')).render();
      }) ;
    }
  });
  
  return UIAdmin ;
});
