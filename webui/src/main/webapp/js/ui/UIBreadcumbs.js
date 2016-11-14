define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil',
], function($, _, Backbone, UIUtil) {
  var UIBreadcumbsTmpl = `
    <div class="ui-breadcumbs">
      <div class="breadcumbs"></div>
      <div class="view" style="padding-top: 10px"></div>
    </div>
  `;

  var UIBreadcumbs = Backbone.View.extend({
    type: 'UIBreadcumbs',
    
    initialize: function (options) {
      this.views = [] ;
      if(this.onInit) this.onInit(options) ;
      _.bindAll(this, 'render', 'onSelectView') ;
    },
    
    _template: _.template(UIBreadcumbsTmpl),
    
    render: function() {
      var params = {} ;
      $(this.el).html(this._template(params));

      if(this.views.length > 0) {
        var breadcumbs = this.$('.breadcumbs') ;
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
            view.setElement(this.$('.view')).render();
          } else {
            breadcumbs.append(this._buttonTmpl({label: label}));
          }
        }
      }
    },
    
    _buttonTmpl: _.template("<a class='onSelectView ui-action ui-disabled'><%=label%></a>"),

    add: function(uicomponent) { this.push(uicomponent) ; },

    remove: function(uicomponent) { throw new Error('to implement') ; },

    clear: function() { this.views = [] ; },
    
    push: function(view) {
      view.uiParent = this ;
      this.views.push(view) ;

      var label = view.label ;
      if(label == null) label = "???" ;
      var breadcumbs = this.$('.breadcumbs') ;
      if(this.views.length > 1) {
        breadcumbs.append("<span style='font-weight: bold'> &gt;&gt; </span>");

      }
      breadcumbs.find("a").removeClass('ui-disabled');
      breadcumbs.append(this._buttonTmpl({label: label}));

      this.$('.view').first().unbind() ;
      view.UIParent = this ;
      view.setElement(this.$('.view')).render();
    },
    
    back: function() {
      if(this.views.length <= 1) return ;
      var view = this.views[this.views.length - 2];
      var breadcumbs = $(this.el).children('.ui-breadcumbs').first().children(".breadcumbs") ;
      this._removeToLabel(breadcumbs, view.label) ;
    },

    getAncestorOfType: function(type) {
      return UIUtil.getValidatorOfType(this, type) ;
    },
    
    events: {
      'click .breadcumbs > a.onSelectView': 'onSelectView'
    },
    
    onSelectView: function(evt) {
      evt.stopPropagation();
      var label = $.trim($(evt.target).text()) ;
      var breadcumbs = $(evt.target).closest('.breadcumbs') ;
      this._removeToLabel(breadcumbs,label);
    },


    _removeToLabel: function(breadcumbs, label) {
      for(var i = this.views.length - 1; i >= 0; i--) {
        if(this.views[i].label == label) {
          var uiView = breadcumbs.closest(".ui-breadcumbs").children(".view");
          uiView.unbind() ;
          breadcumbs.find("a:last-child").addClass("ui-disabled");
          this.views[i].setElement(uiView).render();
          return ;
        } else {
          var view = this.views.pop() ;
          if(view.onBreadcumbsRemove) {
            view.onBreadcumbsRemove();
          }
          breadcumbs.find("a:last-child").remove();
          breadcumbs.find("span:last-child").remove();
        }
      }
    }
  });
  
  return UIBreadcumbs ;
});
