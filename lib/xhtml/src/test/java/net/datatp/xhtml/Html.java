package net.datatp.xhtml;

public class Html {
  final static public String STANDARD = 
      "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
      "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
      "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
      "  <head>\n" +
      "    <base href='http://www.vnexpress.net'/>" +
      "    <title>Hello world</title>\n" +
      "  </head>\n" +
      "  <body>\n" +
      "    <a id='AbsoluteLink' class='Link' href='/static/link/ABCDE'>Hello AbsoluteLink</a>\n" +
      "    <a id='RelativeLink' class='Link' href='link/ABCDE'>Hello RelativeLink</a>\n" +
      "    <A id='ExternalLink' class='Link ExternalLink' href='http://vnexpress.net/static/link/ABCDE'>Hello ExternalLink</A>\n" +
      "    <img id='AbsoluteImgSrc' href='/image/image.jpg' />\n" +
      "    <script id='Script' type='text/javascript'>" +
      "      window.alert('hello') ;" +
      "    </script>" +
      "    <br />" +
      "  </body>\n" +
      "</html>" ;
}
