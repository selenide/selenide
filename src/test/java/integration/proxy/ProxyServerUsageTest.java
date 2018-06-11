package integration.proxy;

import java.util.ArrayList;
import java.util.List;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ProxyServerUsageTest extends IntegrationTest {
  List<String> requests = new ArrayList<>();
  List<String> responses = new ArrayList<>();

  @Before
  @After
  public void setUp() {
    closeWebDriver();
  }

  @Test
  public void canAddInterceptorsToProxyServer() {
    openFile("file_upload_form.html");

    SelenideProxyServer selenideProxy = getSelenideProxy();

    selenideProxy.addRequestFilter("proxy-usages.request", (request, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!url.contains("gstatic.com") || !url.contains("google.com")) {
        requests.add(url + "\n\n" + contents.getTextContents());
      }
      return null;
    });
    selenideProxy.addResponseFilter("proxy-usages.response", (response, contents, messageInfo) -> {
      String url = messageInfo.getUrl();
      if (!url.contains("gstatic.com")|| !url.contains("google.com")) {
        responses.add(url + "\n\n" + contents.getTextContents());
      }
    });

    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#submit").click();

    assertNotNull("Check browser mob proxy instance", getSelenideProxy().getProxy());

    assertEquals("All requests: " + requests, 1, requests.size());
    assertEquals("All responses: " + responses, 1, responses.size());

    assertThat(requests.get(0), containsString("/upload"));
    assertThat(requests.get(0), containsString("Content-Disposition: form-data; name=\"cv\"; filename=\"hello_world.txt\""));
    assertThat(requests.get(0), containsString("Hello, WinRar!"));

    assertThat(responses.get(0), containsString("<h3>Uploaded 1 files</h3>"));
  }
}
