package net.datatp.xhtml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

import net.datatp.util.text.StringUtil;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 27, 2010  
 */
public class LSDomWriter {
  final static LSDomWriter INSTANE = new LSDomWriter();


  private DOMImplementationLS domImpl ;
  private LSSerializerFilter filter ;

  public LSDomWriter() {
    try {
      DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
      domImpl = (DOMImplementationLS)registry.getDOMImplementation("LS");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
      throw new RuntimeException(e);
    }
  }

  public LSDomWriter(LSSerializerFilter filter) {
    this.filter = filter ;
  }

  public void write(OutputStream os, Document doc) throws Exception {
    LSSerializer serializer = domImpl.createLSSerializer();
    if(filter != null) serializer.setFilter(filter) ;
    LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(os);
    lso.setEncoding("UTF-8");
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(doc, lso);
  }

  public String toXMLString(Document doc) {
    return toXMLString(doc, StringUtil.UTF8) ;
  }
  
  public String toXMLString(Document doc, Charset charset) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    LSSerializer serializer = domImpl.createLSSerializer();
    if(filter != null) serializer.setFilter(filter) ;
    LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(bos);
    lso.setEncoding(charset.name());
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(doc, lso);
    return new String(bos.toByteArray(), charset) ;
  }

  public String toXMLString(Node node) throws Exception {
    return toXMLString(node, StringUtil.UTF8) ;
  }

  public String toXMLString(Node node, Charset charset) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    LSSerializer serializer = domImpl.createLSSerializer();
    if(filter != null) serializer.setFilter(filter) ;
    LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(bos);
    lso.setEncoding(charset.name());
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(node, lso);
    return new String(bos.toByteArray(), charset) ;
  }

  
  static public class NodeNameFilter implements LSSerializerFilter {
    private String[] skipNodeName ;

    public NodeNameFilter(String[] skipNodeName) {
      this.skipNodeName = skipNodeName ;
    }

    public int getWhatToShow() { return LSSerializerFilter.SHOW_ALL ; }

    public short acceptNode(Node node) {
      String name = node.getNodeName() ;
      if(StringUtil.isIn(name, skipNodeName)) {
        return LSSerializerFilter.FILTER_REJECT ;
      }
      return LSSerializerFilter.FILTER_ACCEPT ;
    }
  }
}
