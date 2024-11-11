package com.codeborne.selenide.ex;

import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;

import java.util.List;

public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(Alias alias, By searchCriteria, WebElementCondition expectedCondition) {
    this(alias, searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(Alias alias, String searchCriteria, WebElementCondition expectedCondition) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition));
  }

  public ElementNotFound(Alias alias, String searchCriteria, WebElementCondition expectedCondition,
                         @Nullable Throwable cause) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition), cause);
  }

  public ElementNotFound(CollectionSource collection, List<String> expectedTexts, @Nullable Throwable cause) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", collection.getAlias().appendable(), collection.getSearchCriteria(), expectedTexts), cause);
  }

  public ElementNotFound(CollectionSource collection, String description, long timeoutMs, @Nullable Throwable cause) {
    super(String.format("Element%s not found {%s}%nExpected: %s",
        collection.getAlias().appendable(), collection.getSearchCriteria(), description),
      timeoutMs,
      cause);
  }
}
