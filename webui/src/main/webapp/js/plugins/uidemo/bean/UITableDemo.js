define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/UIBean',
  'ui/table/UITable',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UIBean, UITable, data) {

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
        toolbar: {
          refresh: {
            label: "Refresh",
            onClick: function(uiTable) { 
              uiTable.setBeans(data.createBeans("Table Bean At" + new Date(), 100), true);
            }
          }
        },

        bean: {
          edit: {
            label: "Edit",
            onClick: function(uiTable, beanState) {
              var bean = beanState.bean;
              var uiBean = new UIBean();
              uiBean.set(uiTable.beanInfo, bean);
              uiTable.addWorkspaceTabPluginUI(bean.input, bean.input, uiBean, true, true);
              uiTable.refreshWorkspace();
            }
          },
          remove: {
            label: "Del",
            onClick: function(uiTable, beanState) {
              uiTable.removeBeanState(beanState, true);
            }
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
