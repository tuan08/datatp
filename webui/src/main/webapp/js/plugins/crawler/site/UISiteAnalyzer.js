define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible'
], function($, _, Backbone, UICollabsible) {

  var UIURLStructure = Backbone.View.extend({
    label: "URL Structure",
    
    initialize: function () {
      _.bindAll(this, 'render') ;
      
    },
    
    _template: _.template("Hello"),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onEvent': 'onEvent'
    },
    
    onEvent: function(evt) {
    }
  });
    

  var UISiteAnalyzer = UICollabsible.extend({
    label: "Site Analyzer", 
    config: {
      actions: [
        {
          action: "analyze", label: "Analyze",
          onClick: function(thisUI) { }
        },
        { 
          action: "back", label: "Back",
          onClick: function(thisUI) {
          }
        }
      ]
    },

    onInit: function(options) {
      this.add(new UIURLStructure());
    }
  }) ;

  return UISiteAnalyzer ;
});
