package net.datatp.webcrawler.urldb;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.apache.activemq.util.ClassLoadingAwareObjectInputStream;
import org.junit.Test;

import net.datatp.util.io.IOUtil;

public class URLDatumSerializationUnitTest {
  @Test
  public void test() throws Exception {
    URLDatum urldatum = new URLDatum(System.currentTimeMillis());
    urldatum.setOrginalUrl("http://vnexpress.net");
    byte[] data = IOUtil.serialize(urldatum);
    urldatum = (URLDatum) IOUtil.deserialize(data);
    
    ObjectInputStream obis = new ObjectInputStream(new ByteArrayInputStream(data));
    urldatum = (URLDatum)obis.readObject();
    obis.close();
    
    ClassLoadingAwareObjectInputStream clois = new ClassLoadingAwareObjectInputStream(new ByteArrayInputStream(data));
    urldatum = (URLDatum)clois.readObject();
    clois.close();
  }
}
