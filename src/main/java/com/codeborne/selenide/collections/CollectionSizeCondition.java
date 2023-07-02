package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
abstract class CollectionSizeCondition extends CollectionCondition {
  private final String operator;
  protected final int expectedSize;

  CollectionSizeCondition(String operator, int expectedSize) {
    this.operator = operator;
    this.expectedSize = expectedSize;
  }

  @Override
  @Nonnull
  @CheckReturnValue
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
  public boolean missingElementSatisfiesCondition() {
    return apply(0);
  }

  protected abstract boolean apply(int size);
}
