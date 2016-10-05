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

    onFieldInputEnter: function(evt) { if(evt.keyCode == 13) this.__onSaveFieldValue($(evt.target)); },
    onFieldInputSave: function(evt) { this.__onSaveFieldValue($(evt.target)); },

    onFieldArrayInputEnter: function(evt) { 
      if(evt.keyCode == 13) this.__onSaveArrayFieldValue($(evt.target)); 
    },

    onFieldArrayInputSave: function(evt) { 
      this.__onSaveArrayFieldValue($(evt.target)); 
    },

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


    __toggle: function(uiBean) {
      var beanInfo = this.__getBeanInfo();
      var beanState = this.__getBeanState(uiBean);
      var fieldBlks = uiBean.find('div[field]');
      var editMode = beanState.editMode;
      fieldBlks.each(function(idx, ele) {
       if(editMode) widget.view.field($(ele), beanInfo, beanState);
       else widget.edit.field($(ele), beanInfo, beanState);
      });
      beanState.editMode = !editMode;
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

    __onSaveFieldValue: function(triggerEle) {
      var uiFieldValue = triggerEle.closest('.field-value');
      var fieldInput = uiFieldValue.find('.field-input');
      var fieldName  = uiFieldValue.attr("field");

      var bean      = this.__getBean(uiFieldValue);
      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo  = this.__getBeanInfo();
      var fieldInfo = beanInfo.fields[fieldName];
      var value     = fieldInput.val();

      var validateResult = validator.validate(fieldInfo, value);
      if(validateResult.success) {
        beanState.fields[fieldName].value = value;
        widget.view.field(uiFieldValue, beanInfo, beanState);
      } else {
        uiFieldValue.find('.ui-text-error').remove();
        uiFieldValue.append("<span class='ui-text-error'>" +  validateResult.err + "<span>");
      }
    },

    __onSaveArrayFieldValue: function(triggerEle) {
      var uiFieldValue = triggerEle.closest('.field-value');
      var fieldName =  uiFieldValue.attr('field');
      var fieldInput = uiFieldValue.find('.field-input');
      var value = [];
      fieldInput.each(function(idx, input) {
        var inputVal = $(input).val();
        value.push(inputVal);
      });
      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo = this.__getBeanInfo();
      beanState.fields[fieldName].value = value;
      widget.view.field(uiFieldValue, beanInfo, beanState);
    },
  });

  return UIBeanEditor ;
});
