define([
], function() {
  //var httpServer = "http://192.168.1.15" ;
  var httpServer = window.location.origin;
  var esHost     = window.location.hostname;

  env = {
    user: {
      userId:    "datatp",
      visitorId: "",
      sessionId: "",
    },

    service: {
      crawler: {
        restURL: httpServer + "/crawler"
      },

      elasticsearch: {
        restURL:  "http://" + esHost + ":9200"
      }
    }
  };

  return env
});

