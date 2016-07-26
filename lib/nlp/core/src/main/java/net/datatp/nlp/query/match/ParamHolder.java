package net.datatp.nlp.query.match;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.util.text.StringUtil;

public class ParamHolder {
  private String name ;
  private String params ;
  private Map<String, String[]> fields ;

  public ParamHolder(String expression) {
    fields = new HashMap<String, String[]>() ;
    expression = expression.trim() ;
    int tagSeparator = expression.indexOf('{') ;
    if(tagSeparator > 0) {
      this.name = expression.substring(0, tagSeparator).trim() ;
      String nameValues = expression.substring(tagSeparator + 1, expression.length() - 1) ;
      List<String> nameValueHolder = StringUtil.split(nameValues, '|') ;
      for(int i = 0 ; i < nameValueHolder.size(); i++) {
        String pvstring = nameValueHolder.get(i) ;
        int idx = pvstring.indexOf('=') ;
        if(idx < 0) return ;
        String fname = pvstring.substring(0, idx).trim() ;
        String values = pvstring.substring(idx + 1).trim() ;
        if(values.startsWith("\"\"") && values.endsWith("\"\"")) {
          values = values.substring(2, values.length() - 2) ;
          fields.put(fname, new String[] {values}) ;
        } else {
          String[] fvalue = values.split(",") ;
          for(int j = 0; j < fvalue.length; j++) fvalue[j] = fvalue[j].trim() ;
          fields.put(fname, fvalue) ;
        }
      }
    } else {
      this.name = expression ;
    }
  }

  public String getName() { return this.name ; }

  public String[] getFieldValue(String name) { return fields.get(name) ; }

  public String getFirstFieldValue(String name) { 
    String[] value = fields.get(name) ; 
    if(value == null) return null ;
    return value[0] ;
  }

  public void setFieldValue(String name, String value) { 
    fields.put(name, new String[] {value}) ;
  }
}