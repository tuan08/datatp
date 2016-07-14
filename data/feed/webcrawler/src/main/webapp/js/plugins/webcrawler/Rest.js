define([
  'rest/Server'
], function(Server) {
  var server = new Server("http://localhost:8080");

  function SiteRest() {
    this.getSiteConfigs = function() {
      return server.restGET("/site/get-configs");
    };
  };

  function MasterRest(server) {
    this.server = server;
    
    this.getURLCommitInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/master/report/url-commit", {max: max});
    };

    this.getURLScheduleInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/master/report/url-schedule", {max: max});
    };
  };

  function FetcherRest(server) {
    this.server = server;
  };

  var Rest = {
    site:     new SiteRest(server),
    master:   new MasterRest(server),
    fetcher:  new FetcherRest(server),
  }
  return Rest ;
});
