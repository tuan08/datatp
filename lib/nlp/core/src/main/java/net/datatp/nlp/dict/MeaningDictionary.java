package net.datatp.nlp.dict;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.util.StringPool;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class MeaningDictionary {
  private String               otype;
  private List<Meaning>        meangings = new ArrayList<>();

  public MeaningDictionary(String otype) {  
    this.otype = otype;
  }
  
  public MeaningDictionary(String otype, List<Meaning> meanings) throws Exception { 
    this.otype = otype;
    for(int i = 0; i < meanings.size(); i++) add(meanings.get(i));
  }

  public void add(Meaning meaning) {
    if(meaning.getOType() == null) meaning.setOType(otype);
    if(!otype.equals(meaning.getOType())) {
      throw new RuntimeException(meaning.getName() + " is not an " + otype) ;
    }
    meangings.add(meaning) ;
  }

  public void add(String name, String[] type, String[] variant) {
    Meaning meaning = new Meaning() ;
    meaning.setOType(otype) ;
    meaning.setName(name) ;
    meaning.setType(type) ;
    meaning.setVariant(variant) ;
    add(meaning) ;
  }

  public Meaning[] find(String name, String[] type) {
    List<Meaning> holder  = new ArrayList<Meaning>() ;
    find(holder, name, type);
    return holder.toArray(new Meaning[holder.size()]) ;
  }
  
  
  public void find(List<Meaning> holder, String name, String[] type) {
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

    for(int i = 0; i < meangings.size(); i++) {
      Meaning entity = meangings.get(i) ;
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
  }

  public void optimize(StringPool pool) {
    for(int i = 0; i < meangings.size(); i++) {
      meangings.get(i).optimize(pool) ;
    }
  }
}