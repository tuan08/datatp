define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIDialog',
  'plugins/uidemo/UIPropertiesDemo'
], function($, _, Backbone, UIDialog, UIPropertiesDemo) {
  var UIDialogDemoTmpl = `
    <div>
      <a class="ui-action onOpen">Open UIDialog</a>
    </div>
  `;

  var UIDialogDemo = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(UIDialogDemoTmpl),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onOpen': 'onOpen'
    },

    onOpen: function(evt) {
      evt.preventDefault();
      var config = {
        title: "Hello Modal",
        width: "400px", height: "400px"
      }
      UIDialog.activate(UIPropertiesDemo, config);
    }
  });
  
  return new UIDialogDemo() ;
});
