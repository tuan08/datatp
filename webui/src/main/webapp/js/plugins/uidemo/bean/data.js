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

      "textarea": { 
        label: "textarea", type: "textarea", required: true,  
        validator: { name: 'empty', errorMsg: "Textarea cannot be empty" } 
      },
    }
  };

  var ContactBeanInfo = {
    label: 'Contact Bean Info',
    fields: {
      "name": { 
        label: "Name", required: true,  validator: { name: 'empty', errorMsg: "Input cannot be empty" } 
      },
      "email": { 
        label: "Email", required: true, validator: { name: 'email'} 
      }
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
      },
      textarea: "Textarea sample",
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

  function createComplexBean(seed) {
    var bean = createBean(seed);
    bean.contacts = [];
    for(var i = 0; i < 5; i++) {
      bean.contacts[i] = { name: "name " + i, email: "contact-" + i + "@xyz.com" };
    }
    return bean;
  }

  function createComplexBeans(seed, size) {
    var beans = [];
    for(var i = 0; i < size; i++) {
      beans.push(createComplexBean(seed + " " + i));
    }
    return beans;
  }

  function createContactBean(name) {
    return { name: name };
  }

  var data = {
    BeanInfo:           BeanInfo,
    ContactBeanInfo:    ContactBeanInfo,
    bean:               createBean("Sample single bean"),
    beans:              createBeans("Sample array bean", 5),
    createBean:         createBean,
    createBeans:        createBeans,
    createComplexBean:  createComplexBean,
    createComplexBeans: createComplexBeans,
    createContactBean:  createContactBean
  }

  return data ;
});
