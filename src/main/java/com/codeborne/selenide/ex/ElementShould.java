package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.ElementDescriber;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.Strings.join;
import static com.codeborne.selenide.impl.Plugins.inject;

public class ElementShould extends UIAssertionError {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  public ElementShould(Driver driver, Alias alias, String searchCriteria, String prefix,
                       WebElementCondition expectedCondition, @Nullable CheckResult lastCheckResult,
                       WebElement element, @Nullable Throwable cause) {
    super(
      join(
        String.format("Element%s should %s%s {%s}", alias.appendable(), prefix, expectedCondition, searchCriteria),
        String.format("Element: '%s'", describe.fully(driver, element)),
        errorFormatter.actualValue(expectedCondition, driver, element, lastCheckResult)
      ),
      expectedCondition,
      lastCheckResult == null ? null : lastCheckResult.actualValue(),
      cause);
  }
}
