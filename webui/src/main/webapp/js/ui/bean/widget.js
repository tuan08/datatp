define([
  'jquery', 'underscore', 'backbone',
  'util/util'
], function($, _, Backbone, util) {
  var readOnlyFieldValueTmpl = _.template( `
    <div class="ui-ib"><span><%=value%></span></div>
  `);

  var editableFieldValueTmpl = _.template( `
    <div class="ui-ib editable-field-value">
      <span><%=value%></span>
      <a class="ui-icon ui-icon-pencil" style="visibility: hidden"><span/></a>
    </div>
  `);

  var inputFieldValueTmpl = _.template( `
    <div class="ui-ib edit-field-value box-width-full">
       <input class="field-input" type="text" name="<%=fieldName%>" value="<%=value%>" autocomplete="<%=autocomplete%>" />
       <a class="ui-icon ui-icon-check"/>
    </div>
  `);

  var arrayInputFieldValueTmpl = _.template( `
    <div class="ui-ib edit-array-field-value box-width-full">
      <%for(var i = 0; i < value.length; i++) {%>
        <input class="field-input" type="text" name="<%=fieldName + '_' + i %>" value="<%=value[i]%>" autocomplete="<%=autocomplete%>" />
        <a class="ui-icon ui-icon-minus" idx="<%=i%>"/>
      <%}%>
      <a class="ui-icon ui-icon-check"/>
      <a class="ui-icon ui-icon-plus"/>
    </div>
  `);

  var selectInputFieldValueTmpl = _.template( `
    <div class="ui-ib edit-field-value box-width-full">
      <select class="field-input">
        <%var fieldValue = value != null ? value : field.defaultValue ; %>
        <%for(var i = 0; i < options.length ; i++) { %>
        <%  var selected = fieldValue == options[i].value ? 'selected' : '' ; %>
            <option value="<%=options[i].value%>" <%=selected%> >
              <%=options[i].label%>
            </option>
        <%}%>
      </select>
      <a class="ui-icon ui-icon-check"/>
    </div>
  `);

  var actionWidgetTmpl = _.template( `
    <div class="ui-ib box-width-full">
      <%for(var name in actions) {%>
        <%var action = actions[name]; %>
        <a class="ui-action onAction" name="<%=name%>"><%=action.label%></a>
      <%}%>
    </div>
  `);

  var toggleModeTmpl = _.template( `
    <span class="ui-state-default ui-corner-all"><a class="ui-ib ui-icon ui-icon-pencil onToggleMode" style="cursor: pointer"/></span>
  `);

  var widget = {
    readonly: {
      field: function(uiFieldValue, beanInfo, beanState) {
        var fieldName = uiFieldValue.attr("field");
        var value = beanState.fields[fieldName].value;
        uiFieldValue.html(readOnlyFieldValueTmpl({ value: value }));
      }
    },

    view: {
      field: function(uiFieldValue, beanInfo, beanState) {
        var fieldName = uiFieldValue.attr("field");
        var value = beanState.fields[fieldName].value;
        uiFieldValue.html(editableFieldValueTmpl({ value: value }));
      }
    },

    edit: {
      field: function(uiFieldValue, beanInfo, beanState) {
        var fieldName = uiFieldValue.attr("field");
        var value = beanState.fields[fieldName].value;
        var fieldInfo = beanInfo.fields[fieldName];
        var params = {
          fieldName: fieldName, field: fieldInfo,
          value: value,
          autocomplete: 'on'
        };
        if(fieldInfo.type == 'array') {
          uiFieldValue.html(arrayInputFieldValueTmpl(params));
        } else if(fieldInfo.type == 'select') {
          if(_.isArray(fieldInfo.options)) {
            params.options = fieldInfo.options;
          } else {
            params.options = fieldInfo.options(this);
          }
          uiFieldValue.html(selectInputFieldValueTmpl(params));
        } else {
          uiFieldValue.html(inputFieldValueTmpl(params));
        }
      },
    },
    actions: function(actionsBlk, actions) {
      if(actions) {
        actionsBlk.html(actionWidgetTmpl({ actions: actions }));
      } else {
        actionsBlk.empty();
      }
    },

    toggle: function(blk) { blk.html(toggleModeTmpl({})); }
  };

  return widget ;
});
