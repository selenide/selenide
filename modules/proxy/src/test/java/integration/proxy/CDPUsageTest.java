package integration.proxy;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v96.network.Network;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.devtools.v96.network.Network.getRequestPostData;
import static org.openqa.selenium.devtools.v96.network.Network.getResponseBody;

/**
 * Similar to ProxyServerUsageTest, but using CDP instead of proxy to intercept network requests
 */
final class CDPUsageTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(CDPUsageTest.class);
  private final List<String> requests = new ArrayList<>();
  private final List<String> responses = new ArrayList<>();

  @Test
  void canAddListenersToNetworkRequests() {
    openFile("file_upload_form.html");

    runWithDevTools(devTools -> {
      devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

      devTools.addListener(Network.requestWillBeSent(), request -> {
        log.info("request: {}", request.getDocumentURL());
        String requestBody = devTools.send(getRequestPostData(request.getRequestId()));
        requests.add(request.getDocumentURL() + "\n\n" + requestBody);
      });

      devTools.addListener(Network.responseReceived(), responseReceived -> {
        log.info("response: {}", responseReceived.getResponse().getUrl());

        String body = devTools.send(getResponseBody(responseReceived.getRequestId())).getBody();
        responses.add(responseReceived.getResponse().getUrl() + "\n\n" + body);
      });

      $("#cv").uploadFromClasspath("hello_world.txt");
      $("#submit").click();

      assertThat(requests)
        .withFailMessage("All requests: " + requests)
        .hasSize(1);
      assertThat(responses)
        .withFailMessage("All responses: " + responses)
        .hasSize(1);

      assertThat(requests.get(0)).contains("/upload");
      assertThat(requests.get(0)).contains("Content-Disposition: form-data; name=\"cv\"; filename=\"hello_world.txt\"");
      assertThat(responses.get(0)).contains("<h3>Uploaded 1 files</h3>");
    });
  }

  private void runWithDevTools(Consumer<DevTools> block) {
    DevTools devTools = getDevTools();
    devTools.createSession();

    try {
      block.accept(devTools);
    }
    finally {
      devTools.disconnectSession();
    }
  }

  private DevTools getDevTools() {
    WebDriver webDriver = getWebDriver();
    if (webDriver instanceof HasDevTools) {
      return ((HasDevTools) webDriver).getDevTools();
    }
    else {
      WebDriver augmentedDriver = new Augmenter().augment(getWebDriver());
      return ((HasDevTools) augmentedDriver).getDevTools();
    }
  }
}
