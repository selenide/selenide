package integration.proxy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import integration.ProxyIntegrationTest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;

final class ProxyServerUsageTest extends ProxyIntegrationTest {
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
    selenideProxy.addRequestFilter("mock-server-response", (request, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (url.endsWith("page_with_dynamic_select.html")) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), OK, wrappedBuffer(mockedResponse()));
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        return response;
      }
      return null;
    });

    openFile("page_with_frames.html");
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("This is a fake response"));
  }

  private byte[] mockedResponse() {
    return "<html><body><h1>This is a fake response</h1></body></html>".getBytes(UTF_8);
  }

  private boolean isBrowserOwnTechnicalRequest(String url) {
    return !url.startsWith(Configuration.baseUrl) || url.contains("/favicon.ico");
  }
}
