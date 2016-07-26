define([
  'jquery'
], function($) {
  /**@type service.Server */
  Server = function(baseUrl) {
    this.baseUrl = baseUrl;

    /**@memberOf service.Server */
    this.syncGETResource = function(path, dataType) {
      path = this.baseUrl + path;
      var returnData = null ;
      $.ajax({ 
        type: "GET",
        dataType: dataType,
        url:  path,
        async: false ,
        error: function(data) {
          console.log(data) ;
          console.trace() ;
        },
        success: function(data) {  returnData = data ; }
      });
      return returnData ;
    };

    /**@memberOf service.Server */
    this.syncPOSTJson = function(path, dataObj) {
      path = this.baseUrl + path;
      var returnData = null ;
      $.ajax({ 
        async: false ,
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: path,
        data:  JSON.stringify(dataObj) ,
        error: function(data) {  
          console.log("Error:") ; 
          console.printJSON(data) ; 
        },
        success: function(data) {  
          returnData = data ; 
        }
      });
      return returnData ;
    };
    
    /**@memberOf service.Server */
    this.restGET = function(restPath, params) {
      restPath = this.baseUrl + restPath;
      var returnData = null ;
      $.ajax({ 
        type: "GET",
        dataType: "json",
        url: restPath,
        data: params ,
        async: false ,
        error: function(data) {  
          console.log("Error:") ; 
          console.log(data) ; 
        },
        success: function(data) {  returnData = data ; }
      });
      return returnData ;
    };

    /**@memberOf service.Server */
    this.restGETJsonpPush = function(restPath, obj) {
      restPath = this.baseUrl + restPath;
      var restPathWithJsonp = restPath + "?jsonp=" + encodeURIComponent(JSON.stringify(obj));
      var returnData = null ;
      $.ajax({ 
        type: "GET",
        dataType: "json",
        url: restPathWithJsonp,
        data: {} ,
        async: false ,
        error: function(data) {  
          console.log("Error:") ; 
          console.log(data) ; 
        },
        success: function(data) {  returnData = data ; }
      });
      return returnData ;
    };
    
    /**@memberOf service.Server */
    this.restPOST = function(path, params, crossDomain) {
      var returnData = null ;
      $.ajax({ 
        async: false ,
        type: "POST",
        crossDomain: crossDomain,
        dataType: "json",
        //contentType: "application/json; charset=utf-8",
        url: path,
        data:  JSON.stringify(params) ,

        error: function(data) {  
          console.debug("Error: \n" + JSON.stringify(data)) ; 
        },
        success: function(data) {  
          returnData = data ; 
        }
      });
      return returnData ;
    };
  };
  return Server ;
});
