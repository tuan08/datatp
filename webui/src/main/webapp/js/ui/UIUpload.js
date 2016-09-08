define([
  'jquery', 
  'underscore', 
  'backbone',
  'text!ui/UIUpload.jtpl'
], function($, _, Backbone, Template) {

  var UIUpload = Backbone.View.extend({
    initialize: function (options) {
      if(this.onInit) {
        this.onInit(options);
      }
    },

    _template: _.template(Template),

    render: function() {
      var params = {
        config: this.config
      };

      $(this.el).html(this._template(params));
      var fileInput = $(this.el).find("input");
      fileInput.css('opacity','0');
      var config = this.config;
      var thisUI = this;
      fileInput.change(function() {
        var form = fileInput.closest("form");
        var uploadStatus = form.find(".uploadStatus");
        var formData = new FormData();
        var files = fileInput[0].files;
        var filename = files[0].name;
        var filesize = files[0].size;
        formData.append("file", files[0]);
        $.ajax({
          url:      config.serviceUrl,
          data:     formData,
          type:     "POST",
          processData: false,      
          contentType: false,      
          success: function(data) {
            uploadStatus.html('<span>Uploaded '+ filename + '</span>');
            if(config.onSuccess) {
              config.onSuccess(thisUI, data);
            }
          },
          error : function(result){
            alert(JSON.stringify(result));
            if(config.onError) {
              config.onError(thisUI, result);
            }
          }
        });
        uploadStatus.html("<progress></progress>");
      });
    },

    events: {
      'click a.onSelect': 'onSelect'
    },

    onSelect: function(evt) {
      evt.preventDefault();
      var form = $(evt.target).closest("form");
      var fileInput = form.find("input");
      fileInput.trigger('click');
    }
  });
  
  return UIUpload ;
});
