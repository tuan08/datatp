define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/bean/UIBean',
  'ui/bean/UIBeanArray',
  'ui/bean/UIBeanComplex',
  'ui/bean/UIBeanComplexArray',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UICollapsible, UIBean, UIBeanArray, UIBeanComplex, UIBeanComplexArray, data) {
  var UISingleBeanDemo = UIBean.extend({
    label: 'Single Bean Demo',

    config: {
      header: "UIBean Demo",
      width:  "600px",
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) { console.log('call save'); }
        }
      }
    },

    onFieldChange: function(bean, fieldName, oldValue, newValue) {
      console.log('field change: field = ' + fieldName + ", old value = " + oldValue + ", new value = " + newValue);
      console.printJSON(bean);
    }
  });

  var UIBeanArrayDemo = UIBeanArray.extend({
    label: 'UIBeanArray Demo',

    config: {
      header: "UIBeanArray Demo",
      width:  "600px",
      label: function(bean, idx) { return "bean[" + idx + "]"},
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI, beanState, bean) { 
            console.log('call save(tab)'); 
            console.printJSON(beanState);
            console.printJSON(bean);
          }
        }
      }
    },
    
    onInit: function(options) {
      var actionTest = function(uiArrayBean) {
      };
      this.set(data.BeanInfo, data.beans);
    },

    createDefaultBean: function() { return data.createBean("New Bean"); }
  });

  var UIArrayTableBeanDemo = UIBeanArray.extend({
    label: 'UIBeanArray Table Demo',

    config: {
      header: "UIBeanArray Demo",
      width:  "100%",
      layout: 'table',
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI, beanState, bean) { 
            console.printJSON(beanState);
            console.printJSON(bean);
          }
        }
      }
    },
    
    onInit: function(options) {
      this.set(data.BeanInfo, data.beans);
    },

    createDefaultBean: function() { return data.createBean("New Bean"); }
  });

  var UIArrayContactBean = UIBeanArray.extend({
    label: 'Contacts',

    config: {
      header: "Contacts"
    },
    
    createDefaultBean: function() { return data.createContactBean("New Bean"); }
  });

  var UIBeanComplexDemo = UIBeanComplex.extend({
    label: 'UIBeanComplex Demo',

    _template: _.template(`
      <div style="border: 1px solid #ddd; margin-top: 10px">
        <h6 class="box-border-bottom" style="padding: 5px">Complex Bean Mapping Demo</h6>
        <div class="box-display-ib-left-right" style="padding: 10px">
          <div class="box-display-ib box-valign-top" style="width: 40%" name="UISingleBeanDemo"/>
          <div class="box-display-ib box-valign-top" style="width: calc(60% - 20px)" name="UIBeanArrayDemo"/>
        </div>
      </div>
    `),

    getBean: function() {
    },
    
    set: function(bean) {
      var uiSingle = new UISingleBeanDemo().configure({ width: "100%"}).set(data.BeanInfo, bean);
      this.__setUIComponent("UISingleBeanDemo", uiSingle) ;

      var uiArray = new UIArrayContactBean().configure({ width: "100%"} ).set(data.ContactBeanInfo, bean.contacts);
      this.__setUIComponent("UIBeanArrayDemo", uiArray) ;
      return this;
    }
  });

  var UIBeanComplexArrayDemo = UIBeanComplexArray.extend({
    label: 'Array UIBeanComplex Demo',
    
    config: {
      UIBeanComplex: UIBeanComplexDemo
    },

    createDefaultBean: function() { return data.createComplexBean("New Bean"); }
  });

  var UIBeanDemo = UICollapsible.extend({
    label: "Bean Demo", 
    config: {
      actions: [
        {
          action: "save", label: "Save",
          onClick: function(thisUI) {
            console.log("on click save") ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      this.add(new UISingleBeanDemo().set(data.BeanInfo, data.bean));
      this.add(new UIBeanArrayDemo()) ;
      this.add(new UIArrayTableBeanDemo()) ;
      this.add(new UIBeanComplexDemo().set(data.createComplexBean("A Complex Bean"))) ;
      this.add(new UIBeanComplexArrayDemo().set(data.createComplexBeans("Array Complex Bean", 5))) ;
    }
  }) ;

  return new UIBeanDemo() ;
});
