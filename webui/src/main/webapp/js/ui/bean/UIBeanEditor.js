define([
  'jquery', 'underscore', 'backbone',
  "ui/bean/validator",
  "ui/bean/widget",
  'util/util',
], function($, _, Backbone, validator, widget, util) {

  var UIBeanEditor = Backbone.View.extend({
    __getBean: function(fv) { throw new Error("this method need to override") },

    __getBeanInfo: function() { throw new Error("this method need to override") },


    UIBeanEditorEvents: {
      //Handle by UIBeanEditor
      "mouseover .editable-field-value":   "onMouseOverEditableFieldValue",
      "mouseout  .editable-field-value":   "onMouseOutEditableFieldValue",
      "click     .editable-field-value":   "onClickEditableFieldValue",

      'click      .edit-field-value .ui-icon-check' : 'onFieldInputSave',
      'keydown    .edit-field-value .field-input' :   'onFieldInputEnter',

      'click      .edit-array-field-value .ui-icon-check' : 'onFieldArrayInputSave',
      'click      .edit-array-field-value .ui-icon-minus' : 'onFieldArrayInputRemove',
      'click      .edit-array-field-value .ui-icon-plus'  : 'onFieldArrayInputAdd',
      'keydown    .edit-array-field-value .field-input'   : 'onFieldArrayInputEnter',
    },

    onMouseOverEditableFieldValue: function(evt) {
      $(evt.target).closest('.editable-field-value').find(".ui-icon-pencil").css("visibility", "visible");
    },

    onMouseOutEditableFieldValue: function(evt) {
      $(evt.target).closest('.editable-field-value').find("a").css("visibility", "hidden");
    },

    onClickEditableFieldValue: function(evt) {
      var uiFieldValue = $(evt.target).closest('.field-value');
      var beanInfo   = this.__getBeanInfo();
      var beanState  = this.__getBeanState(uiFieldValue);
      widget.edit.field(uiFieldValue, beanInfo, beanState);
    },

    onFieldInputEnter: function(evt) { if(evt.keyCode == 13) this.__onTriggerSaveFieldValue($(evt.target)); },
    onFieldInputSave: function(evt) { this.__onTriggerSaveFieldValue($(evt.target)); },

    onFieldArrayInputEnter: function(evt) { 
      if(evt.keyCode == 13) this.__onTriggerSaveArrayFieldValue($(evt.target)); 
    },

    onFieldArrayInputSave: function(evt) { this.__onTriggerSaveArrayFieldValue($(evt.target)); },

    onFieldArrayInputRemove: function(evt) { 
      var uiFieldValue = $(evt.target).closest(".field-value");
      var fieldName  = uiFieldValue.attr('field');
      var valIdx     = parseInt($(evt.target).attr('idx'));

      var beanState  = this.__getBeanState(uiFieldValue);
      var beanInfo   = this.__getBeanInfo();
      var value      = beanState.fields[fieldName].value;
      value.splice(valIdx, 1);
      widget.edit.field(uiFieldValue, beanInfo, beanState);
    },

    onFieldArrayInputAdd: function(evt) { 
      var uiFieldValue = $(evt.target).closest(".field-value");
      var fieldName  = uiFieldValue.attr('field');
      var beanState  = this.__getBeanState(uiFieldValue);
      var beanInfo   = this.__getBeanInfo();
      var value      = beanState.fields[fieldName].value;
      value.push("");
      widget.edit.field(uiFieldValue, beanInfo, beanState);
    },

    commitChange: function(uiBean) {
      var uiFieldValues = uiBean.find(".field-value");
      var beanInfo = this.__getBeanInfo();
      for(var i = 0; i < uiFieldValues.length; i++) {
        var uiFieldValue = $(uiFieldValues[i]);
        var fieldName    = uiFieldValue.attr("field");
        var fieldConfig  = beanInfo.fields[fieldName];
        if(fieldConfig.type == "array") {
          this.__onSaveArrayFieldValue(uiFieldValue);
        } else {
          this.__onSaveFieldValue(uiFieldValue);
        }
      }
    },

    __toggle: function(uiBean) {
      var beanState = this.__getBeanState(uiBean);
      if(beanState.editMode) this.__setViewMode(uiBean);
      else this.__setEditMode(uiBean);
    },

    __setViewMode: function(uiBean) {
      var beanInfo = this.__getBeanInfo();
      var beanState = this.__getBeanState(uiBean);
      if(beanState.editMode) this.commitChange(uiBean);
      var fieldBlks = uiBean.find('div[field]');
      fieldBlks.each(function(idx, ele) {
        widget.view.field($(ele), beanInfo, beanState);
      });
      beanState.editMode = false;
    },

    __setEditMode: function(uiBean) {
      var beanInfo = this.__getBeanInfo();
      var beanState = this.__getBeanState(uiBean);
      var fieldBlks = uiBean.find('div[field]');
      fieldBlks.each(function(idx, ele) {
        widget.edit.field($(ele), beanInfo, beanState);
      });
      beanState.editMode = true;
    },

    __createBeanState: function(bInfo, bean) { 
      var state    = { fields: {} };
      for(var key in bInfo.fields) {
        var value = util.reflect.getFieldValue(bean, key);
        var field = bInfo.fields[key];
        if(field.type == 'array')  {
          if(value == null) value = [] ;
          else value = value.slice();
        }
        state.fields[key] = { value: value, modified: false };
      }
      return state;
    },

    __onTriggerSaveFieldValue: function(triggerEle) {
      var uiFieldValue = triggerEle.closest('.field-value');
      this.__onSaveFieldValue(uiFieldValue);
    },

    __onSaveFieldValue: function(uiFieldValue) {
      var fieldInput = uiFieldValue.find('.field-input');
      var fieldName  = uiFieldValue.attr("field");

      var bean      = this.__getBean(uiFieldValue);
      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo  = this.__getBeanInfo();
      var fieldInfo = beanInfo.fields[fieldName];
      var value     = fieldInput.val();

      var validateResult = validator.validate(fieldInfo, value);
      if(validateResult.success) {
        var oldValue = beanState.fields[fieldName].value ;
        beanState.fields[fieldName].value = value;
        util.reflect.setFieldValue(bean, fieldName, value);
        widget.view.field(uiFieldValue, beanInfo, beanState);
        if(this.onFieldChange) this.onFieldChange(bean, fieldName, oldValue, value);
      } else {
        uiFieldValue.find('.ui-text-error').remove();
        uiFieldValue.append("<span class='ui-text-error'>" +  validateResult.err + "<span>");
      }
    },

    __onTriggerSaveArrayFieldValue: function(triggerEle) {
      var uiFieldValue = triggerEle.closest('.field-value');
      this.__onSaveArrayFieldValue(uiFieldValue);
    },

    __onSaveArrayFieldValue: function(uiFieldValue) {
      var fieldName =  uiFieldValue.attr('field');
      var fieldInput = uiFieldValue.find('.field-input');
      var value = [];
      fieldInput.each(function(idx, input) {
        var inputVal = $(input).val();
        value.push(inputVal);
      });
      var bean      = this.__getBean(uiFieldValue);
      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo = this.__getBeanInfo();
      var oldValue = beanState.fields[fieldName].value ;
      beanState.fields[fieldName].value = value;
      util.reflect.setFieldValue(bean, fieldName, value);
      widget.view.field(uiFieldValue, beanInfo, beanState);
      if(this.onFieldChange) this.onFieldChange(bean, fieldName, oldValue, value);
    },
  });

  return UIBeanEditor ;
});
