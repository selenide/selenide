package integration;

import com.codeborne.selenide.BearerTokenCredentials;
import com.codeborne.selenide.Credentials;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.AuthenticationType.BEARER;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BearerTokenAuthViaProxyTest extends ProxyIntegrationTest {
  @Test
  void canAuthUsingProxyWithCredentials() {
    Credentials credentials = new BearerTokenCredentials("token-123");
    open("/bearer-token-auth/hello", BEARER, credentials);
    $("#greeting").shouldHave(text("Hello, token-123!"));
    $("#bye").click();
    $("#greeting").shouldHave(text("bye, token-123!"));
  }

  @Test
  void canSwitchToAnotherBearerToken() {
    open("/bearer-token-auth/hello", BEARER, new BearerTokenCredentials("token-123"));
    $("#greeting").shouldHave(text("Hello, token-123!"));
    open("/bearer-token-auth/hello2", BEARER, new BearerTokenCredentials("token-456"));
    $("#greeting").shouldHave(text("Hello2, token-456!"));
  }

  @Test
  void removesPreviousAuthHeaders() {
    open("/bearer-token-auth/hello", BEARER, new BearerTokenCredentials("token-123"));
    $("#greeting").shouldHave(text("Hello, token-123!"));
    open("/headers/hello3");
    $("body")
      .shouldHave(partialText("Hello3"), partialText("Accept="), partialText("User-Agent="))
      .shouldNotHave(partialText("Authorization="));
  }

  @Test
  void invalidBearerToken() {
    open("/bearer-token-auth/hello", BEARER, new BearerTokenCredentials("foo-bar-789"));
    $("body").shouldHave(text("UNAUTHORIZED: Invalid bearer token foo-bar-789"));
  }
}
