package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomErrorMessageTest extends IntegrationTest {
  @Test
  void errorMessageContainsPageUrl() {
    Configuration.timeout = 2;
    open("about:blank");
    assertThatThrownBy(() -> $("body").shouldHave(text("Hello 100K")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have text \"Hello 100K\" {body}")
      .hasMessageContaining("Actual value: text=\"\"")
      .hasMessageContaining("Screenshot: file:/")
      .hasMessageContaining("Page source: file:/")
      .hasMessageContaining("Timeout: 2 ms.")
      .hasMessageContaining("Page url: about:blank");
  }
}
