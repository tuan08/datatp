define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITable'
], function($, _, Backbone,  UITable) {
  var UIURLPattern = UITable.extend({
    label: "URL Pattern",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "onNew", icon: "add", label: "New", 
              onClick: function(thisTable) { 
                thisTable.onAddBean(thisTable.onSaveBeanCallback) ; 
              } 
            }
          ]
        }
      },
      
      bean: {
        label: 'URL Pattern',
        fields: [
          {
            field:  "type", label: "Type", defaultValue: 'ignore', toggled: true, filterable: true,
            select: {
              getOptions: function(field, bean) {
                var options = [
                  { label: 'ignore',  value: 'ignore' },
                  { label: 'list',    value: 'list' },
                  { label: 'detail',  value: 'detail' },
                  { label: 'unkwnon', value: 'unknown' },
                ];
                return options ;
              }
            }
          },
          { field: "pattern",  label: "Pattern", multiple: true, toggled: true, filterable: true }
        ],
        actions:[
          {
            icon: "edit", label: "Edit",
            onClick: function(thisTable, row) { 
              thisTable.onEditBean(row, thisTable.onSaveBeanCallback) ;
            }
          },
          {
            icon: "delete", label: "Del",
            onClick: function(thisTable, row) { 
              thisTable.onDeleteBeanCallback(thisTable, row);
            }
          }
        ]
      }
    },

    onSaveBeanCallback: function(thisTable, row, bean) {
      var beans = thisTable.commitChange();
    },

    onDeleteBeanCallback: function(thisTable, row) {
      thisTable.removeItemOnCurrentPage(row);
    }
  });

  return UIURLPattern ;
});
