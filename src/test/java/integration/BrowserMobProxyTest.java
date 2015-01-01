package integration;

import com.codeborne.selenide.WebDriverRunner;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.ProxyServer;
import net.lightbody.bmp.proxy.http.BrowserMobHttpRequest;
import net.lightbody.bmp.proxy.http.RequestInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.net.PortProber.findFreePort;

public class BrowserMobProxyTest extends IntegrationTest {
  ProxyServer proxyServer;

  @Before
  public void closePreviousWebdriver() {
    WebDriverRunner.closeWebDriver();
    server.uploadedFiles.clear();
  }

  @Before
  public void startBrowserMobProxyServer() throws Exception {
    proxyServer = new ProxyServer(findFreePort());
    proxyServer.start();
  }

  @After
  public void stopBrowserMobProxyServer() throws Exception {
    proxyServer.stop();
  }

  @After
  public void resetWebdriverProxySettings() {
    WebDriverRunner.setProxy(null);
    WebDriverRunner.closeWebDriver();
  }

  private int requestCounter = 0;
  
  @Test
  public void canUseBrowserMobProxy() throws UnknownHostException {
    proxyServer.addRequestInterceptor(new RequestInterceptor() {
      @Override
      public void process(BrowserMobHttpRequest httpRequest, Har har) {
        String requestUri = httpRequest.getProxyRequest().getURI().toString();
        System.out.println("request: " + requestUri);
        if (!requestUri.endsWith("/favicon.ico") && !"http://ocsp.digicert.com/".equals(requestUri)) {
          requestCounter++;
        }
      }
    });

    proxyServer.newHar("some-har");

    WebDriverRunner.setProxy(proxyServer.seleniumProxy());
    
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    assertEquals(2, server.uploadedFiles.size());

    assertEquals(2, requestCounter);
    
    List<HarEntry> harEntries = proxyServer.getHar().getLog().getEntries();
    assertEndsWith(harEntries.get(0).getRequest().getUrl(), "/file_upload_form.html");
    assertEndsWith(harEntries.get(harEntries.size()-1).getRequest().getUrl(), "/upload");
  }

  private void assertEndsWith(String text, String suffix) {
    assertTrue(String.format("Should end with '%s', but received: '%s'", suffix, text), text.endsWith(suffix));
  }
}
