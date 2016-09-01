define([
  'rest/Server'
], function(Server) {
  var server = new Server("http://localhost:8080/crawler");

  function SiteRest() {
    this.getSiteConfigs = function() {
      return server.restGET("/site/configs");
    };

    this.save = function(siteConfig) {
      return server.syncPOSTJson("/site/save", siteConfig);
    };

    this.getAnalyzedURLSiteStructure = function(siteConfig, maxDownload, forceNew) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: forceNew } ;
      return server.syncPOSTJson("/site/analyzed-site-url", params);
    };

    this.reanalyseURLSiteStructure = function(siteConfig, maxDownload) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: false } ;
      return server.syncPOSTJson("/site/reanalyse-site-url", params);
    };

    this.recrawlURLSiteStructure = function(siteConfig, maxDownload) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: false } ;
      return server.syncPOSTJson("/site/recrawl-site-url", params);
    };

    this.getAnalyzedURLData = function(url) {
      var params = { url: url } ;
      return server.restGET("/site/analyzed-url-data", params);
    };
  };

  function SchedulerRest(server) {
    this.server = server;
    
    this.getURLCommitInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/scheduler/report/url-commit", {max: max});
    };

    this.getURLScheduleInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/scheduler/report/url-schedule", {max: max});
    };
  };

  function FetcherRest(server) {
    this.server = server;
  };

  var Rest = {
    site:       new SiteRest(server),
    scheduler:  new SchedulerRest(server),
    fetcher:    new FetcherRest(server),
  }
  return Rest ;
});
