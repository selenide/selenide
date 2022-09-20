package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(Alias alias, By searchCriteria, Condition expectedCondition) {
    this(alias, searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(Alias alias, String searchCriteria, Condition expectedCondition) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition));
  }

  public ElementNotFound(Alias alias, String searchCriteria, Condition expectedCondition, @Nullable Throwable lastError) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", alias.appendable(), searchCriteria, expectedCondition), lastError);
  }

  public ElementNotFound(CollectionSource collection, List<String> expectedTexts, @Nullable Throwable lastError) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", collection.getAlias().appendable(), collection.getSearchCriteria(), expectedTexts), lastError);
  }

  public ElementNotFound(CollectionSource collection, String description, @Nullable Throwable lastError) {
    super(String.format("Element%s not found {%s}" +
      "%nExpected: %s", collection.getAlias().appendable(), collection.getSearchCriteria(), description), lastError);
  }
}
