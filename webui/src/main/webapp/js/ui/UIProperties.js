define([
  'jquery', 
  'underscore', 
  'backbone',
], function($, _, Backbone) {
  var TEMPLATE = `
    <%var width = config.width ? config.width : "100%"; %>
    <div class="ui-bean" style="width: <%=width%>">
      <%if(config.header) {%>
        <h6 class="box-bottom-border"><%=config.header%></h6>
      <%}%>

      <%for(var key in bean) { %>
        <div class="box-bottom-border">
          <label class="ui-ib-120px"><%=key%></label>
          <span class="ui-ib"><%=bean[key]%></label>
        </div>
      <%}%>
    </div>
  `;

  var UIProperties = Backbone.View.extend({
    initialize: function (options) {
      if(options) {
        this.bean = options.bean;
      }
      if(this.onInit) this.onInit(options);
      if(!this.config) this.config = {};
    },

    setBean: function(bean) { this.bean = bean; },

    _template: _.template(TEMPLATE),

    render: function() {
      var params = { config: this.config, bean: this.bean };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onSelect': 'onSelect'
    },

    onSelect: function(evt) {
    }
  });
  
  return UIProperties ;
});
