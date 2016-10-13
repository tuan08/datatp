define([
  'env',
  'rest/Server'
], function(env, Server) {
  var server = new Server(env.service.elasticsearch.restURL);

  function Cluster(server) {
    this.getStatus = function() { return server.restGET("/_cluster/health?pretty=true", {}); };

    this.getNodes = function() { return server.restGET("/_nodes?pretty=true", {}); };
  };

  function Indices(server) {
    this.getStats = function() { return server.restGET("/_stats?pretty=true", {}); };
  };

  var Rest = {
    cluster:  new Cluster(server),
    indices:  new Indices(server)
  }
  return Rest ;
});
