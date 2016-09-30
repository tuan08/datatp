define([
  'jquery', 'underscore', 'backbone',
  'ui/bean/UIBean',
  'ui/bean/UIArrayBean'
], function($, _, Backbone, UIBean, UIArrayBean) {

  var bean = {
    UIBean: UIBean,
    UIArrayBean: UIArrayBean
  };
  
  return bean ;
});
