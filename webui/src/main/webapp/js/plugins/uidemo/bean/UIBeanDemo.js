define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/bean/UIBean',
  'ui/bean/UIArrayBean',
  'ui/bean/UIComplexBean',
  'ui/bean/UIArrayComplexBean',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UICollapsible, UIBean, UIArrayBean, UIComplexBean, UIArrayComplexBean, data) {
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
  });

  var UIArrayBeanDemo = UIArrayBean.extend({
    label: 'UIArrayBean Demo',

    config: {
      header: "UIArrayBean Demo",
      width:  "600px",
      label: function(bean, idx) { return "bean " + idx},
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) { console.log('call save'); }
        }
      }
    },
    
    onInit: function(options) {
      this.set(data.BeanInfo, data.beans);
    },

    createDefaultBean: function() { return data.createBean("New Bean"); }
  });

  var UIArrayTableBeanDemo = UIArrayBean.extend({
    label: 'UIArrayBean Table Demo',

    config: {
      header: "UIArrayBean Demo",
      width:  "100%",
      layout: 'table',
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) { console.log('call save'); }
        }
      }
    },
    
    onInit: function(options) {
      this.set(data.BeanInfo, data.beans);
    },

    createDefaultBean: function() { return data.createBean("New Bean"); }
  });

  var UIArrayContactBean = UIArrayBean.extend({
    label: 'Contacts',

    config: {
      header: "Contacts"
    },
    
    createDefaultBean: function() { return data.createContactBean("New Bean"); }
  });

  var UIComplexBeanDemo = UIComplexBean.extend({
    label: 'UIComplexBean Demo',

    _template: _.template(`
      <div style="border: 1px solid #ddd; margin-top: 10px">
        <h6 class="box-border-bottom" style="padding: 5px">Complex Bean Mapping Demo</h6>
        <div class="box-display-ib-left-right" style="padding: 10px">
          <div class="box-display-ib box-valign-top" style="width: 40%" name="UISingleBeanDemo"/>
          <div class="box-display-ib box-valign-top" style="width: calc(60% - 20px)" name="UIArrayBeanDemo"/>
        </div>
      </div>
    `),
    
    set: function(bean) {
      var uiSingle = new UISingleBeanDemo().configure({ width: "100%"}).set(data.BeanInfo, bean);
      this.__setUIComponent("UISingleBeanDemo", uiSingle) ;

      var uiArray = new UIArrayContactBean().configure({ width: "100%"} ).set(data.ContactBeanInfo, bean.contacts);
      this.__setUIComponent("UIArrayBeanDemo", uiArray) ;
      return this;
    }
  });

  var UIArrayComplexBeanDemo = UIArrayComplexBean.extend({
    label: 'Array UIComplexBean Demo',
    
    config: {
      UIComplexBean: UIComplexBeanDemo
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
      this.add(new UIArrayBeanDemo()) ;
      this.add(new UIArrayTableBeanDemo()) ;
      this.add(new UIComplexBeanDemo().set(data.createComplexBean("A Complex Bean"))) ;
      this.add(new UIArrayComplexBeanDemo().set(data.createComplexBeans("Array Complex Bean", 5))) ;
    }
  }) ;

  return new UIBeanDemo() ;
});
