package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.Strings.join;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class ElementShouldNot extends UIAssertionError {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  public ElementShouldNot(Driver driver, Alias alias, String searchCriteria, String prefix,
                          Condition expectedCondition, @Nullable CheckResult lastCheckResult,
                          WebElement element, @Nullable Throwable cause) {
    super(
      driver,
      join(
        String.format("Element%s should not %s%s {%s}", alias.appendable(), prefix, expectedCondition, searchCriteria),
        String.format("Element: '%s'", describe.fully(driver, element)),
        errorFormatter.actualValue(expectedCondition, driver, element, lastCheckResult)
      ),
      expectedCondition,
      lastCheckResult == null ? null : lastCheckResult.actualValue(),
      cause);
  }
}
