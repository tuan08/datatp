define([
  'jquery', 
  'jqueryui', 
  'underscore', 
  'backbone'
], function($,jqueryui,  _, Backbone) {
  var modalTmpl = _.template(`
    <div class="modal-content">
      <div style="text-align: right; border-bottom: 1px solid #ddd;">
        <span class="close">×</span>
      </div>
      <p>Some text in the Modal..</p>
    </div>
  `);

  var UIPopup = Backbone.View.extend({
    el: "#UIPopupDialog",
    
    initialize: function (config) {
      this.type = 'UIPopup' ;
    },

    activate: function(uicomp, config) {
      uicomp.uiParent = this ;
      uicomp.setElement($(this.el)).render();
      var thisUI = this ;
      config.close = function() {
        thisUI.closePopup() ;
      };
      $(this.el).dialog(config);
    },
    
    closePopup: function() {
      $(this.el).html("") ;
      $(this.el).unbind()  ;
      $(this.el).dialog("destroy");
    }
  });
  
  return new UIPopup() ;
});
