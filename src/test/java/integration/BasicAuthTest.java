package integration;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuth() {
    open("/basic-auth/hello", "", "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }
}
