package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuth_via_URL() {
    useProxy(false);
    open("/basic-auth/hello", "", "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void canPassBasicAuth_via_proxy() {
    useProxy(true);
    open("/basic-auth/hello", "", "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void canAuthUsingProxyWithLoginAndPassword() {
    useProxy(true);
    open("/basic-auth/hello", AuthenticationType.BASIC, "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void canAuthUsingProxyWithCredentials() {
    useProxy(true);
    Credentials credentials = new Credentials("scott", "tiger");
    open("/basic-auth/hello", AuthenticationType.BASIC, credentials);
    $("body").shouldHave(text("Hello, scott:tiger!"));
    $("#bye").click();
    $("body").shouldHave(text("bye, scott:tiger!"));
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void canSwitchToAnotherBasicAuth() {
    useProxy(true);
    open("/basic-auth/hello", AuthenticationType.BASIC, new Credentials("scott", "tiger"));
    $("body").shouldHave(text("Hello, scott:tiger!"));
    open("/basic-auth/hello2", AuthenticationType.BASIC, new Credentials("scott2", "tiger2"));
    $("body").shouldHave(text("Hello2, scott2:tiger2!"));
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  void removesPreviousBasicAuthHeaders() {
    useProxy(true);
    open("/basic-auth/hello", AuthenticationType.BASIC, new Credentials("scott", "tiger"));
    $("body").shouldHave(text("Hello, scott:tiger!"));
    open("/headers/hello3");
    $("body")
      .shouldHave(text("Hello3"), text("Accept="), text("User-Agent="))
      .shouldNotHave(text("Authorization="));
  }
}
