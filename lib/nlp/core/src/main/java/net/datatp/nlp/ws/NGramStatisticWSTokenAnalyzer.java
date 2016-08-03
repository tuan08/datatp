/**
 * Tách từ dựa theo xác suất. Ví dụ: Học sinh học sinh học
 * Vị trí 0: - Tìm các từ có thể là: "Học", "Học sinh"
 *           - Lấy từ "Học" và tìm các từ tiếp theo là "sinh", "sinh học". Lưu các cặp "Học|sinh", 
 *             "Học|sinh học"
 *           - Lấy từ "Học sinh" và tìm các từ tiếp theo "học", "học sinh". Lưu các cặp "Học sinh|học", 
 *             "Học sinh|học sinh"
 *           - Xét 4 cặp dựa theo inferencePair để tìm cặp tốt nhất -> "Học sinh|học" trả về vị trí 
 *             tiếp theo 3 (theo Rule 2)
 * Vị trí 3: - Tìm các từ có thể là: "sinh", "sinh học"
 *           - Lấy từ "sinh" và tìm các từ tiếp theo là "học". Lưu cặp "sinh|học"
 *           - Lấy từ "sinh học", từ này cuối câu nên trả về null. Lưu cặp "sinh học|null"
 *           - Xét 2 cặp dựa theo inferencePair để tìm cặp tốt nhất -> "sinh học|null" trả về vị trí 
 *             tiếp theo 5 (theo Rule 5)
 * Vị trí thứ 5 > length trả lại kết quả: Học_sinh học sinh_học           
 */
package net.datatp.nlp.ws;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.tag.PunctuationTag;
import net.datatp.util.dataformat.DataReader;
import net.datatp.util.io.IOUtil;

public class NGramStatisticWSTokenAnalyzer implements TokenAnalyzer {
  private NGramStatisticDictionary ngramDict = new NGramStatisticDictionary();

  private boolean debug = false ;

  public NGramStatisticWSTokenAnalyzer() throws Exception {
    this("classpath:nlp/vnws.statistic.gz");
  }

  public NGramStatisticWSTokenAnalyzer(String res) throws Exception {
    DataReader reader = new DataReader(IOUtil.loadRes(res), true);
    ngramDict = reader.read(NGramStatisticDictionary.class);
    reader.close();
    ngramDict.buildIndex();
  }

  public NGramStatisticWSTokenAnalyzer setDebug(boolean b) {
    debug = b ;
    return this ;
  }

  private void dumpProgress(List<IToken> holder) {
    System.out.print("\nProgress: ") ;
    for(int i = 0; i < holder.size(); i++) {
      if(i > 0) System.out.print(" - ") ;
      IToken token = holder.get(i) ;
      System.out.print(token.getOriginalForm()) ;
    }
    System.out.println("\n");
  }

  private void dumpObservations(List<Observation> holder, int position) {
    System.out.println("Observations " + position) ;
    for(int i = 0; i < holder.size(); i++) {
      Observation obs = holder.get(i) ;
      System.out.println("  " + obs) ;
    }
  }

  private void dumpPairs(ObservationPair[] holder) {
    System.out.println("Pairs: ") ;
    for(int i = 0; i < holder.length; i++) {
      System.out.println("  " + holder[i]) ;
    }
  }

  // Câu ví dụ: Học sinh học sinh học
  public IToken[] analyze(IToken[] tokens) throws TokenException {
    try {
      Observation[][] observations = generateObservations(tokens) ;
      List<IToken> holder = new ArrayList<IToken>();
      int position = 0;
      // Duyệt từng token
      while (position < tokens.length) {
        IToken token = tokens[position];
        if (token.hasTagType(PunctuationTag.TYPE)) {
          holder.add(token);
          position++;
          continue;
        }
        // Tại mỗi token sinh ra trạng thái và dự đoán, trả lại là vị trí của token tiếp theo
        position = generateStates(holder, tokens, observations, position);
        if(debug) dumpProgress(holder) ;
      }
      return holder.toArray(new IToken[holder.size()]);
    } catch(Throwable t) {
      System.out.println("==>: '" + new TokenCollection(tokens).getOriginalForm() + '\'');
      throw new RuntimeException(t) ;
    }
  }

