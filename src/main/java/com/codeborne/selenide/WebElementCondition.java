package com.codeborne.selenide;

import com.codeborne.selenide.conditions.ExplainedCondition;
import com.codeborne.selenide.conditions.Not;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;

public abstract class WebElementCondition {
  protected final String name;
  protected final boolean missingElementSatisfiesCondition;

  protected WebElementCondition(String name) {
    this(name, false);
  }

  protected WebElementCondition(String name, boolean missingElementSatisfiesCondition) {
    this.name = name;
    this.missingElementSatisfiesCondition = missingElementSatisfiesCondition;
  }

  /**
   * Check if given element matches this condition
   *
   * @param driver  selenide driver
   * @param element given WebElement
   * @return {@link CheckResult.Verdict#ACCEPT} if element matches condition, or
   *         {@link CheckResult.Verdict#REJECT} if element doesn't match (and we should keep trying until timeout).
   */
  public abstract CheckResult check(Driver driver, WebElement element);

  public WebElementCondition negate() {
    return new Not(this, missingElementSatisfiesCondition);
  }

  /**
   * Should be used for explaining the reason of condition
   */
  public WebElementCondition because(String message) {
    return new ExplainedCondition<>(this, message);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  public boolean missingElementSatisfiesCondition() {
    return missingElementSatisfiesCondition;
  }

  /**
   * Using "or" checks in tests is probably a flag of bad test design.
   * Consider splitting this "or" check into two different methods or tests.
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  public WebElementCondition or(WebElementCondition alternative) {
    return new WebElementCondition("%s OR %s".formatted(WebElementCondition.this.toString(), alternative.toString())) {
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        CheckResult r1 = WebElementCondition.this.check(driver, element);
        return r1.verdict() == ACCEPT ? r1 : alternative.check(driver, element);
      }

      @Override
      public WebElementCondition negate() {
        return new Not(this,
          Stream.of(WebElementCondition.this, alternative)
            .map(WebElementCondition::negate)
            .anyMatch(WebElementCondition::missingElementSatisfiesCondition)
        );
      }
    };
  }
}
