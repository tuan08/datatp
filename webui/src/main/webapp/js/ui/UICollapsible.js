define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil',
  'text!ui/UICollapsible.jtpl',
], function($, _, Backbone, UIUtil, UICollapsibleTmpl) {
  /** *@type ui.UICollapsible */
  var UICollapsible = Backbone.View.extend({
    
    initialize: function (options) {
      this.clear() ;
      this.type = 'UICollapsible' ;
      this.onInit(options) ;
    },
    
    onInit: function(options) { },

    
    add: function(component, collapsed) {
      component.collapible = {} ;
      if(collapsed) component.collapible.collapsed = true ;
      component.uiParent = this ;
      this.components.push(component) ; 
    },

    remove: function(component) {
      var holder = [] ;
      for(var i = 0; i < this.components.length; i++) {
        if(this.components[i] !=  component) {
          holder.push(this.components[i]) ;
        }
      }
      this.components = holder() ;
    },

    clear: function() { 
      this.components = [] ;
      this.state = { actions: {} } ;
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },
    
    setActionHidden: function(actionName, bool) {
      if(this.state.actions[actionName] == null) {
        this.state.actions[actionName] = {} ;
      }
      this.state.actions[actionName].hidden = bool ;
    },
    
    _template: _.template(UICollapsibleTmpl),
    
    render: function() {
      var params = {
        title:      this.label,
        config:     this.config,
        state:      this.state,
        components: this.components
      } ;
      $(this.el).html(this._template(params));
      $(this.el).trigger("create");
      
      for(var i = 0; i < this.components.length; i++) {
        var comp = this.components[i] ;
        var blockClass = '.ui-component-' + i ;
        comp.setElement(this.$(blockClass)).render();
      }
    },
    
    events: {
      'click a.onToggleCollapsibleSection': 'onToggleCollapsibleSection',
      'click a.onAction': 'onAction',
    },
    
    onToggleCollapsibleSection: function(evt) {
      var collapsibleBlock = $(evt.target).closest(".ui-collapsible") ;
      var collapsibleSectionBlock = collapsibleBlock.find(".ui-collapsible-section") ;
      var iconEle =$(evt.target).closest(".ui-icon");
      var compIdx = $(evt.target).closest("a").attr("component") ;
      this.components[compIdx].collapible.collapsed = !this.components[compIdx].collapible.collapsed ;

      var iconEle =$(evt.target).closest(".ui-icon");
      if(this.components[compIdx].collapible.collapsed) {
        iconEle.removeClass("ui-icon-minus");
        iconEle.addClass("ui-icon-plus");
        collapsibleSectionBlock.css("display", "none");
      } else {
        iconEle.removeClass("ui-icon-plus");
        iconEle.addClass("ui-icon-minus");
        collapsibleSectionBlock.css("display", "block");
      }
    },
    
    onAction: function(evt) {
      var actionName= $(evt.target).closest('a').attr('action') ;
      var actions = this.config.actions ;
      for(var i = 0; i < actions.length; i++) {
        if(actions[i].action == actionName) {
          actions[i].onClick(this) ;
          return ;
        }
      }
    }
  });
  
  return UICollapsible ;
});
