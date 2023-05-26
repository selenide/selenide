package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SizeNotEqual extends CollectionCondition {
  protected final int expectedSize;

  public SizeNotEqual(int expectedSize) {
    this.expectedSize = expectedSize;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    int size = elements.size();
    return new CheckResult(apply(size), elements);
  }

  @Override
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    throw new ListSizeMismatch("<>", expectedSize, explanation, collection, lastCheckResult.getActualValue(), cause, timeoutMs);
  }

  @Override
  public boolean missingElementSatisfiesCondition() {
    return apply(0);
  }

  @Override
  public String toString() {
    return String.format("size <> %s", expectedSize);
  }

  private boolean apply(int size) {
    return size != expectedSize;
  }
}
