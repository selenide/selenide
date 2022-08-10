package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

final class CssMethodsTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGetAllCssProperties() {
    Map<String, String> css = $("h1").css();
    assertThat(css).hasSizeBetween(200, 2_000);
    assertThat(css).containsEntry("display", "block");
    assertThat(css).containsEntry("opacity", "1");
  }

  @Test
  void canGetAllCssProperties_hiddenElement() {
    Map<String, String> css = $("#theHiddenElement").css();
    assertThat(css).containsEntry("display", "none");
    assertThat(css).containsEntry("opacity", "1");
  }

  @Test
  void canGetCssValue() {
    assertThat($("h1").getCssValue("display")).isEqualTo("block");
    assertThat($("#theHiddenElement").getCssValue("display")).isEqualTo("none");
  }

  @Test
  void canVerifyCssValue() {
    $("h1").shouldHave(cssValue("display", "block"));
    $("#theHiddenElement").shouldHave(cssValue("display", "none"));
  }
}
