package net.datatp.nlp.vi.addtone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class AddTone {
  Hashtable<String, String[]> htMap = new Hashtable<String, String[]>();
  Hashtable<String, Double> htProbability = new Hashtable<String, Double>();

  public AddTone() throws Exception {
    loadCorpus();
  }

  public int loadCorpus() throws Exception {
    String s = new String();

    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("map.txt"), "UTF-16"));
    while ((s = in.readLine()) != null) {
      String[] temp = s.split("\\|");
      if (temp.length == 2)
        this.htMap.put(temp[0], temp[1].split(","));
    }
    in.close();

    in = new BufferedReader(new InputStreamReader(new FileInputStream("bigramDict.txt"), "UTF-16"));
    while ((s = in.readLine()) != null) {
      String[] temp = s.split("\\|");
      if (temp.length == 2) {
        this.htProbability.put(temp[0], Double.valueOf(Double.parseDouble(temp[1])));
      }
    }
    in.close();
    return 1;
  }

  public String addTone(String str) {
    String s = new String();
    double pNotHas = ((Double) htProbability.get("notHas")).doubleValue();

    double[] p1 = new double[50];
    double[] p2 = new double[50];
    int[][] track = new int[50][50];
    str = str.trim();
    String[] word = str.split("[ ]+");
    String[] wordResult = str.toLowerCase().split("[ ]+");

    if (!this.htMap.containsKey(wordResult[0]))
      this.htMap.put(wordResult[0], wordResult[0].split("\\s"));
    String[] temp1 = (String[]) this.htMap.get(wordResult[0]);
    for (int i = 1; i < wordResult.length; i++) {
      if (!this.htMap.containsKey(wordResult[i]))
        this.htMap.put(wordResult[i], wordResult[i].split("\\s"));
      String[] temp2 = (String[]) this.htMap.get(wordResult[i]);

      for (int j = 0; j < temp2.length; j++) {
        double[] ptemp = new double[50];
        for (int k = 0; k < temp1.length; k++) {
          s = new String(temp1[k] + " " + temp2[j]);
          if (this.htProbability.containsKey(s))
            p1[k] += ((Double) this.htProbability.get(s)).doubleValue();
          else
            p1[k] += pNotHas;
        }
        p2[j] = min(ptemp);
        int pos = getPosInArr(p2[j], ptemp);
        track[i][j] = pos;
      }
      moveArr(p1, p2);
      temp1 = temp2;
    }

    int pos = getPosInArr(min(p1), p1);
    for (int i = wordResult.length - 1; i >= 0; i--) {
      temp1 = (String[]) this.htMap.get(wordResult[i]);
      wordResult[i] = temp1[pos];
      pos = track[i][pos];
    }

    s = new String();
    for (int i = 0; i < wordResult.length; i++) {
      String singleWord = wordResult[i];
      if (!dropSignal(singleWord).equals(word[i])) {
        char[] charArrWord = singleWord.toCharArray();
        singleWord = new String();
        for (int j = 0; j < charArrWord.length; j++) {
          String ch = new String(charArrWord, j, 1);
          if ((word[i].charAt(j) > '@') && (word[i].charAt(j) < '['))
            ch = ch.toUpperCase();
          singleWord = singleWord + ch;
        }
        wordResult[i] = singleWord;
      }
      s = s + wordResult[i] + " ";
    }
    return s.trim();
  }

  public double min(double[] arr) {
    double min = arr[0];
    for (int i = 1; i < arr.length; i++) {
      double so = arr[i];
      if (so != 0.0D)
        min = min <= so ? min : so;
    }
    return min;
  }

  public int getPosInArr(double so, double[] arr) {
    for (int i = 0; i < arr.length; i++)
      if (so == arr[i])
        return i;
    return -1;
  }

  public boolean moveArr(double[] arr1, double[] arr2) {
    if (arr1.length < arr2.length) {
      return false;
    }
    for (int i = 0; i < arr2.length; i++) {
      arr1[i] = arr2[i];
      arr2[i] = 0.0D;
    }
    return true;
  }

  public static String dropSignal(String s) {
    s = s.replaceAll("á", "a");
    s = s.replaceAll("à", "a");
    s = s.replaceAll("ả", "a");
    s = s.replaceAll("ã", "a");
    s = s.replaceAll("ạ", "a");

    s = s.replaceAll("â", "a");
    s = s.replaceAll("ấ", "a");
    s = s.replaceAll("ầ", "a");
    s = s.replaceAll("ẩ", "a");
    s = s.replaceAll("ẫ", "a");
    s = s.replaceAll("ậ", "a");

    s = s.replaceAll("ă", "a");
    s = s.replaceAll("ắ", "a");
    s = s.replaceAll("ằ", "a");
    s = s.replaceAll("ẳ", "a");
    s = s.replaceAll("ẵ", "a");
    s = s.replaceAll("ặ", "a");

    s = s.replaceAll("é", "e");
    s = s.replaceAll("è¨", "e");
    s = s.replaceAll("ẻ", "e");
    s = s.replaceAll("ẽ", "e");
    s = s.replaceAll("ẹ", "e");

    s = s.replaceAll("ê", "e");
    s = s.replaceAll("ế", "e");
    s = s.replaceAll("ề", "e");
    s = s.replaceAll("ể", "e");
    s = s.replaceAll("ễ", "e");
    s = s.replaceAll("ệ", "e");

    s = s.replaceAll("ó", "o");
    s = s.replaceAll("ò", "o");
    s = s.replaceAll("ỏ", "o");
    s = s.replaceAll("õ", "o");
    s = s.replaceAll("ọ", "o");

    s = s.replaceAll("ô", "o");
    s = s.replaceAll("ồ", "o");
    s = s.replaceAll("ố", "o");
    s = s.replaceAll("ổ", "o");
    s = s.replaceAll("ỗ", "o");
    s = s.replaceAll("ộ", "o");

    s = s.replaceAll("ơ", "o");
    s = s.replaceAll("ớ", "o");
    s = s.replaceAll("ờ", "o");
    s = s.replaceAll("ở", "o");
    s = s.replaceAll("ỡ", "o");
    s = s.replaceAll("ợ", "o");

    s = s.replaceAll("í", "i");
    s = s.replaceAll("ì", "i");
    s = s.replaceAll("ỉ", "i");
    s = s.replaceAll("ĩ", "i");
    s = s.replaceAll("ị", "i");

    s = s.replaceAll("ú", "u");
    s = s.replaceAll("ù", "u");
    s = s.replaceAll("ủ", "u");
    s = s.replaceAll("ũ", "u");
    s = s.replaceAll("ụ", "u");

    s = s.replaceAll("ư", "u");
    s = s.replaceAll("ứ", "u");
    s = s.replaceAll("ừ", "u");
    s = s.replaceAll("ử", "u");
    s = s.replaceAll("ữ", "u");
    s = s.replaceAll("ự", "u");

    s = s.replaceAll("đ", "d");

    s = s.replaceAll("ý", "y");
    s = s.replaceAll("ỳ", "y");
    s = s.replaceAll("ỷ", "y");
    s = s.replaceAll("ỹ", "y");
    s = s.replaceAll("ỵ", "y");
    return s;
  }

  public String addTextSegment(String textSegment) {
    StringTokenizer stk = new StringTokenizer(textSegment, "()[]/-\"…',“”.‘’?!:;", true);

    textSegment = new String();
    while (stk.hasMoreTokens()) {
      boolean check = false;
      String temp = stk.nextToken();
      if ((temp.equals(".")) || (temp.equals(",")) || (temp.equals("?"))
          || (temp.equals("!")) || (temp.equals(":")) || (temp.equals(")"))
          || (temp.equals("]")) || (temp.equals("”")) || (temp.equals("’"))
          || (temp.equals("…"))) {
        temp = temp + " ";
        check = true;
      }
      if ((temp.equals("(")) || (temp.equals("[")) || (temp.equals("“"))
          || (temp.equals("‘"))) {
        temp = " " + temp;
        check = true;
      }
      if (temp.equals("-")) {
        temp = " " + temp + " ";
        check = true;
      }
      if ((temp.equals("\"")) || (temp.equals("'")) || (temp.equals("\n")) || (temp.equals("/")))
        check = true;
      if (!check) {
        temp = addTone(temp);
      }
      textSegment = textSegment + temp;
    }
    return textSegment;
  }
}
