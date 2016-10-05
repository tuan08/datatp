define([
  'jquery',
  'underscore', 
  'backbone',
  'text!ui/UIBorderLayout.jtpl'
], function($, _, Backbone, Template) {

  var UIBorderLayout = Backbone.View.extend({
    initialize: function(options) {
      this.layout = { };
      if(this._onInit) this._onInit(options);
      if(this.onInit) this.onInit(options);
    },

    getUI: function(position) { 
      if(this.layout[position]) return this.layout[position].uiComponent;
      return null;
    },

    setUI: function(position, uiComponent, config, refresh) {
      var layoutConfig = { };
      if(config != null) $.extend(layoutConfig, config);
      uiComponent.uiParent = this;
      this.layout[position] = { config: layoutConfig, uiComponent: uiComponent }
      if(refresh) this.refresh(position);
    },

    removeUI: function(position) { this.layout[position] = null; },

    toggleUISplit: function(pos) {
      var config = this.layout[pos];
      var split = this.$('.' + pos + '-split').first();
      var display = split.css('display');
      if(display == 'none') {
        split.css("display", config.display);
      } else {
        config.display = display;
        split.css("display", 'none');
      }
    },

    refreshUIPanel: function(position) {
      var config = this.layout[position];
      var panel = this.$('.' + position + '-panel').first();
      if(config) config.uiComponent.render();
    },

    _template: _.template(Template),

    render: function() {
      var params = { layout: this.layout } ;
      $(this.el).html(this._template(params));
      var northConfig = this.layout.north;
      if(this.layout.north) {
        var north = this.layout.north;
        north.uiComponent.setElement(this.$('.north-panel').first()).render();
      }
      this.refresh('north');
      this.refresh('west');
      this.refresh('center');
      this.refresh('east');
      this.refresh('shouth');
    },

    refresh: function(position) {
      var config = this.layout[position];
      var panel = this.$('.' + position + '-panel').first();
      panel.empty();
      panel.unbind();
      if(config) config.uiComponent.setElement(panel).render();
    },


    events: {
      "mousedown .onResizeNorthPanel": "onResizeNorthPanel",
      "dblclick  .onResizeNorthPanel": "onToggleNorthPanel",

      "mousedown .onResizeShouthPanel": "onResizeShouthPanel",

      "mousedown .onResizeWestPanel": "onResizeWestPanel",
      "mousedown .onResizeEastPanel": "onResizeEastPanel"
    },

    onResizeNorthPanel: function(evt) {
      var uisplit = $(evt.target).closest(".ui-split");
      var nPanel = uisplit.find(".north-split").first().find(".north-panel").first();
      var middleSplit = uisplit.find(".middle-split").first();
      this.onVResize(evt, nPanel, middleSplit, true);
    },

    onToggleNorthPanel: function(evt) {
      console.log('onToggleNorthPanel');
    },

    onResizeShouthPanel: function(evt) {
      var uisplit = $(evt.target).closest(".ui-split");
      var sPanel = uisplit.find(".shouth-split").first().find(".shouth-panel").first();
      var middleSplit = uisplit.find(".middle-split").first();
      this.onVResize(evt, sPanel, middleSplit, false);
    },

    onResizeWestPanel: function(evt) {
      var uisplit = $(evt.target).closest(".ui-split");
      var middleSplit = uisplit.find(".middle-split").first();
      var westSplit = middleSplit.find(".west-split").first();
      var centerSplit = middleSplit.find(".center-split").first();
      this.onHResize(evt, westSplit, centerSplit, true);
    },

    onResizeEastPanel: function(evt) {
      var uisplit = $(evt.target).closest(".ui-split");
      var middleSplit = uisplit.find(".middle-split").first();
      var eastSplit = middleSplit.find(".east-split").first();
      var centerSplit = middleSplit.find(".center-split").first();
      this.onHResize(evt, eastSplit, centerSplit, false);
    },

    onVResize: function(evt, panel, neighborPanel, panelOnTop) {
      evt.preventDefault();
      var y0 = evt.pageY ;
      var panelH = panel.height();
      var neighborH = neighborPanel.height();

      $(document).mousemove(function (e) {
        e.preventDefault();
        var deltaH = e.pageY - y0;
        if(panelOnTop) {
          panel.height(panelH + deltaH);
          neighborPanel.height(neighborH - deltaH);
        } else {
          panel.height(panelH - deltaH);
          neighborPanel.height(neighborH + deltaH);
        }
      });

      $(document).mouseup(function (e) {
        $(document).unbind('mousemove');
      });
    },

    onHResize: function(evt, panel, neighborPanel, panelOnLeft) {
      evt.preventDefault();
      var x0 = evt.pageX ;
      var panelW = panel.width();

      $(document).mousemove(function (e) {
        e.preventDefault();
        var deltaW = e.pageX - x0;
        if(panelOnLeft) {
          var w = panelW + deltaW ;
          panel.width(w);
          neighborPanel.css("margin-left", w + "px");
        } else {
          var w = panelW - deltaW ;
          panel.width(w);
          neighborPanel.css("margin-right", w + "px");
        }
      });

      $(document).mouseup(function (e) {
        $(document).unbind('mousemove');
      });
    }
  });
  
  return UIBorderLayout ;
});
