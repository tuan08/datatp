define([
  'jquery', 
  'underscore', 
  'backbone',
  "nv",
  "ui/UIUtil",
  "css!../../../libs/d3/nv/nv.d3.min.css"
], function($, _, Backbone, nv, UIUtil) {
  var UINVChart = Backbone.View.extend({
    data:  [],

    initialize: function (options) {
      if(this.config == null) this.config = {};
      if(this.config.width == null) this.config.width = "100%";
      if(this.config.height == null) this.config.height = "600px";
      if(this.config.id == null) this.config.id = "ui-chart-" + UIUtil.guid();
      if(this.onInit) this.onInit(options);
    },

    _template: _.template(
      "<div id='<%=config.id%>' style='width: <%=config.width%>; height: <%=config.height%>'><svg></svg></div>"
    ),

    render: function() {
      var params = { config: this.config };
      $(this.el).html(this._template(params));
      var chart = this.createChart(this.config, this.data) ;
      nv.addGraph(function() { return chart; });
    },

    createChart: function() { 
      throw new Error('This method should be overrided'); 
    }
  });
  
  return UINVChart ;
});
