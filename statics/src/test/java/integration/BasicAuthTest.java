package integration;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

final class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuth_via_URL() {
    useProxy(false);
    open("/basic-auth/hello", "", "scott", "tiger");
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
  }
}
