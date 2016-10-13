define([
  'jquery', 'underscore', 'backbone',
  'ui/UIView'
], function($, _, Backbone, UIView) {
  var TEMPLATE = `
    <%var width = config.width ? config.width : "100%"; %>
    <div class="ui-bean" style="width: <%=width%>">
      <%if(config.header) {%>
        <h6 class="box-border-bottom"><%=config.header%></h6>
      <%}%>

      <%for(var key in bean) { %>
        <div class="box-border-bottom">
          <label class="box-display-ib" style="width: <%=config.label.width%>"><%=key%></label>
          <span class="box-display-ib"><%=bean[key]%></label>
        </div>
      <%}%>
    </div>
  `;

  var UIProperties = UIView.extend({
    defaultConfig: {
      width: "100%",
      label: { width: "120px" }
    },

    initialize: function (options) {
      this.config = this.mergeConfig(null);
      if(this.onInit) this.onInit(options);
    },

    conf: function(config) {
      $.extend(true, this.config, config);
      return this;
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
