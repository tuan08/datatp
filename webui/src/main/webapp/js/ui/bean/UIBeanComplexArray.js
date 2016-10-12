define([
  'jquery', 'underscore', 'backbone'
], function($, _, Backbone) {
  var Template = `
    <%var width = config.width ? config.width : "100%"; %>
    <div class="ui-beans" style="padding: 2px; width: <%=width%>">
      <ul class="ui-tabs">
        <%for(var i = 0; i < state.size; i++) { %>
        <%  var label  = "bean " + i; %>
        <%  if(config.label) label = config.label(i); %>
        <%  var active = i == state.select ? "active" : ""; %>
            <li class="<%=active%>" beanIdx="<%=i%>">
              <a class="onSelectUIBeanComplex"><%=label%></a>
              <a class="remove">x</a>
            </li>
        <%}%>
        <li><a class="add">+</a></li>
      </ul>
      
      <div class="ui-tab-contents">
        <%for(var i = 0; i < state.size; i++) { %>
          <%var display = i == state.select ? "block" : "none"; %>
          <div class="ui-tab-content" beanIdx="<%=i%>" style="display: <%=display%>"></div>
        <%}%>
      </div>
    </div>
  
  `;

  var UIBeanComplexArray = Backbone.View.extend({
    initialize: function (options) {
      if(!this.config) this.config = { };
      //clone config to isolate the modification
      var newConfig = {} ;
      $.extend(newConfig, this.config);
      this.config = newConfig;

      if(this.onInit) this.onInit(options);
    },

    configure: function(newConfig) { 
      $.extend(this.config, newConfig); 
      return this;
    },

    set: function(beans) { 
      this.uiComplexBeans = [];
      for(var i = 0; i < beans.length; i++) {
        var uiComplexBean = new this.config.UIBeanComplex();
        uiComplexBean.set(beans[i]);
        this.uiComplexBeans.push(uiComplexBean);
      }
      this.state = { size: beans.length, select: 0 }
      return this;
    },

    _template: _.template(Template),

    render: function() {
      var params = { config: this.config, state: this.state };
      $(this.el).html(this._template(params));

      var uiTabContents = this.$('.ui-tab-content') ;
      for(var i = 0; i < uiTabContents.length; i++) {
        var uiTabContent = $(uiTabContents[i]) ;
        var idx = parseInt(uiTabContent.attr("beanIdx"));
        this.uiComplexBeans[idx].setElement(uiTabContent).render();
      }
    },
    
    events: {
      'click      .onSelectUIBeanComplex' : 'onSelect',
      'click      .add' :       'onAdd',
      'click      .remove' :    'onRemove',
    },

    onSelect: function(evt) {
      var uiTab = $(evt.target).closest("[beanIdx]");
      var beanIdx = parseInt(uiTab.attr("beanIdx"));

      var uiActiveTab  = $(evt.target).closest(".ui-tabs").find("li.active").first(); 
      uiActiveTab.removeClass("active");

      uiTab.addClass("active");

      var uiTabContents = $(this.el).find(".ui-tab-contents").first();
      uiTabContents.children("[beanIdx=" + this.state.select + "]").css("display", "none");
      uiTabContents.children("[beanIdx=" + beanIdx + "]").css("display", "block");
      this.state.select = beanIdx;
    },

    onAdd: function(evt) {
      console.log('add');
      if(!this.createDefaultBean) return ;
      var uiComplexBean = new this.config.UIBeanComplex();
      var bean = this.createDefaultBean()
      uiComplexBean.set(bean);
      this.uiComplexBeans.push(uiComplexBean);
      this.state.size +=1;
      this.state.select = this.uiComplexBeans.length - 1;
      this.render();
    },

    onRemove: function(evt) {
      var uiTab = $(evt.target).closest("[beanIdx]");
      var beanIdx = parseInt(uiTab.attr("beanIdx"));
      this.uiComplexBeans.splice(beanIdx, 1);
      this.state.size = this.uiComplexBeans.length;
      this.state.select = beanIdx - 1;
      if(this.state.select < 0) this.state.select = 0;
      this.render();
    }
  });

  return UIBeanComplexArray ;
});
