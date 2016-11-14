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
        restURL: "http://192.168.1.18:8080/crawler"
      },

      elasticsearch: {
        restURL: "http://192.168.1.18:9200"
      }
    }
  };

  return env
});

