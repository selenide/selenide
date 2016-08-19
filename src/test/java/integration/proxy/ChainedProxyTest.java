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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Selenide runs its own proxy server.
 * User can also configure Selenide to use his proxy server (for Selenide, this is "chained" proxy).
 * 
 * This test verifies that both these proxies work well together.  
 */
public class ChainedProxyTest extends IntegrationTest {
  static BrowserMobProxy chainedProxy;
  List<String> visitedUrls = new ArrayList<>();
  
  @Before
  public void setUp() throws UnknownHostException {
    if (chainedProxy == null) {
      close();

      chainedProxy = new BrowserMobProxyServer();
      chainedProxy.setTrustAllServers(true);
      chainedProxy.start(0);

      chainedProxy.addResponseFilter(new ResponseFilter() {
        @Override
        public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
          System.out.println(response.getStatus().code());
          visitedUrls.add(messageInfo.getUrl());
        }
      });

      Proxy seleniumProxy = ClientUtil.createSeleniumProxy(chainedProxy);
      WebDriverRunner.setProxy(seleniumProxy);
    }
  }

  @AfterClass
  public static void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
    if (chainedProxy != null) {
      chainedProxy.stop();
    }
  }

  @Test
  public void remote_http() {
    open("http://google.com");
    $(By.name("q")).shouldBe(visible, enabled);
    assertThat(visitedUrls.size(), is(1));
  }

  @Test
  public void remote_https() {
    open("https://google.com");
    $(By.name("q")).shouldBe(visible, enabled);
    assertThat(visitedUrls.size(), is(1));
  }

  @Test
  public void local_https() {
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    assertEquals(2, server.uploadedFiles.size());
    assertThat(visitedUrls.size(), is(1));
  }

  @Test
  public void downloadExternalFile() throws FileNotFoundException {
    open("http://the-internet.herokuapp.com/download");
    File video = $(By.linkText("some-file.txt")).download();
    assertEquals("some-file.txt", video.getName());
    assertThat(visitedUrls.size(), is(1));
    assertThat(visitedUrls.get(0), is("http://the-internet.herokuapp.com/download/some-file.txt"));
  }
}
