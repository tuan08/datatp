define([
], function($, _, Backbone) {
  var BeanInfo = {
    label: 'Bean',
    fields: {
      "category": { label: "Category", required: true },
      "type":     { label: "Type", required: true },
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

  var BarChartModel = {
    label: 'BarChart Model',
    fields: {
      "timestamp": { label: "Timestamp", datatype: "datetime", required: true },
      "bar1": { label: "Bar 1", datatype: "integer", required: true },
      "bar2": { label: "Bar 2", datatype: "integer", required: true },
      "bar3": { label: "Bar 3", datatype: "integer", required: true },
      "stream1": { label: "Stream 1", datatype: "number", required: true },
      "stream2": { label: "Stream 2", datatype: "number", required: true },
      "stream3": { label: "Stream 3", datatype: "number", required: true },
    }
  };



  function createBean(seed) {
    var randomCategory = function() {
      var num = Math.random();
      if(num < 0.3) return "category-1";
      if(num < 0.6) return "category-2";
      return "category-3";
    };

    var randomType =  function() {
      var num = Math.random();
      if(num < 0.3) return "type-2";
      if(num < 0.6) return "type-3";
      return "type-1";
    };

    var bean = {
      category: randomCategory(),
      type:     randomType(),
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

  function createBarChart(size) {
    var currTimeMillis = new Date().getTime();
    var beans = [] ;
    for(var i = 0; i < size; i++) {
      var bar3 = Math.floor((Math.random() * 100) + 1) ;
      var BarChart = { 
        timestamp: currTimeMillis + (i * 60 * 1000), 
        bar1:    Math.floor((Math.random() * 100) + 1),
        bar2:    Math.floor((Math.random() * 100) + 1),
        bar3:    Math.floor((Math.random() * 100) + 1),
        stream1: Math.floor((Math.random() * 100) + 201),
        stream2: Math.floor((Math.random() * 100) + 301),
        stream3: Math.floor((Math.random() * 100) + 401),
      } ;
      beans.push(BarChart);
    }
    return beans;
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
    createContactBean:  createContactBean,

    chart: {
      BarChart: {
        Model:  BarChartModel,
        create: createBarChart
      }
    }
  }

  return data ;
});
