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
        restURL: "http://localhost:8080/crawler"
      },

      elasticsearch: {
        restURL: "http://localhost:9200"
      }
    }
  };

  return env
});

