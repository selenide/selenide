package integration;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.Credentials;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BasicAuthViaProxyTest extends ProxyIntegrationTest {
  @Test
  void canPassBasicAuth_via_proxy() {
    open("/basic-auth/hello", domain(), "scott", basicAuthPassword());
    $("#greeting").shouldHave(text("Hello, scott:" + basicAuthPassword() + "!"));
  }

  @Test
  void canAuthUsingProxyWithCredentials() {
    Credentials credentials = new BasicAuthCredentials(domain(), "scott", basicAuthPassword());
    open("/basic-auth/hello", BASIC, credentials);
    $("#greeting").shouldHave(text("Hello, scott:" + basicAuthPassword() + '!'));
    $("#bye").click();
    $("#greeting").shouldHave(text("bye, scott:" + basicAuthPassword() + '!'));
  }

  @Test
  void canSwitchToAnotherBasicAuth() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", basicAuthPassword()));
    $("#greeting").shouldHave(text("Hello, scott:" + basicAuthPassword() + '!'));
    open("/basic-auth/hello2", BASIC, new BasicAuthCredentials(domain(), "scott2", "tiger2"));
    $("#greeting").shouldHave(text("Hello2, scott2:tiger2!"));
  }

  @Test
  void removesPreviousBasicAuthHeaders() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", basicAuthPassword()));
    $("#greeting").shouldHave(text("Hello, scott:" + basicAuthPassword() + '!'));
    open("/headers/hello3");
    $("body")
      .shouldHave(partialText("Hello3"), partialText("Accept="), partialText("User-Agent="))
      .shouldNotHave(partialText("Authorization="));
  }
}
