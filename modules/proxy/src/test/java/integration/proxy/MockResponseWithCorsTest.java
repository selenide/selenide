package integration.proxy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.MockResponseFilter;
import integration.ProxyIntegrationTest;
import integration.server.LocalHttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.proxy.RequestMatcher.HttpMethod.POST;
import static com.codeborne.selenide.proxy.RequestMatchers.urlContains;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

final class MockResponseWithCorsTest extends ProxyIntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(MockResponseWithCorsTest.class);
  private LocalHttpServer corsProtectedService;

  @BeforeEach
  void setUp() throws Exception {
    String allowedOrigin = Configuration.baseUrl;
    corsProtectedService = LocalHttpServer.startWithRetry(true, allowedOrigin, emptyMap()).start();
    Configuration.timeout = 2000;

    log.info("Started AUT on {}", getBaseUrl());
    log.info("Started CORS-protected service on {}", corsProtectedService.getPort());
  }

  @Test
  void proxySupportsCors() {
    open("/page_with_cross-origin-request.html?anotherPort=" + corsProtectedService.getPort());
    $("#moria").shouldHave(text("[200] Say CORS and enter, friend Frodo!"));
  }

  @Test
  void canMockServerResponse() {
    open();
    proxyMocker().mockText("cors-mock", urlContains(POST, "/try-cors/Frodo"), this::mockedResponse);
    open("/page_with_cross-origin-request.html?anotherPort=" + corsProtectedService.getPort());
    $("#moria").shouldHave(text("[200] You hacked the CORS, Frodo!"));
  }

  @Test
  void canMockServerResponseWithAnyHttpStatus() {
    open();
    proxyMocker().mockText("cors-mock", urlContains(POST, "/try-cors/Frodo"), 429, this::mockedResponse);
    open("/page_with_cross-origin-request.html?anotherPort=" + corsProtectedService.getPort());
    $("#moria").shouldHave(text("[429] You hacked the CORS, Frodo!"));
  }

  private String mockedResponse() {
    return "You hacked the CORS, Frodo!";
  }

  @AfterEach
  void tearDown() throws Exception {
    proxyMocker().resetAll();
    if (corsProtectedService != null) {
      corsProtectedService.stop();
    }
  }

  private static MockResponseFilter proxyMocker() {
    return requireNonNull(getSelenideProxy()).responseMocker();
  }
}
