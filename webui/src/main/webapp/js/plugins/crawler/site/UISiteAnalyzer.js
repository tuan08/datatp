define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/UITable',
  'plugins/crawler/Rest',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UITable, Rest, data) {

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
    label: 'Site Structure Analyzer',

    config: {
      control: { header: "Table Control Demo"},
      table: { header: "Demo Table"}
    },
    
    onInit: function(options) {
      this.addDefaultControlPluginUI();
      this.addControlPluginUI("Hello", new UIHelloPluginCtrl());
      this.set(data.BeanInfo, data.createBeans("Table Bean", 100));
    },


    configure: function(siteConfig) {
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(siteConfig, 250, false);
      return this;
    }
  }) ;

  return  UITableDemo ;
});
