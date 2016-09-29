define([
  'jquery', 'underscore', 'backbone',
  "ui/bean/validator",
  'util/util',
  "text!ui/bean/UIBean.jtpl"
], function($, _, Backbone, validator, util, UIBeanTmpl) {
  var editableFieldValueTemplate = _.template( `
    <div class="ui-ib editable-field-value">
      <span><%=value%></span>
      <a class="ui-ib ui-icon ui-icon-pencil" style="display: none"/>
    </div>
  `);

  var inputFieldValueTemplate = _.template( `
    <div class="ui-ib edit-field-value box-width-full">
       <input class="field-input" type="text" name="<%=fieldName%>" value="<%=value%>" autocomplete="<%=autocomplete%>" />
       <a class="ui-icon ui-icon-check"/>
    </div>
  `);

  var arrayInputFieldValueTemplate = _.template( `
    <div class="ui-ib edit-array-field-value box-width-full">
      <%for(var i = 0; i < value.length; i++) {%>
        <input class="field-input" type="text" name="<%=fieldName + '_' + i %>" value="<%=value[i]%>" autocomplete="<%=autocomplete%>" />
        <a class="ui-icon ui-icon-minus" idx="<%=i%>"/>
      <%}%>
      <a class="ui-icon ui-icon-check"/>
      <a class="ui-icon ui-icon-plus"/>
    </div>
  `);

  var selectInputFieldValueTemplate = _.template( `
    <div class="ui-ib edit-field-value box-width-full">
      <select class="field-input">
        <%var fieldValue = value != null ? value : field.defaultValue ; %>
        <%for(var i = 0; i < options.length ; i++) { %>
        <%  var selected = fieldValue == options[i].value ? 'selected' : '' ; %>
            <option value="<%=options[i].value%>" <%=selected%> >
              <%=options[i].label%>
            </option>
        <%}%>
      </select>
      <a class="ui-icon ui-icon-check"/>
    </div>
  `);

  var actionWidgetTemplate = _.template( `
    <div class="ui-ib box-width-full">
      <%for(var name in actions) {%>
        <%var action = actions[name]; %>
        <a class="ui-action onAction" name="<%=name%>"><%=action.label%></a>
      <%}%>
      <span class="ui-state-default ui-corner-all"><a class="ui-ib ui-icon ui-icon-comment onToggleMode" style="cursor: pointer"/></span>
    </div>
  `);

  var UIBean = Backbone.View.extend({
    initialize: function (options) {
      if(options) {
        if(options.beanInfo) this.beanInfo = options.beanInfo;
        if(options.bean)     this.bean     = options.bean;
      }
      if(!this.config) this.config = { };
      if(this.onInit) this.onInit(options);
    },

    setBeanInfo: function(bInfo) { this.beanInfo = bInfo; },

    setBean: function(bean) { this.bean = bean; },

    onViewMode: function() {
      var fieldBlks = $(this.el).find('div[field]');
      var bean = this.bean;
      fieldBlks.each(function(idx, ele) {
        var fieldName = $(ele).attr("field");
        var value = util.reflect.getFieldValue(bean, fieldName);
        $(ele).html(editableFieldValueTemplate({ value: value }));
      });
      this.editMode = false;
    },

    onEditMode: function() {
      var fieldValueBlks = $(this.el).find('.field-value');
      for(var i = 0; i < fieldValueBlks.length; i++) {
        this._onRenderEditFieldValue($(fieldValueBlks[i]));
      }
      this.editMode = true;
    },

    _template: _.template(UIBeanTmpl),

    render: function() {
      var params = { config: this.config, beanInfo: this.beanInfo, bean: this.bean };
      $(this.el).html(this._template(params));
      this.onViewMode();

      if(this.config.actions) {
        var actionsBlk = $(this.el).find(".actions");
        actionsBlk.html(actionWidgetTemplate({ actions: this.config.actions }));
      }
    },
    

    events: {
      "mouseover .editable-field-value":   "onMouseOverEditableFieldValue",
      "mouseout  .editable-field-value":   "onMouseOutEditableFieldValue",
      "click     .editable-field-value":   "onClickEditableFieldValue",

      'click      .edit-field-value .ui-icon-check' : 'onFieldInputSave',
      'keydown    .edit-field-value .field-input' :   'onFieldInputEnter',

      'click      .edit-array-field-value .ui-icon-check' : 'onFieldArrayInputSave',
      'click      .edit-array-field-value .ui-icon-minus' : 'onFieldArrayInputRemove',
      'click      .edit-array-field-value .ui-icon-plus'  : 'onFieldArrayInputAdd',
      'keydown    .edit-array-field-value .field-input'   : 'onFieldArrayInputEnter',

      'click      .actions .onAction' : 'onAction',
      'click      .actions .onToggleMode' : 'onToggleMode',
    },

    onMouseOverEditableFieldValue: function(evt) {
      $(evt.target).closest('.editable-field-value').find(".ui-icon-pencil").css("display", "inline-block");
    },

    onMouseOutEditableFieldValue: function(evt) {
      $(evt.target).closest('.editable-field-value').find("a").css("display", "none");
    },

    onClickEditableFieldValue: function(evt) {
      var fieldValueBlk = $(evt.target).closest('.field-value');
      this._onRenderEditFieldValue(fieldValueBlk);
    },

    onFieldInputEnter: function(evt) { if(evt.keyCode == 13) this._onSaveFieldValue($(evt.target)); },
    onFieldInputSave: function(evt) { this._onSaveFieldValue($(evt.target)); },

    onFieldArrayInputEnter: function(evt) { if(evt.keyCode == 13) this._onSaveArrayFieldValue($(evt.target)); },
    onFieldArrayInputSave: function(evt) { this._onSaveArrayFieldValue($(evt.target)); },

    onFieldArrayInputRemove: function(evt) { 
      var fieldValue = $(evt.target).closest(".field-value");
      var fieldName = fieldValue.attr('field');
      var valIdx = parseInt($(evt.target).attr('idx'));
      var value = util.reflect.getFieldValue(this.bean, fieldName);
      value.splice(valIdx, 1);
      this._onRenderEditFieldValue(fieldValue);
    },

    onFieldArrayInputAdd: function(evt) { 
      var fieldValue = $(evt.target).closest(".field-value");
      var fieldName = fieldValue.attr('field');
      var value = util.reflect.getFieldValue(this.bean, fieldName);
      value.push("");
      this._onRenderEditFieldValue(fieldValue);
    },

    onAction: function(evt) {
      var name = $(evt.target).attr('name');
      var action = this.config.actions[name];
      action.onClick(this);
    },

    onToggleMode: function(evt) {
      if(this.editMode)  this.onViewMode();
      else this.onEditMode();
    },

    _onSaveFieldValue: function(triggerEle) {
      var fieldValue = triggerEle.closest('.field-value');
      var fieldInput = fieldValue.find('.field-input');
      var fieldName = fieldValue.attr("field");
      var fieldInfo = this.beanInfo.fields[fieldName];
      var value = fieldInput.val();
      var validateResult = validator.validate(fieldInfo, value);
      if(validateResult.success) {
        util.reflect.setFieldValue(this.bean, fieldName, value);
        fieldValue.html(editableFieldValueTemplate({ value: value }));
      } else {
        fieldValue.find('.ui-text-error').remove();
        fieldValue.append("<span class='ui-text-error'>" +  validateResult.err + "<span>");
      }
    },

    _onSaveArrayFieldValue: function(triggerEle) {
      var fieldValue = triggerEle.closest('.field-value');
      var fieldName =  fieldValue.attr('field');
      var fieldInput = fieldValue.find('.field-input');
      var value = [];
      fieldInput.each(function(idx, input) {
        var inputVal = $(input).val();
        value.push(inputVal);
      });
      util.reflect.setFieldValue(this.bean, fieldName, value);
      fieldValue.html(editableFieldValueTemplate({ value: value }));
    },

    _onRenderEditFieldValue: function(fieldValueBlk) {
      var fieldName = fieldValueBlk.attr("field");
      var value = util.reflect.getFieldValue(this.bean, fieldName);
      var fieldInfo = this.beanInfo.fields[fieldName];

      var params = {
        fieldName: fieldName, field: fieldInfo,
        value: value,
        autocomplete: 'on'
      };

      if(fieldInfo.type == 'array') {
        fieldValueBlk.html(arrayInputFieldValueTemplate(params));
      } else if(fieldInfo.type == 'select') {
        if(_.isArray(fieldInfo.options)) {
          params.options = fieldInfo.options;
        } else {
          params.options = fieldInfo.options(this);
        }
        fieldValueBlk.html(selectInputFieldValueTemplate(params));
      } else {
        fieldValueBlk.html(inputFieldValueTemplate(params));
      }
    },
  });

  return UIBean ;
});
