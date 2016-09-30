define([
], function($, _, Backbone) {
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
          { label: 'Option 1 long data', value: 'opt1' },
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

  function createBean(seed) {
    var bean = {
      input: seed,
      email: "name@xyz.com",
      intInput: 20,
      arrayInput: ["input 1", "input 2"],
      select: 'opt2',
      customSelect: 'opt2',
      nested: {
        input: "A Input",
      }
    };
    return bean;
  }
   
  function createBeans(seed, size) {
    var beans = [];
    for(var i = 0; i < size; i++) {
      beans.push(createBean(seed + " " + i));
    }
    return beans;
  }

  var data = {
    BeanInfo: BeanInfo,
    bean:     createBean("Sample single bean"),
    beans:    createBeans("Sample array bean", 5)
  }

  return data ;
});
