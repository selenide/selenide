package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class ElementShould extends UIAssertionError {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  public ElementShould(Driver driver, String searchCriteria, String prefix,
                       Condition expectedCondition, @Nullable CheckResult lastCheckResult,
                       WebElement element, @Nullable Throwable lastError) {
    super(
      String.format("Element should %s%s {%s}%nElement: '%s'%s",
        prefix, expectedCondition, searchCriteria,
        describe.fully(driver, element),
        actualValue(expectedCondition, driver, element, lastCheckResult)
      ),
      expectedCondition,
      lastCheckResult == null ? null : lastCheckResult.actualValue,
      lastError);
  }
}
