package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(Driver driver, Alias alias, By searchCriteria, Condition expectedCondition) {
    this(driver, alias, searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(Driver driver, Alias alias, String searchCriteria, Condition expectedCondition) {
    super(driver, String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition));
  }

  public ElementNotFound(Driver driver, Alias alias, String searchCriteria, Condition expectedCondition,
                         @Nullable Throwable cause) {
    super(driver, String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition), cause);
  }

  public ElementNotFound(CollectionSource collection, List<String> expectedTexts, @Nullable Throwable cause) {
    super(collection.driver(), String.format("Element%s not found {%s}" +
      "%nExpected: %s", collection.getAlias().appendable(), collection.getSearchCriteria(), expectedTexts), cause);
  }

  public ElementNotFound(CollectionSource collection, String description, long timeoutMs, @Nullable Throwable cause) {
    super(collection.driver(), String.format("Element%s not found {%s}%nExpected: %s",
        collection.getAlias().appendable(), collection.getSearchCriteria(), description),
      timeoutMs,
      cause);
  }
}
