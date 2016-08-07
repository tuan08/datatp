/**
 * 1. Thống kê Unigram Word: Thống kê tần số xuất hiện của các từ trong tập huấn luyện
 * 2. Thống kê Bigram Word: Thống kê tần số xuất hiện của các cặp 2 từ liên tiếp trong tập huấn luyện
 * 3. Thống kê Ngram Token: Thống kê tần số xuất hiện của các ngram token trong tập huấn luyện
 * 4. Tính phân phối Unigram Word = tần số xuất hiện của một từ chia cho tần số ngram token 
 *    tương ứng với từ đấy
 * 5. Tính phân phối Bigram Word = tần số xuất hiện của cặp từ chia cho tần số ngram token 
 *    tương ứng với cặp từ đấy
 * 6. Lập danh sách các thành phần xuất hiện trong 1 từ. Ví dụ từ: Tây Ban Nha sẽ có 2 chuỗi 
 *    thành phần là: tây, tây ban
 */
package net.datatp.nlp.ws;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.datatp.nlp.dict.LexiconDictionary;
import net.datatp.nlp.dict.Meaning;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.util.CharacterSet;
import net.datatp.nlp.wtag.WTagDocumentReader;
import net.datatp.nlp.wtag.WTagDocumentSet;
import net.datatp.util.dataformat.DataReader;
import net.datatp.util.dataformat.DataWriter;
import net.datatp.util.io.IOUtil;
import net.datatp.util.stat.Statistics;

public class NGramStatisticDictionaryBuilder {
  private TokenFrequency wordStatistic = new TokenFrequency() ;
  private TokenFrequency wordNormStatistic = new TokenFrequency() ;

  private TokenFrequency biWordStatistic = new TokenFrequency() ;
  private TokenFrequency biWordNormStatistic = new TokenFrequency() ;
  private TokenFrequency ngramStatistic = new TokenFrequency() ;
  private Statistics reporter  = new Statistics();

  private void buildStatistic(String dataDir) throws Exception {
    WTagDocumentSet set = new WTagDocumentSet(dataDir, ".*\\.(tagged|wtag)");
    String[] files = set.getFiles();
    for(int k = 0; k < files.length; k++) {
      reporter.incr("File", "all", 1) ;
      String content = IOUtil.getFileContentAsString(files[k]);
      WTagDocumentReader reader = new WTagDocumentReader();
      TokenCollection[] collection = reader.read(content);
      for (int i = 0; i < collection.length; i++) {
        IToken[] tokens = collection[i].getTokens();
        for (int j = 0; j < tokens.length; j++) {
          wordStatistic.addToken(tokens[j].getOriginalForm());
          wordNormStatistic.addToken(tokens[j].getNormalizeForm()) ;
          if(j != tokens.length - 1) {
            biWordStatistic.addToken(tokens[j].getOriginalForm() + "|" + tokens[j + 1].getOriginalForm());
            biWordNormStatistic.addToken(tokens[j].getNormalizeForm() + "|" + tokens[j + 1].getNormalizeForm());
          }
        }
        NGram[] ngrams = NGram.ngrams(collection[i].getOriginalForm().split(" "), 5);
        for (NGram ngram : ngrams) {
          ngramStatistic.addToken(ngram.getToken());
          if (matches(ngram.getToken(), "\\p{Lu}"))
            ngramStatistic.addToken(ngram.getToken().toLowerCase());
        }
      }
      if(k != 0) {
        System.out.println("-----------------------------------------------------") ;
      }
      System.out.println("File: " + k) ;
      System.out.println("Word: " + this.wordStatistic.size()) ;
      System.out.println("Word Norm: " + this.wordNormStatistic.size()) ;
      System.out.println("Bi Word: " + this.biWordStatistic.size()) ;
      System.out.println("Bi Word Norm: " + this.biWordNormStatistic.size()) ;
      System.out.println("NGram: " + this.ngramStatistic.size()) ;
    }

    for(String selRes : LexiconDictionary.VI_LEXICON_RES) {
      InputStream is = IOUtil.loadRes(selRes) ;
      DataReader reader = new DataReader(is) ;
      Meaning meaning = null ;
      while((meaning = reader.read(Meaning.class)) != null) {
        String name = meaning.getName().toLowerCase() ;
        if(!wordNormStatistic.containsKey(name)) {
          System.out.println("not have " + name);
          wordNormStatistic.addToken(name) ;
        }
      }
    }
  }

  public void process(String dataDir) throws Exception {
    buildStatistic(dataDir) ;
    List<NGramStatistic> unigram = new ArrayList<NGramStatistic>();

    String[] keyWords = wordStatistic.getTokens();
    for (String key : keyWords) {
      if (ngramStatistic.containsKey(key) && isWordSequence(key)) {
        String nkey = key.toLowerCase() ;
        int keywordCount = wordStatistic.getTokenCount(key) ;
        int keywordNormCount = wordNormStatistic.getTokenCount(nkey) ;
        float distribution     = (float) keywordCount/ngramStatistic.getTokenCount(key);
        float distributionNorm = (float)keywordNormCount/ngramStatistic.getTokenCount(nkey);
        unigram.add(new NGramStatistic(key, nkey, keywordCount, keywordNormCount, distribution, distributionNorm));
      }
    }

    List<NGramStatistic> bigram  = new ArrayList<NGramStatistic>();
    String[] biWord = biWordStatistic.getTokens();
    for (String key : biWord) {
      String word = key.replace("|", " ");
      if(ngramStatistic.containsKey(word) && isWordSequence(word)) {
        int biKeyWordCount = biWordStatistic.getTokenCount(key) ;
        int biKeyWordNormCount = biWordNormStatistic.getTokenCount(key.toLowerCase()) ;
        float value = (float) biKeyWordCount/ngramStatistic.getTokenCount(word);
        float valueNormalize = (float) biKeyWordNormCount / ngramStatistic.getTokenCount(word.toLowerCase());
        bigram.add(new NGramStatistic(key, key.toLowerCase(), biKeyWordCount, biKeyWordNormCount, value, valueNormalize));
      }
    }

    NGramStatisticDictionary gram = new NGramStatisticDictionary(unigram, bigram);

    DataWriter writer = new DataWriter("target/gram.json");
    writer.write(gram);
    writer.close();
  }

  private boolean matches(String text, String regex) {
    boolean foundMatch = false;
    Pattern pattern = Pattern.compile(regex);
    Matcher regexMatcher = pattern.matcher(text);
    foundMatch = regexMatcher.find();
    return foundMatch;
  }

  private boolean isWordSequence(String sequence) {
    String[] tokens = sequence.split(" ");
    for (String token : tokens)
      if(!CharacterSet.isWLetter(token)) return false;
    return true;
  }

  static public class TokenFrequency  extends TreeMap<String, Integer>{
    public int getTokenCount(String token) {
      Integer count = get(token) ;
      if(count == null) return 0 ; 
      return count.intValue() ;
    }

    public void addToken(String token) {
      Integer count = get(token) ;
      if(count == null) put(token, 1) ;
      else put(token, count.intValue() + 1) ;
    }

    public String[] getTokens() {
      return keySet().toArray(new String[size()]) ;
    }
  }

  public static void main(String[] args) throws Exception {
    new NGramStatisticDictionaryBuilder().process("d:/set.vlsp") ;
  }
}