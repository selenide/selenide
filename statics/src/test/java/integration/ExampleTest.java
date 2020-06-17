package integration;
import com.codeborne.selenide.WebDriverRunner;
import com.github.kklisura.cdt.protocol.commands.Network;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

class ExampleTest {
  Map<String, Request> requests = new ConcurrentHashMap<>();

  @Test
  public void testCdpResponses() {
    open("about:blank");
    int debuggerPort = getDebuggerPort();
    ChromeDevToolsService cdpService = getChromeDevToolsService(debuggerPort);
    Network network = cdpService.getNetwork();

    network.onRequestWillBeSent(e -> {
      requests.put(e.getRequestId(), e.getRequest());
    });
    network.onResponseReceived(
      e -> {
        System.out.println("GOT response " + e.getRequestId() + " " + e.getResponse().getUrl() + "  "  + e.getType() + " " + e.getResponse().getStatus());
        Request request = requests.get(e.getRequestId());
        if (request != null) {
          String body = network.getResponseBody(e.getRequestId()).getBody();
          System.out.println("GOT response " + request.getUrl() + "  ->  " + body);
        }
      });
    network.onLoadingFinished(e -> {
      System.out.println("Loading finished: " + e.getRequestId());
    });
    network.onLoadingFailed(e -> {
      System.out.println("Loading finished: " + e.getRequestId() + "  " + requests.get(e.getRequestId()).getUrl() + "    " +
        e.getErrorText() + " " + e.getBlockedReason());
    });

    open("http://the-internet.herokuapp.com/download");
    sleep(2000);
    network.enable();
    $(By.linkText("some-file.txt")).click();
    sleep(5000);
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
