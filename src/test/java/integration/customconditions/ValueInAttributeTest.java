package integration.customconditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementShould;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    assertThatThrownBy(() -> $("#non-clickable-element a").shouldHave(valueInAttribute("href", "duckduckgo.")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have value 'duckduckgo.' in attribute 'href' {#non-clickable-element a}")
      .hasMessageContaining("Element: '<a href=\"https://google.com\">non-clickable element</a>'")
      .hasMessageContaining("Actual value: https://google.com/");
  }

  private static WebElementCondition valueInAttribute(String attributeName, String value) {
    return new WebElementCondition(String.format("value '%s' in attribute '%s'", value, attributeName)) {
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        String attr = element.getAttribute(attributeName);
        String attributeValue = attr == null ? "" : attr;
        return new CheckResult(attributeValue.contains(value), attributeValue);
      }
    };
  }
}
