package integration.proxy;

import integration.IntegrationTest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
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

    getSelenideProxy().addRequestFilter("proxy-usages.request", new RequestFilter() {
      @Override
      public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
        if (!messageInfo.getUrl().contains("gstatic.com")) {
          requests.add(messageInfo.getUrl() + "\n\n" + contents.getTextContents());
        }
        return null;
      }
    });
    getSelenideProxy().addResponseFilter("proxy-usages.response", new ResponseFilter() {
      @Override
      public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
        if (!messageInfo.getUrl().contains("gstatic.com")) {
          responses.add(messageInfo.getUrl() + "\n\n" + contents.getTextContents());
        }
      }
    });

    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#submit").click();

    assertEquals("All requests: " + requests, 1, requests.size());
    assertEquals("All responses: " + responses, 1, responses.size());

    assertThat(requests.get(0), containsString("/upload"));
    assertThat(requests.get(0), containsString("Content-Disposition: form-data; name=\"cv\"; filename=\"hello_world.txt\""));
    assertThat(requests.get(0), containsString("Hello, WinRar!"));

    assertThat(responses.get(0), containsString("<h3>Uploaded 1 files</h3>"));
  }
}
