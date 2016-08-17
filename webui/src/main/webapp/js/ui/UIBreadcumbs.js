define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'text!ui/UIBreadcumbs.jtpl'
], function($, _, Backbone, UIUtil, UIBreadcumbsTmpl) {
  var UIBreadcumbs = Backbone.View.extend({
    type: 'UIBreadcumbs',
    
    initialize: function (options) {
      //this.el = options.el ;
      this.views = [] ;
      if(this.onInit) this.onInit(options) ;
      _.bindAll(this, 'render', 'onSelectView') ;
    },
    
    _template: _.template(UIBreadcumbsTmpl),
    
    render: function() {
      var params = {} ;
      $(this.el).html(this._template(params));

      if(this.views.length > 0) {
        var breadcumbs = this.$('.Breadcumbs') ;
        for(var i = 0; i < this.views.length; i++) {
          var view = this.views[i];
          var label = view.label ;
          if(label == null) label = "???" ;
          if(i > 0) {
            breadcumbs.append("<span style='font-weight: bold'> &gt;&gt; </span>");
          }
          if(i == this.views.length - 1) {
            breadcumbs.find("a").removeClass('ui-disabled');
	    breadcumbs.append(this._buttonTmpl({label: label}));
            view.setElement(this.$('.BreadcumbsView')).render();
          } else {
            breadcumbs.append(this._buttonTmpl({label: label}));
          }
        }
      }
    },
    
    _buttonTmpl: _.template("<a class='onSelectView ui-action ui-disabled'><%=label%></a>"),

    add: function(uicomponent) { this.push(uicomponent) ; },

    remove: function(uicomponent) { throw new Error('to implement') ; },
    
    push: function(view) {
      view.uiParent = this ;
      this.views.push(view) ;

      var label = view.label ;
      if(label == null) label = "???" ;
      var breadcumbs = this.$('.Breadcumbs') ;
      if(this.views.length > 1) {
        breadcumbs.append("<span style='font-weight: bold'> &gt;&gt; </span>");

      }
      breadcumbs.find("a").removeClass('ui-disabled');
      breadcumbs.append(this._buttonTmpl({label: label}));

      this.$('.BreadcumbsView').unbind() ;
      view.UIParent = this ;
      view.setElement(this.$('.BreadcumbsView')).render();
    },
    
    back: function() {
      if(this.views.length <= 1) return ;
      var view = this.views[this.views.length - 2];
      this._removeToLabel(view.label) ;
    },

    getAncestorOfType: function(type) {
      return UIUtil.getValidatorOfType(this, type) ;
    },
    
    events: {
      'click a.onSelectView': 'onSelectView'
    },
    
    onSelectView: function(evt) {
      var label = $.trim($(evt.target).text()) ;
      this._removeToLabel(label);
    },


    _removeToLabel: function(label) {
      var breadcumbs = this.$('.Breadcumbs') ;
      for(var i = this.views.length - 1; i >= 0; i--) {
        if(this.views[i].label == label) {
          this.$('.BreadcumbsView').unbind() ;
          breadcumbs.find("a:last-child").addClass("ui-disabled");
          this.views[i].setElement(this.$('.BreadcumbsView')).render();
          return ;
        } else {
          var view = this.views.pop() ;
          breadcumbs.find("a:last-child").remove();
          breadcumbs.find("span:last-child").remove();
        }
      }
    }
  });
  
  return UIBreadcumbs ;
});
