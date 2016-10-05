define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  "ui/widget",
  "ui/UIDialog",
  "ui/bean/UIBean",
  "text!ui/bean/UITableWS.jtpl"
], function($, _, Backbone, util, widget, UIDialog, UIBean, TableTmpl) {

  var UITableWS = Backbone.View.extend({

    _template: _.template(TableTmpl),

    render: function() {
      var params = { 
        config: this.uiParent.config, 
        beanInfo: this.uiParent.beanInfo,
        beanStatePageList: this.uiParent.beanStatePageList,
        util: util,
        widget: widget
      };
      $(this.el).html(this._template(params));
    },

    events: {
      'click    .onToggleControl': 'onToggleControl',
      'click    .onSelectPage' :   'onSelectPage'
    },

    onToggleControl: function(evt) { this.uiParent.toggleControl(); },

    onSelectPage: function(evt) { 
      var page = widget.page.iterator.getSelectPage(evt);
      this.uiParent.setSelectPage(page);
    },
  });

  return UITableWS;
});
