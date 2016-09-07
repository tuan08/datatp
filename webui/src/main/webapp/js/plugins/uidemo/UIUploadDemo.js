define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUpload'
], function($, _, Backbone, UIUpload) {
  var UIUploadDemo = UIUpload.extend({
    label: 'Upload Demo',

    config: {
      label: "Upload demo",
      serviceUrl: "/crawler/site/upload",
      onSuccess: function(thisUI) {
        console.log("on upload success");
      },

      onError: function(thisUI) {
        console.log("on upload error");
      }
    },
    
    onInit: function(options) {
    }
  });

  return new UIUploadDemo({}) ;
});
