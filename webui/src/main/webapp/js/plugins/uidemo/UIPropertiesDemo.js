define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIProperties'
], function($, _, Backbone, UIProperties) {
  var UIPropertiesDemo = UIProperties.extend({
    label: "UIProperties Demo", 

    config: {
      header: "UI Properties Demo",
      width:  "600px"
    },
    
    onInit: function(options) {
      var bean = {
        "key1": 'Value 1' ,
        "a text": 'This is a long text' ,
        "number": 3.45 ,
        "boolean": true ,
        "array": ["value 1", "value 2"],
      };
      this.setBean(bean);
    }
  }) ;
  
  return new UIPropertiesDemo() ;
});
