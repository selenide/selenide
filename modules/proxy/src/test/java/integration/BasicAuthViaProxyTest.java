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
    open("/basic-auth/hello", "", "scott", "tiger");
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  void canAuthUsingProxyWithLoginAndPassword() {
    open("/basic-auth/hello", BASIC, "scott", "tiger");
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  void canAuthUsingProxyWithCredentials() {
    Credentials credentials = new BasicAuthCredentials("scott", "tiger");
    open("/basic-auth/hello", BASIC, credentials);
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
    $("#bye").click();
    $("#greeting").shouldHave(text("bye, scott:tiger!"));
  }

  @Test
  void canSwitchToAnotherBasicAuth() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials("scott", "tiger"));
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
    open("/basic-auth/hello2", BASIC, new BasicAuthCredentials("scott2", "tiger2"));
    $("#greeting").shouldHave(text("Hello2, scott2:tiger2!"));
  }

  @Test
  void removesPreviousBasicAuthHeaders() {
    open("/basic-auth/hello", BASIC, new BasicAuthCredentials("scott", "tiger"));
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
    open("/headers/hello3");
    $("body")
      .shouldHave(partialText("Hello3"), partialText("Accept="), partialText("User-Agent="))
      .shouldNotHave(partialText("Authorization="));
  }
}
