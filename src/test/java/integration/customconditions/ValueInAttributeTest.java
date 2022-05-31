package integration.customconditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementShould;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ParametersAreNonnullByDefault
final class ValueInAttributeTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canUseCustomCondition() {
    $("#non-clickable-element a").shouldHave(valueInAttribute("href", "google"));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $("#non-clickable-element a").shouldHave(valueInAttribute("href", "yandex")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have value 'yandex' in attribute 'href' {#non-clickable-element a}")
      .hasMessageContaining("Element: '<a href=\"http://google.com\">non-clickable element</a>'")
      .hasMessageContaining("Actual value: http://google.com/");
  }

  private static Condition valueInAttribute(String attributeName, String value) {
    return new Condition(String.format("value '%s' in attribute '%s'", value, attributeName)) {
      @Override
      @Nonnull
      public CheckResult check(Driver driver, WebElement element) {
        String attr = element.getAttribute(attributeName);
        String attributeValue = attr == null ? "" : attr;
        return new CheckResult(attributeValue.contains(value), attributeValue);
      }
    };
  }
}
