define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UIBorderLayout'
], function($, _, Backbone, UIContent, UIBorderLayout) {
  var UIBorderLayoutDemo = UIBorderLayout.extend({
    label: 'BorderLayout Demo',

    config: {
    },
    
    onInit: function(options) {
      var northConfig = { height: "50px" };
      this.set('north', new UIContent({ content: 'North Panel Content'}), northConfig);

      var shouthConfig = {};
      this.set('shouth', new UIContent({ content: 'Shouth Panel Content'}), shouthConfig);

      var westConfig = { width: "250px"};
      this.set('west', new UIContent({ content: 'West Panel Content'}), westConfig);

      var eastConfig = { width: "150px"};
      this.set('east', new UIContent({ content: 'East Panel Content'}), eastConfig);

      var centerConfig = {};
      this.set('center', new UIContent({ content: 'Center Panel Content'}), centerConfig);
    }
  });

  return new UIBorderLayoutDemo() ;
});
