define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/bean'
], function($, _, Backbone, bean) {

  var BeanInfo = {
    label: 'Bean',
    fields: {
      "input": { 
        label: "Input", required: true,  validator: { name: 'empty', errorMsg: "Input cannot be empty" } 
      },
      "email": { 
        label: "Email", required: true, validator: { name: 'email'} 
      },
      "intInput": { 
        label: "Integer Input", defaultValue: 0,
        validator: { name: 'integer', from: 0, to: 100, errorMsg: "Expect an integer from 0 to 100" }
      },

      "select": {
        label: "Select", type: 'select',
        options: [
          { label: 'Option 1', value: 'opt1' },
          { label: 'Option 2', value: 'opt2' }
        ]
      },

      "customSelect": {
        label: "Custom", type: 'select',
        options: function(thisUI) {
          var options = [
            { label: 'Option 1', value: 'opt1' },
            { label: 'Option 2', value: 'opt2' }
          ];
          return options;
        }
      },

      "arrayInput": { 
        label: "Array Input", type: 'array',
      },

      "nested.input": { 
        label: "Nested Input", required: true,  
        validator: { name: 'empty', errorMsg: "custom error message" } 
      },
    }
  };

  var UIBeanDemo = bean.UIBean.extend({
    label: 'Upload Demo',

    config: {
      header: "UIBean Demo",
      width:  "400px",
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) {
            console.log('call save');
          }
        }
      }
    },
    
    onInit: function(options) {
      this.setBeanInfo(BeanInfo);
      var bean = {
        input: "A Input",
        email: "name@xyz.com",
        intInput: 20,
        arrayInput: ["input 1", "input 2"],
        select: 'opt2',
        customSelect: 'opt2',
        nested: {
          input: "A Input",
        }
      };
      this.setBean(bean);
    }
  });

  return new UIBeanDemo() ;
});
