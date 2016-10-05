define([
  'jquery', 
  'underscore', 
  'backbone'
], function($, _, Backbone) {

  var UIDialogTmpl = `
    <%function renderActions(actions) {%>
      <%for(var key in actions) {%>
      <%  var action = actions[key]; %>
          <a class="ui-action onAction" action="<%=key%>"><%=action.label%></a>
      <%}%>
    <%}%>

    <div class="dialog">
      <div class="banner">
        <span class="box-display-ib" style="width: calc(100% - 20px)"><%=title%></span>
        <span class="close">×</span>
      </div>
      <div class="ui-modal-content"></div>

      <div class="box-layout-left-right footer">
        <div class="box-display-ib"><%=footerMessage%></div>
        <div class="box-display-ib actions"><%renderActions(actions);%></div>
      </div>
    </div>
  `;

  var UIDialog = Backbone.View.extend({
    el:   "#UIPopupDialog",
    type: 'UIDialog' ,
    
    initialize: function (options) {
    },

    _template: _.template(UIDialogTmpl),

    events: {
      'click .close': 'close',
      'click .onAction': 'onAction',
    },

    onAction: function(evt) {
      var actionName = $(evt.target).attr('action') ;
      this.config.actions[actionName].onClick(this.uiComponent);
    },

    activate: function(uicomp, config) {
      var UIDialog = this;
      var params = { 
        title: "Dialog", footerMessage: "", 
        width: "600px", height: "400px",
        actions: {
          cancel: {
            label: "Cancel",
            onClick: function(thisUI) { UIDialog.close(); }
          }
        }
      };
      params = $.extend(params, config);

      $(this.el).html(this._template(params));
      $(this.el).find(".dialog").css({ width: params.width, height: params.height });
      var uiModalContent = this.$('.ui-modal-content') ;
      uicomp.setElement(uiModalContent).render();
      $(this.el).css("display", "block");
      this.config = params;
      this.uiComponent = uicomp;
    },
    
    close: function() {
      $(this.el).empty() ;
      $(this.el).css("display", "none");
    }
  });
  
  return new UIDialog() ;
});
