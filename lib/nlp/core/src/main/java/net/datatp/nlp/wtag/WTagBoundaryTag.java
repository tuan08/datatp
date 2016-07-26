package net.datatp.nlp.wtag;

import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagBoundaryTag extends TokenTag {
  final static public String TYPE = "boundary" ;
  protected String[] feature ;

  public WTagBoundaryTag(String[] feature) {
    this.feature = feature ;
  }

  public WTagBoundaryTag(String feature) {
    this.feature = new String[] {feature} ;
  }

  public String[] getFeatures() { return this.feature ; }

  public void addFeature(String afeature) {
    if(feature == null || feature.length == 0) {
      feature = new String[] { afeature } ;
    } else {
      feature = StringUtil.merge(feature, afeature) ;
    }
  }

  public void removeFeatureWithPrefix(String afeature) {
    this.feature = StringUtil.removeStringWithPrefix(feature, afeature) ;
  }

  public void clear() { feature = StringUtil.EMPTY_ARRAY ; }

  public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) { return getOType().equals(type)  ; }

  public String getInfo() {
    StringBuilder b = new StringBuilder() ;
    b.append(getOType()).append(": {") ;
    for(int i = 0; i < feature.length; i++) {
      b.append(feature[i]) ;
      if(i + 1 < feature.length) b.append(", ") ;
    }
    b.append("}") ;
    return b.toString() ;
  }
}
