package net.datatp.nlp.dict;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.util.StringPool;
import net.datatp.util.io.IOUtil;
import net.datatp.util.json.JSONReader;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityDictionary {
  final static public String[] DICT_RES = {
      "classpath:nlp/entity/vn.place.json",
      "classpath:nlp/entity/vn.person.json",
      "classpath:nlp/entity/mobile.product.json"
  } ;

  private List<Meaning> entities = new ArrayList<Meaning>() ;

  public EntityDictionary() {  }

  public EntityDictionary(String[] res) throws Exception { 
    for(String sel : res) {
      InputStream is = IOUtil.loadRes(sel) ;
      JSONReader reader = new JSONReader(is) ;
      Meaning meaning = null ;
      while((meaning = reader.read(Meaning.class)) != null) {
        add(meaning) ;
      }
    }
    optimize(new StringPool()) ;
  }

  public void add(Meaning meaning) {
    if(!"entity".equals(meaning.getOType())) {
      throw new RuntimeException(meaning.getName() + " is not an entity") ;
    }
    entities.add(meaning) ;
  }

  public void add(String name, String[] type, String[] variant) {
    Meaning meaning = new Meaning() ;
    meaning.setOType("entity") ;
    meaning.setName(name) ;
    meaning.setType(type) ;
    meaning.setVariant(variant) ;
    add(meaning) ;
  }

  public Meaning[] find(String name, String[] type) {
    List<String> mandatoryTypeHolder  = new ArrayList<String>() ;
    List<String> nonMandatoryTypeHolder  = new ArrayList<String>() ;
    if(type != null) {
      for(String selType : type) {
        if(selType.startsWith("+")) mandatoryTypeHolder.add(selType.substring(1)) ;
        else nonMandatoryTypeHolder.add(selType) ;
      }
    }
    String[] mandatoryType = mandatoryTypeHolder.toArray(new String[mandatoryTypeHolder.size()]) ;
    String[] nonMandatoryType = nonMandatoryTypeHolder.toArray(new String[nonMandatoryTypeHolder.size()]) ;

    List<Meaning> holder  = new ArrayList<Meaning>() ;
    for(int i = 0; i < entities.size(); i++) {
      Meaning entity = entities.get(i) ;
      if(name != null) {
        if(!name.equals(entity.getName())) continue ;
      }
      String[] meaningTypes = entity.getType() ;
      if(mandatoryType.length > 0) {
        if(!StringUtil.isIn(mandatoryType, meaningTypes))  continue ;
      }
      if(nonMandatoryType.length > 0) {
        boolean select = false ; 
        for(String selType : nonMandatoryType) {
          if(StringUtil.isIn(selType, meaningTypes))  {
            select = true ;
            break ;
          }
        }
        if(!select) continue; 
      }
      holder.add(entity) ;
    }
    return holder.toArray(new Meaning[holder.size()]) ;
  }

  public void optimize(StringPool pool) {
    for(int i = 0; i < entities.size(); i++) {
      entities.get(i).optimize(pool) ;
    }
  }
}