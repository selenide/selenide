package integration.proxy;

import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;

import java.net.UnknownHostException;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertEquals;

public class BrowserMobProxyServerTest extends IntegrationTest {
  static BrowserMobProxy proxy;

  @Before
  public void setUp() throws UnknownHostException {
    if (proxy == null) {
      close();

      proxy = new BrowserMobProxyServer();
      proxy.setTrustAllServers(true);
      proxy.start(0);

      proxy.addResponseFilter(new ResponseFilter() {
        @Override
        public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
          System.out.println(response.getStatus().code());
        }
      });

      Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
      WebDriverRunner.setProxy(seleniumProxy);
    }
  }

  @AfterClass
  public static void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
    if (proxy != null) {
      proxy.stop();
    }
  }

  @Test
  public void remote_http() {
    open("http://google.com");
    $(By.name("q")).shouldBe(visible, enabled);
  }

  @Test
  public void remote_https() {
    open("https://google.com");
    $(By.name("q")).shouldBe(visible, enabled);
  }

  @Test @Ignore
  public void local_https() {
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    assertEquals(2, server.uploadedFiles.size());
  }
}
