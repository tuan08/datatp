package net.datatp.xhtml;

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.datatp.util.text.StringUtil;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 27, 2010
 */
public class TransformDomWriter {
  private Transformer transformer ;

  public TransformDomWriter() throws Exception {
    TransformerFactory factory = TransformerFactory.newInstance();
    transformer = factory.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "html");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  }

  public void write(OutputStream os, Document doc) throws Exception {
    //Print the DOM node
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);
    String xmlString = sw.toString();
    os.write(xmlString.getBytes(StringUtil.UTF8)) ;
  }

  public String toXMLString(Node node) throws Exception {
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(node);
    transformer.transform(source, result);
    return sw.toString();
  }
}