define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITableTree'
], function($, _, Backbone, UITableTree) {

  var UIURLStructure = UITableTree.extend({
    label: "URL Structure",

    config: {
      bean: {
        label: 'URL Structure',
        fields: [
          { 
            field: "domain", label: "Domain", defaultValue: '', toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(bean)) ;
            }
          },
          { field: "directory", label: "Directory", toggled: true },
          { field: "urlInfo", label: "URL Info", toggled: true }
        ],
        actions:[
          {
            icon: "edit", label: "Mod",
            onClick: function(thisTable, row) { 
              thisTable.onEditBean(row) ;
            }
          }
        ]
      }
    },

    onInit: function(options) {
      var siteStructure = options.siteStructure; 
      var domainStructures = siteStructure.urlStructure.domainStructures;
      for (var name in domainStructures) {
         var domainStructure = domainStructures[name];
        this._addDomain(domainStructure);
      }
    },
    
    _addDomain: function(domainStructure) {
      var domain = { domain: domainStructure.domain } ;
      var domainNode = this.addNode(domain);
      domainNode.setCollapse(false);

      var directoryStructures = domainStructure.directoryStructure;
      for (var path in directoryStructures) {
        var directoryStructure = directoryStructures[path];
        this._addDirectory(domainNode, directoryStructure) ;
      }
    },

    _addDirectory: function(domainNode, directoryStructure) {
      var directoryNode = domainNode.addChild(
        { domain: "", directory: directoryStructure.directory }
      );
      directoryNode.setCollapse(false);

      var urlInfos = directoryStructure.urlInfos;
      for(var i = 0; i < urlInfos.length; i++) {
        var urlInfo = urlInfos[i]
        var urlInfoNode =  directoryNode.addChild(
          { domain: "", directory: '', urlInfo: urlInfo }
        );
      }
    }
  });
  
  return UIURLStructure ;
});
