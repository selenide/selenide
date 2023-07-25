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
    String allowedDomains = String.format("localhost,%s,my.laptop.local", domain());
    open("/basic-auth/hello", allowedDomains, "scott", scottPassword());
    $("#greeting").shouldHave(text("Hello, scott:" + scottPassword()));
  }

  @Test
  void canAuthUsingProxyWithCredentials() {
    Credentials credentials = new BasicAuthCredentials(domain(), "scott", scottPassword());
    open("/basic-auth/hello", BASIC, credentials);
    $("#greeting").shouldHave(text("Hello, scott:" + scottPassword()));
    $("#bye").click();
    $("#greeting").shouldHave(text("bye, scott:" + scottPassword()));
  }

  @Test
  void canSwitchToAnotherBasicAuth() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", scottPassword()));
    $("#greeting").shouldHave(text("Hello, scott:" + scottPassword()));
    open("/basic-auth/hello2", BASIC, new BasicAuthCredentials(domain(), "john", johnPassword()));
    $("#greeting").shouldHave(text("Hello2, john:" + johnPassword()));
  }

  @Test
  void removesPreviousBasicAuthHeaders() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", scottPassword()));
    $("#greeting").shouldHave(text("Hello, scott:" + scottPassword()));
    open("/headers/hello3");
    $("body")
      .shouldHave(partialText("Hello3"), partialText("Accept="), partialText("User-Agent="))
      .shouldNotHave(partialText("Authorization="));
  }
}
