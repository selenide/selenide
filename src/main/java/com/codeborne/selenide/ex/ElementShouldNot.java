package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.System.lineSeparator;

public class ElementShouldNot extends UIAssertionError {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  public ElementShouldNot(Driver driver, String searchCriteria, String prefix, Condition expectedCondition,
                          WebElement element, Throwable lastError) {
    super(driver,
      String.format("Element should not %s%s {%s}%sElement: '%s'%s",
        prefix, expectedCondition, searchCriteria, lineSeparator(),
        describe.fully(driver, element),
        actualValue(expectedCondition, driver, element)
      ), lastError);
  }
}
