package integration;
import com.codeborne.selenide.WebDriverRunner;
import com.github.kklisura.cdt.protocol.commands.Fetch;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.types.fetch.ResponseBody;
import com.github.kklisura.cdt.protocol.types.network.Request;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.impl.ChromeServiceImpl;
import com.github.kklisura.cdt.services.types.ChromeTab;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

class FetchTest {
  private static final Logger log = LoggerFactory.getLogger(FetchTest.class);
  private final Map<String, Request> requests = new ConcurrentHashMap<>();

  @Test
  public void testCdpResponses() {
    open("about:blank");
    int debuggerPort = getDebuggerPort();
    ChromeDevToolsService cdpService = getChromeDevToolsService(debuggerPort);
    Fetch fetch = cdpService.getFetch();

    Set<String> requests = new HashSet<>();
    fetch.onRequestPaused(e -> {
      log.info("Request paused: {} {}", e.getRequestId(), e.getResourceType());
      fetch.continueRequest(e.getRequestId());
      requests.add(e.getRequestId());
    });


    open("http://the-internet.herokuapp.com/download");
    sleep(2000);
    fetch.enable();
    $(By.linkText("some-file.txt")).click();
    sleep(5000);
    for (String request : requests) {
      ResponseBody body = fetch.getResponseBody(request);
      log.info("BODY: {}", body);
    }
    sleep(2000);
  }

  private ChromeDevToolsService getChromeDevToolsService(int debuggerPort) {
    ChromeService chromeService = new ChromeServiceImpl(debuggerPort);
    ChromeTab pageTab = chromeService.getTabs().stream().filter(tab -> tab.getType().equals("page")).findFirst().get();
    return chromeService.createDevToolsService(pageTab);
  }

  private int getDebuggerPort() {
    WebDriver webDriver = WebDriverRunner.getWebDriver();
    Capabilities caps = ((RemoteWebDriver) webDriver).getCapabilities();
    String debuggerAddress = (String) ((Map<String, Object>) caps.getCapability("goog:chromeOptions")).get("debuggerAddress");
    return Integer.parseInt(debuggerAddress.split(":")[1]);
  }
}
