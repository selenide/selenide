package integration.proxy;

import java.util.ArrayList;
import java.util.List;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenide runs its own proxy server.
 * User can also configure Selenide to use his proxy server (for Selenide, this is "chained" proxy).
 * <p>
 * This test verifies that both these proxies work well together.
 */
class ChainedProxyTest extends IntegrationTest {
  private static BrowserMobProxy chainedProxy;
  private List<String> visitedUrls = new ArrayList<>();

  @AfterAll
  static void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
    if (chainedProxy != null) {
      chainedProxy.stop();
    }
  }

  @BeforeEach
  void setUp() {
    Assumptions.assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...
    Assumptions.assumeFalse(isHtmlUnit()); // Why it's not working? It's magic for me...

    if (chainedProxy == null) {
      close();

      chainedProxy = new BrowserMobProxyServer();
      chainedProxy.setTrustAllServers(true);
      chainedProxy.start(0);

      chainedProxy.addResponseFilter(new ResponseFilter() {
        @Override
        public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
          if (messageInfo.getUrl().startsWith(Configuration.baseUrl)) {
            visitedUrls.add(messageInfo.getUrl());
          }
        }
      });

      Proxy seleniumProxy = ClientUtil.createSeleniumProxy(chainedProxy);
      WebDriverRunner.setProxy(seleniumProxy);
    }
    visitedUrls.clear();
  }

  @Test
  void selenideProxyCanWorkWithUserProvidedChainedProxy() {
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();

    // Assert that files are actually uploaded via 2 proxies
    $("h3").shouldHave(text("Uploaded 2 files"));
    assertEquals(2, server.uploadedFiles.size());

    // Assert that "chained" proxy has intercepted requests
    assertTrue(visitedUrls.size() >= 2, "Expected at least 2 urls, but got: " + visitedUrls);
    assertThat(visitedUrls.get(0), containsString("/file_upload_form.html"));
    assertThat(visitedUrls.get(visitedUrls.size() - 1), containsString("/upload"));
  }
}
