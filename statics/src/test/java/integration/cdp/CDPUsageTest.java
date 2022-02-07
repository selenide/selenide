package integration.cdp;

import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v96.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static integration.cdp.CDP.runWithDevTools;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.devtools.v96.network.Network.getRequestPostData;
import static org.openqa.selenium.devtools.v96.network.Network.getResponseBody;

/**
 * Similar to ProxyServerUsageTest, but using CDP instead of proxy to intercept network requests
 */
final class CDPUsageTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(CDPUsageTest.class);
  private final List<String> requests = new ArrayList<>();
  private final List<String> responses = new ArrayList<>();
  private final ExecutorService threadPool = newFixedThreadPool(5);

  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();
  }

  @AfterEach
  void tearDown() {
    threadPool.shutdown();
  }

  @Test
  void canAddListenersToNetworkRequests() throws Exception {
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

        threadPool.submit(() -> {
          sleep(500);
          try {
            String body = devTools.send(getResponseBody(responseReceived.getRequestId())).getBody();
            responses.add(responseReceived.getResponse().getUrl() + "\n\n" + body);
          }
          catch (DevToolsException failedToGetResponseBody) {
            log.error("failed to get response body for {}, from cache: {}",
              responseReceived.getResponse().getUrl(),
              responseReceived.getResponse().getFromDiskCache(),
              failedToGetResponseBody);
            responses.add(responseReceived.getResponse().getUrl() + "\n\n" + failedToGetResponseBody);
          }
        });
      });

      $("#cv").uploadFromClasspath("hello_world.txt");
      $("#submit").click();

      threadPool.awaitTermination(5, SECONDS);
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
}
