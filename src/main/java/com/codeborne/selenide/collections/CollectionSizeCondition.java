package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

abstract class CollectionSizeCondition extends WebElementsCondition {
  private final String operator;
  protected final int expectedSize;

  CollectionSizeCondition(String operator, int expectedSize) {
    this.operator = operator;
    this.expectedSize = expectedSize;
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    int size = elements.size();
    return new CheckResult(apply(size), size);
  }

  @Override
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    int actualSize = lastCheckResult.getActualValueOrElse(0);
    throw new ListSizeMismatch(operator, expectedSize, actualSize, explanation, collection, cause, timeoutMs);
  }

  @Override
  public boolean missingElementsSatisfyCondition() {
    return apply(0);
  }

  protected abstract boolean apply(int size);
}
