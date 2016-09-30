define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/bean/bean',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UICollapsible,  bean, data) {
  var UISingleBeanDemo = bean.UIBean.extend({
    label: 'Single Bean Demo',

    config: {
      header: "UIBean Demo",
      width:  "400px",
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) { console.log('call save'); }
        }
      }
    },
    
    onInit: function(options) {
      this.set(data.BeanInfo, data.bean);
    }
  });

  var UIArrayBeanDemo = bean.UIArrayBean.extend({
    label: 'UIArrayBean Demo',

    config: {
      header: "UIArrayBean Demo",
      width:  "400px",
      actions: {
        save: { 
          label: "Save",
          onClick: function(thisUI) { console.log('call save'); }
        }
      }
    },
    
    onInit: function(options) {
      this.set(data.BeanInfo, data.beans);
    }
  });

  var UIArrayTableBeanDemo = bean.UIArrayBean.extend({
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
    }
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
      this.add(new UISingleBeanDemo()) ;
      this.add(new UIArrayBeanDemo()) ;
      this.add(new UIArrayTableBeanDemo()) ;
    }
  }) ;

  return new UIBeanDemo() ;
});
