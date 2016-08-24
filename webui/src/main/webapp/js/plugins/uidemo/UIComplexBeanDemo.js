define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContainer',
  'ui/UIBean'
], function($, _, Backbone, UIContainer, UIBean) {
  
  var UIGenericBean = UIBean.extend({
    label: "Generic Bean",
    config: {
      beans: {
        generic: {
          name: 'bean', label: 'Bean',
          fields: [
            {
              field:  "selectOption", label: "Select Option",
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'Option 1', value: 'opt1' },
                    { label: 'Option 2', value: 'opt2' }
                  ];
                  return options ;
                }
              }
            },
            { 
              field: "input",   label: "Input", required: true,  
              validator: { name: 'empty', errorMsg: "custom error message" } 
            }
          ],
          edit: {
            disable: false , 
            actions: [ ],
          },
          view: {
            actions: [ ]
          }
        }
      }
    }
  });

  var UIContactBean = UIBean.extend({
    label: "Contacts",
    config: {
      type: 'array',
      beans: {
        bean: {
          label: 'Contact',
          fields: [
            { field: "name",   label: "Name" },
            { field: "email",  label: "Email", multiple: true }
          ]
        },
      }
    }
  });

  var UIComplexBeanDemo = UIContainer.extend({
    label: "UIComplexBeanDemo", 
    config: {
      actions: [
        { 
          action: "back", label: "Back",
          onClick: function(thisUI) {
            console.log("on click back") ;
          }
        },
        {
          action: "save", label: "Save",
          onClick: function(thisUI) {
            console.log("on click save") ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      var bean = {
        option: 'opt2' ,
        input: "Input Field",
        contacts: [
          { name: "Tuan", email: ["tuan@localhost"]}
        ]
      };

      var uiGenericBean = new UIGenericBean() ;
      uiGenericBean.bind('generic', bean, true) ;
      this.add(uiGenericBean) ;

      var uiContactBean = new UIContactBean();
      uiContactBean.bindArray('bean', bean.contacts) ;
      this.add(uiContactBean) ;
    }
  }) ;
  
  return new UIComplexBeanDemo() ;
});