  public int generateStates(List<IToken> holder, IToken[] tokens, Observation[][] observations, int position) {
    // Sinh ra các trạng thái từ 1 có thể đối với token hiện tại. Ví dụ đối với token thứ 0 
    //sẽ là "Học" và "Học sinh"
    Observation[] currPosObs = observations[position];
    // Nếu không không có trạng thái sinh ra thì xem token đó coi như không có nhãn 
    if (currPosObs.length == 0) {
      holder.add(tokens[position]);
      return position + 1;
    }

    // Tìm các từ 2 của từng trạng thái từ 1 đã tìm được. Ví dụ: "Học" -> "sinh","sinh học" và "Học sinh" -> "học", "học sinh"
    List<ObservationPair> obsPairHolder = new ArrayList<ObservationPair>();
    for(Observation currObs : currPosObs) {
      // Nếu trạng thái từ 1 là kết thúc câu thì không cần tìm từ 2 
      if(currObs.getTo() >= tokens.length) {
        obsPairHolder.add(new ObservationPair(currObs, null));
      } else {
        Observation[] nextPosObs = observations[currObs.getTo()] ;
        if(nextPosObs.length != 0) {
          for(Observation nextObs : nextPosObs) {
            obsPairHolder.add(new ObservationPair(currObs, nextObs));
          }
        } else {
          obsPairHolder.add(new ObservationPair(currObs, null));
        }
      }
    }
    ObservationPair[] obsPair = obsPairHolder.toArray(new ObservationPair[obsPairHolder.size()]) ;
    // Dự đoán ra cặp có xác suất cao nhất
    return inferencePair(holder, obsPair, tokens);
  }

  public Observation[][] generateObservations(IToken[] tokens) {
    Observation[][] observations = new Observation[tokens.length][] ;
    for(int j = 0; j < tokens.length; j++) {
      List<Observation> stateObs = new ArrayList<Observation>();
      int from = j ;
      int limit = j + 4 ;
      if(limit > tokens.length) limit = tokens.length ;
      for(int i = from; i < limit ; i++) {
        String tokenOForm = Token.newTokenString(tokens, from, i + 1) ;
        String tokenNForm = tokenOForm.toLowerCase() ;

        NGramStatistic distribution = ngramDict.getUnigramNormalize(tokenNForm);
        // Kiểm tra xem trong UNIGRAM CHUẨN HÓA có từ đó không ?
        if (distribution != null) {
          // Nếu có thì lưu từ vào trạng thái
          Observation state = new Observation(tokenOForm, tokenNForm, from, i + 1);
          stateObs.add(state);
        }
      }
      observations[j] = stateObs.toArray(new Observation[stateObs.size()]) ;
    }
    return observations;
  }

