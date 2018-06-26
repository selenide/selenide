package integration.proxy;

import java.util.ArrayList;
import java.util.List;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import integration.IntegrationTest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static org.hamcrest.Matchers.containsString;

class ProxyServerUsageTest extends IntegrationTest {
  private List<String> requests = new ArrayList<>();
  private List<String> responses = new ArrayList<>();

  @BeforeEach
  @AfterEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void canAddInterceptorsToProxyServer() {
    openFile("file_upload_form.html");

    SelenideProxyServer selenideProxy = getSelenideProxy();

    selenideProxy.addRequestFilter("proxy-usages.request", (request, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!isChromeOwnTechnicalRequest(url)) {
        requests.add(url + "\n\n" + contents.getTextContents());
      }
      return null;
    });
    selenideProxy.addResponseFilter("proxy-usages.response", (response, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!isChromeOwnTechnicalRequest(url)) {
        responses.add(url + "\n\n" + contents.getTextContents());
      }
    });

    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#submit").click();

    Assertions.assertNotNull(getSelenideProxy().getProxy(), "Check browser mob proxy instance");

    Assertions.assertEquals(1, requests.size(), "All requests: " + requests);
    Assertions.assertEquals(1, responses.size(), "All responses: " + responses);

    MatcherAssert.assertThat(requests.get(0), containsString("/upload"));
    MatcherAssert.assertThat(requests.get(0),
      containsString("Content-Disposition: form-data; name=\"cv\"; filename=\"hello_world.txt\""));
    MatcherAssert.assertThat(requests.get(0), containsString("Hello, WinRar!"));

    MatcherAssert.assertThat(responses.get(0), containsString("<h3>Uploaded 1 files</h3>"));
  }

  private boolean isChromeOwnTechnicalRequest(String url) {
    return url.contains("gstatic.com") || url.contains("google.com");
  }
}
