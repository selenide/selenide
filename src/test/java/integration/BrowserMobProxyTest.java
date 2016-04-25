package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.ProxyServer;
import net.lightbody.bmp.proxy.http.BrowserMobHttpRequest;
import net.lightbody.bmp.proxy.http.BrowserMobHttpResponse;
import net.lightbody.bmp.proxy.http.RequestInterceptor;
import net.lightbody.bmp.proxy.http.ResponseInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.openqa.selenium.net.PortProber.findFreePort;

public class BrowserMobProxyTest extends IntegrationTest {
  private static final Logger log = Logger.getLogger(BrowserMobProxyTest.class.getName());
  
  ProxyServer proxyServer;

  @Before
  public void closePreviousWebdriver() {
    assumeFalse(isPhantomjs());

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
    if (proxyServer != null) {
      proxyServer.stop();
    }
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
        log.info("request: " + requestUri);
        if (!requestUri.endsWith("/favicon.ico") && 
            !requestUri.endsWith("/start_page.html") && 
            !"http://ocsp.digicert.com/".equals(requestUri) &&
            !requestUri.contains("cdn.mozilla.net") &&
            !requestUri.contains("services.mozilla.net")) {
          requestCounter++;
        }
      }
    });
    proxyServer.addResponseInterceptor(new ResponseInterceptor() {
      @Override
      public void process(BrowserMobHttpResponse response, Har har) {
        log.info(">  " + response.getEntry().getRequest().getUrl());
        log.info("< " + response.getRawResponse().getStatusLine().toString());
      }
    });

    proxyServer.newHar("some-har");

    WebDriverRunner.setProxy(proxyServer.seleniumProxy());
    
    open("/file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    assertEquals(2, server.uploadedFiles.size());

    assertEquals(2, requestCounter);
    
    List<HarEntry> harEntries = proxyServer.getHar().getLog().getEntries();
    Set<String> requestedUrls = new HashSet<>();
    for (HarEntry harEntry : harEntries) {
      requestedUrls.add(harEntry.getRequest().getUrl());
    }
    assertTrue(requestedUrls.toString(), requestedUrls.contains(Configuration.baseUrl + "/file_upload_form.html"));
    assertTrue(requestedUrls.toString(), requestedUrls.contains(Configuration.baseUrl + "/upload"));
  }
}
