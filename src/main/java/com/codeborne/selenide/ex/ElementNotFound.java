package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(By searchCriteria, Condition expectedCondition) {
    this(searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(String searchCriteria, Condition expectedCondition) {
    super(String.format("Element not found {%s}" +
      "%nExpected: %s", searchCriteria, expectedCondition));
  }

  public ElementNotFound(String searchCriteria, Condition expectedCondition, @Nullable Throwable lastError) {
    super(String.format("Element not found {%s}" +
      "%nExpected: %s", searchCriteria, expectedCondition), lastError);
  }

  public ElementNotFound(CollectionSource collection, List<String> expectedTexts, @Nullable Throwable lastError) {
    super(String.format("Element not found {%s}" +
      "%nExpected: %s", collection.description(), expectedTexts), lastError);
  }

  public ElementNotFound(CollectionSource collection, String description, @Nullable Throwable lastError) {
    super(String.format("Element not found {%s}" +
      "%nExpected: %s", collection.description(), description), lastError);
  }
}
