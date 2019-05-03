package integration.proxy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;

class ProxyServerUsageTest extends IntegrationTest {
  private List<String> requests = new ArrayList<>();
  private List<String> responses = new ArrayList<>();

  @BeforeEach
  @AfterEach
  void setUp() {
    closeWebDriver();
    useProxy(true);
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void canAddInterceptorsToProxyServer() {
    openFile("file_upload_form.html");

    SelenideProxyServer selenideProxy = getSelenideProxy();

    selenideProxy.addRequestFilter("proxy-usages.request", (request, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!isBrowserOwnTechnicalRequest(url)) {
        requests.add(url + "\n\n" + contents.getTextContents());
      }
      return null;
    });
    selenideProxy.addResponseFilter("proxy-usages.response", (response, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!isBrowserOwnTechnicalRequest(url)) {
        responses.add(url + "\n\n" + contents.getTextContents());
      }
    });

    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#submit").click();

    assertThat(getSelenideProxy().getProxy())
      .as("Check browser mob proxy instance")
      .isNotNull();

    assertThat(requests)
      .withFailMessage("All requests: " + requests)
      .hasSize(1);
    assertThat(responses)
      .withFailMessage("All responses: " + responses)
      .hasSize(1);

    assertThat(requests.get(0)).contains("/upload");
    assertThat(requests.get(0)).contains("Content-Disposition: form-data; name=\"cv\"; filename=\"hello_world.txt\"");
    assertThat(requests.get(0)).contains("Hello, WinRar!");
    assertThat(responses.get(0)).contains("<h3>Uploaded 1 files</h3>");
  }

  private boolean isBrowserOwnTechnicalRequest(String url) {
    return !url.startsWith(Configuration.baseUrl) || url.contains("/favicon.ico");
  }
}
