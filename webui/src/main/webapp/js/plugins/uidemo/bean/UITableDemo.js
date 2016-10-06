define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/UITable',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UITable, data) {

  var UIHelloPluginCtrl = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(`
      <div class="ui-card">
        <h6>Hello Table Plugin Control<h6>
        <div>
          <a class="ui-action onSetPageSize50">Set Page Size To 50</a>
        </div>
        <div>
          <a class="ui-action onSetPageSize100">Set Page Size To 100</a>
        </div>
      </div>
    `),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
    },

    events: {
      'click a.onSetPageSize50': 'onSetPageSize50',
      'click a.onSetPageSize100': 'onSetPageSize100'
    },

    onSetPageSize50: function(evt) {
      this.uiTable.updateDisplayRow(50);
    },

    onSetPageSize100: function(evt) {
      this.uiTable.updateDisplayRow(100);
    }
  });

  var UITableDemo = UITable.extend({
    label: "UITable Demo", 

    config: {
      control: { header: "Table Control Demo"},
      table: { header: "Demo Table"},
      actions: {
        edit: {
          label: "Edit",
          onClick: function(uiTable, beanState) {
            console.log("on click");
            console.printJSON(beanState);
          }
        }
      }
    },
    
    onInit: function(options) {
      this.addDefaultControlPluginUI();
      this.addControlPluginUI("Hello", new UIHelloPluginCtrl());
      this.set(data.BeanInfo, data.createBeans("Table Bean", 100));
    }
  }) ;

  return new UITableDemo() ;
});
