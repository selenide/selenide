package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.browsermob.core.har.HarEntry;
import org.browsermob.proxy.ProxyServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.net.PortProber.findFreePort;

public class BrowserMobProxyTest extends IntegrationTest {
  ProxyServer proxyServer;
  
  @Before
  public void setUp() throws Exception {
    proxyServer = new ProxyServer(findFreePort());
    proxyServer.start();
  }

  @After
  public void tearDown() throws Exception {
    proxyServer.stop();
  }

  private int requestCounter = 0;
  private int responseCounter = 0;
  
  @Test
  public void canUseBrowserMobProxy() {
    proxyServer.addRequestInterceptor(new HttpRequestInterceptor() {
      @Override
      public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        requestCounter++;
      }
    });
    proxyServer.addResponseInterceptor(new HttpResponseInterceptor() {
      @Override
      public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        responseCounter++;
      }
    });

    proxyServer.newHar("some-har");

    WebDriverRunner.closeWebDriver();
    WebDriverRunner.setProxy(proxyServer.seleniumProxy());
    
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    assertEquals(2, server.uploadedFiles.size());

    assertEquals(3, requestCounter);
    assertEquals(3, responseCounter);
    
    List<HarEntry> harEntries = proxyServer.getHar().getLog().getEntries();
    assertEquals(3, harEntries.size());
    assertTrue(harEntries.get(0).getRequest().getUrl().endsWith("/file_upload_form.html"));
    assertTrue(harEntries.get(1).getRequest().getUrl().endsWith("/favicon.ico"));
    assertTrue(harEntries.get(2).getRequest().getUrl().endsWith("/upload"));
  }
}
