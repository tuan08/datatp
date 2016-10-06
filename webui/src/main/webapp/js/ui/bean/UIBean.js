define([
  'jquery', 'underscore', 'backbone',
  "ui/bean/widget",
  'util/util',
  "ui/bean/UIBeanEditor",
], function($, _, Backbone, widget, util, UIBeanEditor) {
  var UIBeanTmpl = `
    <%var width = config.width ? config.width : "100%"; %>

    <div class="ui-beans" style="width: <%=width%>">
      <%if(config.header) {%>
        <div class="header">
          <span><%=config.header%></span>
          <div class="box-display-ib toggle-mode"><span/></div>
        </div>
      <%}%>

      <div class="ui-bean">
        <%for(var name in beanInfo.fields) { %>
        <%  var field = beanInfo.fields[name]; %>
            <div class="field">
              <div class="field-label"><%=field.label%></div>
              <div class="field-value" field="<%=name%>"><span/></div>
            </div>
        <%}%>
      <div>
      <div class="actions"></div>
    </div>
  `;

  var UIBean = UIBeanEditor.extend({
    initialize: function (options) {
      var defaultConfig = {} ;
      if(!this.config) $.extend(defaultConfig, this.config);
      this.config = defaultConfig;

      $.extend(this.events, this.UIBeanEditorEvents);
      if(this.onInit) this.onInit(options);
    },

    init: function(bInfo, bean, beanState) { 
      this.beanInfo = bInfo;
      this.bean     = bean;
      this.state    = beanState;
    },

    configure: function(newConfig) { 
      $.extend(this.config, newConfig); 
      return this;
    },
    
    set: function(bInfo, bean) { 
      this.beanInfo = bInfo;
      this.bean     = bean;
      this.state    = this.__createBeanState(bInfo, bean);
      this.state.editeMode = false;
      return this;
    },

    onViewMode: function() {
      var fieldBlks = $(this.el).find('div[field]');
      var beanInfo = this.beanInfo;
      var beanState = this.state;
      fieldBlks.each(function(idx, ele) {
        widget.view.field($(ele), beanInfo, beanState);
      });
      this.state.editMode = false;
    },

    onEditMode: function() {
      var uiFieldValues = $(this.el).find('.field-value');
      for(var i = 0; i < uiFieldValues.length; i++) {
        widget.edit.field($(uiFieldValues[i]), this.beanInfo, this.state);
      }
      this.state.editMode = true;
    },

    _template: _.template(UIBeanTmpl),

    render: function() {
      var params = { config: this.config, beanInfo: this.beanInfo, bean: this.bean };
      $(this.el).html(this._template(params));
      this.onViewMode();

      var actionsBlk = $(this.el).find(".actions");
      widget.actions(actionsBlk, this.config.actions);

      var uiToggleMode = $(this.el).find(".ui-beans").find(".toggle-mode");
      widget.toggle(uiToggleMode);
    },
    

    events: {
      //Handle by UIBean
      'click      .actions .onAction' : 'onAction',
      'click      .toggle-mode .onToggleMode' : 'onToggleMode',
    },

    __getBean: function(fv) { return this.bean; },

    __getBeanState: function(fv) { return this.state; },

    __getBeanInfo: function() { return this.beanInfo; },

    onAction: function(evt) {
      var name = $(evt.target).attr('name');
      var action = this.config.actions[name];
      action.onClick(this);
    },

    onToggleMode: function(evt) {
      if(this.state.editMode)  this.onViewMode();
      else this.onEditMode();
    },
  });

  return UIBean ;
});