  // Sử dụng luật để tính và dự đoán các cặp trạng thái đã xác định được 
  public int inferencePair(List<IToken> holder, ObservationPair[] pairs, IToken[] token) {
    if(debug) dumpPairs(pairs) ;
    // Nếu chỉ có 1 cặp trường hợp xuất hiện thì xuất kết quả chính là cặp đấy
    if (pairs.length == 1) {
      holder.add(pairs[0].getObs().newToken(token));
      if (pairs[0].getNextObs() != null) {
        holder.add(pairs[0].getNextObs().newToken(token));
      }
      return pairs[0].getNextPosition();
    }

    // Biến total để kiểm tra xem một luật đã được sử dụng chưa
    float total = 0.0f;
    // Tìm xác suất lớn nhất
    float max = 0.0f;
    ObservationPair   bestSelectPair = null;

    // Luật 1: Lấy xác suất BIGRAM của cặp từ đã tìm ra
    // Nếu xác suất của cặp đấy lớn hơn ngưỡng tin cậy (0.7) thì đem ra so sánh
    // Nếu cặp nào lớn nhất thì sẽ tách theo cặp đấy
    if(debug) {
      System.out.println("RULE 1: ");
    }
    for (ObservationPair pair : pairs) {
      float distribute = pair.getBigramDistribution(ngramDict);
      if (pair.getUnigramDistribution(ngramDict) > distribute) distribute = 0;
      if(debug) {
        System.out.println("  Distribution " + pair + " : " + distribute);
      }
      if (distribute > 0.7) {
        if (distribute > max) {
          max = distribute;
          total += distribute;
          bestSelectPair = pair;
        }
      }
    }
    // Luật 2: Lấy xác suất BIGRAM CHUẨN HÓA của cặp từ đã tìm ra
    // Nếu xác suất của cặp đấy lớn hơn ngưỡng tin cậy (0.7) thì đem ra so sánh
    // Nếu cặp nào lớn nhất thì sẽ tách theo cặp đấy
    if (total <= 0.1) {
      if(debug) {
        System.out.println("RULE 2:");
      }
      for (ObservationPair pair : pairs) {
        float distribute = pair.getBigramNormalizeDistribution(ngramDict);
        if (pair.getUnigramDistributionNormalize(ngramDict) > distribute)
          distribute = 0;
        if(debug) {
          System.out.println("  Distribution" + pair + ": " + distribute);
        }
        if (distribute > 0.7)
          if (distribute > max) {
            max = distribute;
            total += distribute;
            bestSelectPair = pair;
          }
      }
    }

    // Luật 3: Nếu xác suất UNIGRAM của từ 1 lớn hơn một ngưỡng tin cậy (0.95) thì đem ra so sánh
    // Nếu từ nào có xác suất lớn nhất thì tách theo từ đấy
    if (total <= 0.1) {
      if(debug) {
        System.out.println("RULE 3: ") ;
      }
      for (ObservationPair pair : pairs) {
        float distribute = pair.getObs().getDistribution(ngramDict) +  (float)pair.getObs().getTokenOForm().length()/100;
        if(debug) {
          System.out.println("  Distribution " + pair.getObs().getTokenOForm() + ":" + distribute);
        }
        if (distribute > 0.95) {
          max = distribute;
          total += distribute;
          bestSelectPair = new ObservationPair(pair.getObs(), null);
        }
      }
    }

    // Luật 4: Nếu xác suất UNIGRAM CHUẨN HÓA của từ 1 lớn hơn một ngưỡng tin cậy (0.95) thì đem ra so sánh
    // Nếu từ nào có xác suất lớn nhất thì tách theo từ đấy
    if (total <= 0.1) {
      if(debug) {
        System.out.println("RULE 4: ");
      }
      for (ObservationPair pair : pairs) {
        float distribute = 
            pair.getObs().getDistributionNormalize(ngramDict) + (float)pair.getObs().getTokenOForm().length() / 100;
        if(debug) {
          System.out.println("  Distribution " + pair.getObs().getTokenNForm() + ":" + distribute);
        }
        if (distribute > 0.95) {
          max = distribute;
          total += distribute;
          bestSelectPair = new ObservationPair(pair.getObs(), null);
        }
      }
    }

    // Luật 5: Sử dụng xác suất kết hợp giữa xác suất UNIGRAM của từ 1 + UNIGRAM của từ 2
    // Nếu xác suất của cặp từ nào lớn nhất thì tách theo cặp từ đấy
    if (total <= 0.1) {
      if(debug) {
        System.out.println("RULE 5");
      }
      for (ObservationPair pair : pairs) {
        float distribute = pair.getCombineDistribution(ngramDict);
        if(debug) {
          System.out.println("  Distribution " + pair + ": " + distribute);
        }
        if (distribute > max) {
          max = distribute;
          total += distribute;
          bestSelectPair = pair;
        }
      }
    }

    // Luật 6: Sử dụng xác suất kết hợp giữa xác suất UNIGRAM CHUẨN HOA của từ 1 + UNIGRAM CHUẨN HOA của từ 2
    // Nếu xác suất của cặp từ nào lớn nhất thì tách theo cặp từ đấy
    if (total <= 0.1) {
      if(debug) {
        System.out.println("RULE 6");
      }
      for (ObservationPair pair : pairs) {
        float distribute = pair.getCombineNormalizeDistribution(ngramDict);
        if(debug) {
          System.out.println("  Distribution " + pair  + ":" + distribute);
        }
        if (distribute > max) {
          max = distribute;
          total += distribute;
          bestSelectPair = pair;
        }
      }
    }

    // Tách theo cặp từ xác suất lớn nhất
    holder.add(bestSelectPair.getObs().newToken(token));
    if (bestSelectPair.getNextObs() != null) holder.add(bestSelectPair.getNextObs().newToken(token));
    return bestSelectPair.getNextPosition();
  }

  static class ObservationPair {
    private Observation obs;
    private Observation nextObs ;
    private String unigram ;
    private String unigramNormalize ;
    private String bigram ;
    private String bigramNormalize ;

    public ObservationPair(Observation obs, Observation nextObs) {
      this.obs = obs;
      this.nextObs = nextObs;
    }

    public Observation getObs() { return obs; }

    public Observation getNextObs() { return nextObs; }

    public String getBigram() { 
      if(bigram == null) bigram = obs.getTokenOForm() + "|" + nextObs.getTokenOForm(); 
      return bigram ;
    }

