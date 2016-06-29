package net.datatp.netty.http.webapp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.mustachejava.MustacheFactory;

import net.datatp.netty.http.webapp.HtmlBlock;
import net.datatp.netty.http.webapp.HtmlPage;

public class HelloPage extends HtmlPage {
  private HelloNavBlock navBlock ;
  public HelloPage() throws IOException {
    super("hello", "file:src/test/resources/webapp/hello.mtpl");
    navBlock = new HelloNavBlock(mFactory) ;
  }

  public void render(Writer writer, Map<String, Object> scopes) throws Exception {
    scopes.put("navigation", navBlock.toHtml(scopes)) ;
    renderHtmlPage(writer, scopes);
  }

  static public class HelloNavBlock extends HtmlBlock {
    public HelloNavBlock(MustacheFactory mf) throws IOException {
      super("hellonav", "file:src/test/resources/webapp/hello-navigation.mtpl", mf);
    }
  }
}
