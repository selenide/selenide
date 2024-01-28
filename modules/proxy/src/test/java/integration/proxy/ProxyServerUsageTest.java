package integration.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.ResponseFilter;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.proxy.RequestMatcher.HttpMethod.GET;
import static com.codeborne.selenide.proxy.RequestMatchers.urlEndsWith;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ProxyServerUsageTest extends ProxyIntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ProxyServerUsageTest.class);
  private final List<String> requests = new ArrayList<>();
  private final List<String> responses = new ArrayList<>();

  @Test
  void canAddInterceptorsToProxyServer() {
    openFile("file_upload_form.html");

    SelenideProxyServer selenideProxy = getSelenideProxy();

    selenideProxy.addRequestFilter("proxy-usages.request", (request, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!isBrowserOwnTechnicalRequest(url)) {
        request.headers().add("User-Agent", "hacker");
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
      .as("Check browser up proxy instance")
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

  @Test
  void canMockServerResponse() {
    open();

    SelenideProxyServer selenideProxy = requireNonNull(getSelenideProxy());
    selenideProxy.responseMocker().mockText("selects-page-mock",
      urlEndsWith(GET, "page_with_dynamic_select.html"), this::mockedResponse);

    openFile("page_with_frames.html");
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("This is a fake response"));

    selenideProxy.responseMocker().reset("selects-page-mock");
    refresh();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }

  private String mockedResponse() {
    return "<html><body><h1>This is a fake response</h1></body></html>";
  }

  @Test
  void canAddSameRequestFilterMultipleTimes() {
    openFile("file_upload_form.html");
    SelenideProxyServer selenideProxy = getSelenideProxy();
    RequestFilter requestLogger = (request, contents, messageInfo) -> {
      log.info("Request {} {}", messageInfo.getUrl(), contents.getTextContents());
      return null;
    };

    selenideProxy.addRequestFilter("proxy-usages.request-logger", requestLogger);
    selenideProxy.addRequestFilter("proxy-usages.request-logger", requestLogger);
  }

  @Test
  void canAddSameResponseFilterMultipleTimes() {
    openFile("file_upload_form.html");
    SelenideProxyServer selenideProxy = getSelenideProxy();
    ResponseFilter responseFilter = (response, contents, messageInfo) -> {
      log.info("Response {} {}", messageInfo.getUrl(), contents.getTextContents());
    };

    selenideProxy.addResponseFilter("proxy-usages.response-logger", responseFilter);
    selenideProxy.addResponseFilter("proxy-usages.response-logger", responseFilter);
  }

  @Test
  void cannotAddDifferentRequestFiltersWithSameName() {
    openFile("file_upload_form.html");
    SelenideProxyServer selenideProxy = getSelenideProxy();
    RequestFilter requestLogger1 = (request, contents, messageInfo) -> {
      log.info("Request {} {}", messageInfo.getUrl(), contents.getTextContents());
      return null;
    };
    RequestFilter requestLogger2 = (request, contents, messageInfo) -> {
      log.info("Request {} {}", messageInfo.getUrl(), contents.getTextContents());
      return null;
    };

    selenideProxy.addRequestFilter("proxy-usages.auth-request", requestLogger1);
    assertThatThrownBy(() -> selenideProxy.addRequestFilter("proxy-usages.auth-request", requestLogger2))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Duplicate request filter: proxy-usages.auth-request");
  }

  @Test
  void cannotAddDifferentResponseFiltersWithSameName() {
    openFile("file_upload_form.html");
    SelenideProxyServer selenideProxy = getSelenideProxy();
    ResponseFilter responseLogger1 = (request, contents, messageInfo) -> {
      log.info("Response {} {}", messageInfo.getUrl(), contents.getTextContents());
    };
    ResponseFilter responseLogger2 = (request, contents, messageInfo) -> {
      log.info("Response {} {}", messageInfo.getUrl(), contents.getTextContents());
    };

    selenideProxy.addResponseFilter("proxy-usages.auth-response", responseLogger1);
    assertThatThrownBy(() -> selenideProxy.addResponseFilter("proxy-usages.auth-response", responseLogger2))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Duplicate response filter: proxy-usages.auth-response");
  }

  private boolean isBrowserOwnTechnicalRequest(String url) {
    return !url.startsWith(Configuration.baseUrl) || url.contains("/favicon.ico");
  }

  @AfterEach
  void tearDown() {
    getSelenideProxy().responseMocker().resetAll();
  }
}
