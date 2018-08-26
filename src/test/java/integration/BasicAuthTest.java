package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Configuration.FileDownloadMode;
import com.codeborne.selenide.Credentials;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuth() {
    open("/basic-auth/hello", "", "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  void canAuthUsingProxyWithLoginAndPassword() {
    switchToDownloadMode(PROXY);
    open("/basic-auth/hello", AuthenticationType.BASIC, "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  void canAuthUsingProxyWithCredentials() {
    switchToDownloadMode(PROXY);
    Credentials credentials = new Credentials("scott", "tiger");
    open("/basic-auth/hello", AuthenticationType.BASIC, credentials);
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }
}
