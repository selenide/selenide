package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(Driver driver, By searchCriteria, Condition expectedCondition) {
    this(driver, searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(Driver driver, String searchCriteria, Condition expectedCondition) {
    super(driver,
      String.format("Element not found {%s}" +
        "%nExpected: %s", searchCriteria, expectedCondition));
  }

  public ElementNotFound(Driver driver, String searchCriteria, Condition expectedCondition, @Nullable Throwable lastError) {
    super(driver,
      String.format("Element not found {%s}" +
        "%nExpected: %s", searchCriteria, expectedCondition), lastError);
  }

  public ElementNotFound(WebElementsCollection collection, List<String> expectedTexts, @Nullable Throwable lastError) {
    super(collection.driver(),
      String.format("Element not found {%s}" +
        "%nExpected: %s", collection.description(), expectedTexts), lastError);
  }

  public ElementNotFound(WebElementsCollection collection, String description, @Nullable Throwable lastError) {
    super(collection.driver(),
      String.format("Element not found {%s}" +
        "%nExpected: %s", collection.description(), description), lastError);
  }
}
