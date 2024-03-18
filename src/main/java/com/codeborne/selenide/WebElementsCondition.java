package com.codeborne.selenide;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public abstract class WebElementsCondition {

  protected String explanation;

  @Nonnull
  @CheckReturnValue
  public CheckResult check(Driver driver, List<WebElement> elements) {
    throw new UnsupportedOperationException("Implement one of 'check' methods in your condition");
  }

  /**
   * The most powerful way to implement condition.
   * Can check the collection using JavaScript or any other effective means.
   * Also, can return "actual values" in the returned {@link CheckResult} object.
   */
  @Nonnull
  @CheckReturnValue
  public CheckResult check(CollectionSource collection) {
    List<WebElement> elements = collection.getElements();
    return check(collection.driver(), elements);
  }

  /**
   * Override this method if you want to customize error class or description
   */
  public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
    throw new UIAssertionError(collection.driver(),
      errorMessage() +
      lineSeparator() + "Actual: " + lastCheckResult.getActualValue() +
      lineSeparator() + "Expected: " + expectedValue() +
      (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
      lineSeparator() + "Collection: " + collection.description(),
      toString(), lastCheckResult.getActualValue()
    );
  }

  public String errorMessage() {
    return "Collection check failed";
  }

  public String expectedValue() {
    return toString();
  }

  @Override
  public abstract String toString();

  /**
   * Use for explaining the reason of condition:
   * WHY you think this collection should match that condition?
   */
  public WebElementsCondition because(String explanation) {
    this.explanation = explanation;
    return this;
  }

  public boolean missingElementsSatisfyCondition() {
    return false;
  }

  /**
   * Using "or" checks in tests is probably a flag of bad test design.
   * Consider splitting this "or" check into two different methods or tests.
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  public WebElementsCondition or(WebElementsCondition alternative) {
    return new WebElementsCondition() {
      @Nonnull
      @Override
      public CheckResult check(CollectionSource collection) {
        CheckResult r1 = WebElementsCondition.this.check(collection);
        return r1.verdict() == ACCEPT ? r1 : alternative.check(collection);
      }

      @Override
      public String toString() {
        return "%s OR %s".formatted(WebElementsCondition.this, alternative);
      }
    };
  }
}
