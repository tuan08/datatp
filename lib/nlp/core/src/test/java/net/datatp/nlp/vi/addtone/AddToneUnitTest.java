package net.datatp.nlp.vi.addtone;

import org.junit.Test;

import net.datatp.nlp.vi.addtone.AddTone;

public class AddToneUnitTest {
  @Test
  public void test() throws Exception {
    String text = "Tren bau troi chi co 2 ngoi sao"; 
    AddTone tone = new AddTone();
    String output = tone.addTextSegment(text);
    System.out.println(output);
  }
}