    public String getUnigram() { 
      if(unigram == null) unigram = obs.getTokenOForm() + " " + nextObs.getTokenOForm() ; 
      return unigram ;
    }

    public String getUnigramNormalize() { 
      if(unigramNormalize == null) unigramNormalize = getUnigram().toLowerCase() ;
      return unigramNormalize; 
    }

    public float getUnigramDistribution(NGramStatisticDictionary ngramDict) {
      if (nextObs != null) {
        NGramStatistic entry = ngramDict.getUnigram(getUnigram());
        if (entry != null) return entry.getDistribution();
        else return 0;
      } 
      return 0;
    }

    public float getUnigramDistributionNormalize(NGramStatisticDictionary ngramDict) {
      if (nextObs != null) {
        NGramStatistic entry = ngramDict.getUnigramNormalize(getUnigramNormalize());
        if(entry != null) return entry.getNWordDistribution();
        else return 0;
      } 
      return 0;
    }

    // Xác suất BIGRAM của cặp từ 1 và từ 2
    public float getBigramDistribution(NGramStatisticDictionary ngramDict) {
      if (nextObs != null) {
        NGramStatistic entry = ngramDict.getBigram(getBigram());
        if (entry != null) return entry.getDistribution();
        else return 0 ;
      }
      return 0;
    }

    public String getBigramNormalize() { 
      if(bigramNormalize == null) bigramNormalize = getBigram().toLowerCase(); 
      return bigramNormalize ;
    }

    // Xác suất BIGRAM chuẩn hóa của cặp từ 1 và từ 2
    public float getBigramNormalizeDistribution(NGramStatisticDictionary ngramDict) {
      if (nextObs != null) {
        NGramStatistic entry = ngramDict.getBigramNormalize(getBigramNormalize());
        if (entry != null) return entry.getNWordDistribution();
        else return 0;
      } 
      return 0;
    }

    //Xác suất kết hợp của xác suất cặp từ 1 + xác suất từ 2
    public float getCombineDistribution(NGramStatisticDictionary ngramDict) {
      float total = obs.getDistribution(ngramDict);
      if (nextObs != null) {
        total = total + nextObs.getDistribution(ngramDict) ;
        return total / 2;
      }
      return total;
    }

    // Xác suất kết hợp chuẩn hóa của xác suất chuẩn hóa cặp từ 1 + xác suất chuẩn hóa từ 2
    public float getCombineNormalizeDistribution(NGramStatisticDictionary ngramDict) {
      float total = obs.getDistributionNormalize(ngramDict) ;
      if (nextObs != null) {
        total = total + nextObs.getDistributionNormalize(ngramDict) ;
        return total / 2;
      } 
      return total;
    }

    public int getNextPosition() {
      if (nextObs != null) return nextObs.getTo();
      else return obs.getTo();
    }

    public String toString() {
      StringBuilder b = new StringBuilder() ;
      b.append(obs).append(" - ").append(nextObs) ;
      return b.toString() ;
    }
  }

  static class Observation {
    private String tokenOForm ;
    private String tokenNForm ;
    private int from ;
    private int to;

    public Observation(String tokenOForm, String tokenNForm, int from, int to) {
      this.tokenOForm = tokenOForm ;
      this.tokenNForm = tokenNForm ;
      this.from = from;
      this.to = to;
    }

    public String getTokenOForm() { return this.tokenOForm ; }

    public String getTokenNForm() { return this.tokenNForm ; }

    public IToken newToken(IToken[] token) { 
      if(from + 1 == to) return token[from] ;
      IToken retToken = new Token(token, from, to) ;
      //retToken.add(WordTag.VNWORD);
      return retToken ; 
    }

    public int getFrom() { return this.from ;}

    public int getTo() { return to; }

    public float getDistribution(NGramStatisticDictionary ngramDict) {
      NGramStatistic entry = ngramDict.getUnigram(tokenOForm);
      if (entry != null) return entry.getDistribution();
      else return 0;
    }

    public float getDistributionNormalize(NGramStatisticDictionary ngramDict) {
      NGramStatistic entry = ngramDict.getUnigramNormalize(tokenNForm);
      if (entry != null) return entry.getNWordDistribution();
      else return 0;
    }

    public String toString() {
      StringBuilder b = new StringBuilder() ;
      b.append(getTokenOForm()).append("[").append(from).append(",").append(to).append("]");
      return b.toString() ;
    }
  }
}