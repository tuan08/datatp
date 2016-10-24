define([
], function() {
  env = {
    user: {
      userId:    "datatp",
      visitorId: "",
      sessionId: "",
    },

    service: {
      crawler: {
        restURL: "http://192.168.1.14:8080/crawler"
      },

      elasticsearch: {
        restURL: "http://192.168.1.14:9200"
      }
    }
  };

  return env
});

