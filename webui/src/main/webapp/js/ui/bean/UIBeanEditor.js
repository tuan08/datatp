define([
  'jquery', 'underscore',
  'ui/UIView',
  "ui/bean/validator",
  "ui/bean/widget",
  'util/util',
], function($, _, UIView, validator, widget, util) {

  function BeanState(bInfo, bean) {
    this.bean   = bean;
    this.fields = {};

    for(var key in bInfo.fields) {
      var value = util.reflect.getFieldValue(bean, key);
      var field = bInfo.fields[key];
      if(field.type == 'array')  {
        if(value == null) value = [] ;
        else value = value.slice();
      }
      this.fields[key] = { value: value, modified: false };
    };

    this.getBean = function() { return this.bean; };

    this.modify = function(key, value) { 
      var oldVal = this.fields[key].value;
      this.fields[key].value = value;
      this.fields[key].modified = true;
      return oldVal;
    };

    this.addToArray = function(key, value) { 
      this.fields[key].value.push(value);
      this.fields[key].modified = true;
    };

    this.commitChange = function() { 
      for(var key in this.fields) {
        var field = this.fields[key];
        util.reflect.setFieldValue(this.bean, key, field.value);
        field.modified = false;
      }
    };

    this.getBeanWithCommitChange = function() { 
      this.commitChange();
      return this.bean; 
    };
  }

  var UIBeanEditor = UIView.extend({
    autoCommit: true,

    setAutoCommit: function(bool) { this.autoCommit = bool; },

    __getBean: function(fv) { throw new Error("this method need to override") },

    __getBeanInfo: function() { throw new Error("this method need to override") },


    UIBeanEditorEvents: {
      //Handle by UIBeanEditor
      "mouseover .editable-field-value": "onMouseOverEditableFieldValue",
      "mouseout  .editable-field-value": "onMouseOutEditableFieldValue",
      "click     .editable-field-value": "onClickEditableFieldValue",

      'click      .edit-field-value .ui-icon-check': 'onFieldInputSave',
      'keydown    .edit-field-value .field-input':   'onFieldInputEnter',

      'click      .edit-array-field-value .ui-icon-check': 'onFieldArrayInputSave',
      'click      .edit-array-field-value .ui-icon-minus': 'onFieldArrayInputRemove',
      'click      .edit-array-field-value .ui-icon-plus':  'onFieldArrayInputAdd',
      'keydown    .edit-array-field-value .field-input':   'onFieldArrayInputEnter',
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

    __commitUIBeanChange: function(uiBean) {
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
      if(beanState.editMode) this.__commitUIBeanChange(uiBean);
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

    __createBeanState: function(bInfo, bean) { return new BeanState(bInfo, bean); },

    __onTriggerSaveFieldValue: function(triggerEle) {
      var uiFieldValue = triggerEle.closest('.field-value');
      this.__onSaveFieldValue(uiFieldValue);
    },

    __onSaveFieldValue: function(uiFieldValue) {
      var fieldInput = uiFieldValue.find('.field-input');
      var fieldName  = uiFieldValue.attr("field");

      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo  = this.__getBeanInfo();
      var fieldInfo = beanInfo.fields[fieldName];
      var value     = fieldInput.val();

      var validateResult = validator.validate(fieldInfo, value);
      if(validateResult.success) {
        var oldValue = beanState.modify(fieldName, value);
        widget.view.field(uiFieldValue, beanInfo, beanState);
        if(this.autoCommit) {
          beanState.commitChange();
        } else {
          if(this.onFieldChange) this.onFieldChange(beanState, fieldName, oldValue, value);
          else throw new Error("This UIBean is not set to autocommit, need to implement method onFieldChange");
        }
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
      var beanState = this.__getBeanState(uiFieldValue);
      var beanInfo = this.__getBeanInfo();
      var oldValue = beanState.modify(fieldName, value) ;
      //util.reflect.setFieldValue(bean, fieldName, value);
      widget.view.field(uiFieldValue, beanInfo, beanState);
      if(this.autoCommit) {
        beanState.commitChange();
      } else {
        if(this.onFieldChange) this.onFieldChange(beanState, fieldName, oldValue, value);
        else throw new Error("This UIBean is not set to autocommit, need to implement method onFieldChange");
      }
    },
  });

  return UIBeanEditor ;
});
