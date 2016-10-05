define([
  'jquery', 
  'underscore', 
  'backbone',
], function($, _, Backbone) {
  var UISiteAnalyzerWS = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(`
      Hello Workspace
    `),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onOpen': 'onOpen'
    },

    set: function(urlSiteStructure) {
      var domainStructures = urlSiteStructure.domainStructures;
      for (var name in domainStructures) {
        var domainStructure = domainStructures[name];
      }
      return this;
    }
  });

  return UISiteAnalyzerWS ;
});
