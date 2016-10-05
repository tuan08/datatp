define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UITemplateLayout'
], function($, _, Backbone, UIContent, UITemplateLayout) {
  var Template = `
    <div>
      <div name="north" />
      <div>
         <div style="width: 50%; display: inline-block; width: calc(50% - 5px)" name="left" />
         <div style="width: 50%; display: inline-block; width: calc(50% - 5px)" name="right" />
      </div>
    <div>
  `;

  var UITemplateLayoutDemo = UITemplateLayout.extend({
    label: 'Templae Layout Demo',

    _template: _.template(Template),
    
    onInit: function(options) {
      this.__setUIComponent('north', new UIContent({ content: 'North Panel Content'}));
      this.__setUIComponent('left', new UIContent({ content: 'Left Panel Content<br/>...'}));
      this.__setUIComponent('right', new UIContent({ content: 'Right Panel Content'}));
    }
  });

  return new UITemplateLayoutDemo() ;
});
