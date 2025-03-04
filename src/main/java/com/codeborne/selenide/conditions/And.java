package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static java.util.stream.Collectors.joining;

public class And extends WebElementCondition {
  private final List<? extends WebElementCondition> conditions;

  /**
   * Ctor.
   *
   * @param name       condition name
   * @param conditions conditions list
   * @throws IllegalArgumentException if {@code conditions} is empty
   */
  public And(String name, List<? extends WebElementCondition> conditions) {
    super(name, checkedConditionsListCtorArg(conditions).stream().allMatch(WebElementCondition::missingElementSatisfiesCondition));
    this.conditions = conditions;
  }

  private static List<? extends WebElementCondition> checkedConditionsListCtorArg(List<? extends WebElementCondition> conditions) {
    if (conditions.isEmpty()) {
      throw new IllegalArgumentException("conditions list is empty");
    }
    return conditions;
  }

  @Override
  public WebElementCondition negate() {
    return new Not(this,
      conditions.stream().map(WebElementCondition::negate).allMatch(WebElementCondition::missingElementSatisfiesCondition)
    );
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    List<CheckResult> results = new ArrayList<>();

    for (WebElementCondition c : conditions) {
      CheckResult checkResult = c.check(driver, element);
      if (checkResult.verdict() != ACCEPT) {
        return checkResult;
      }
      else {
        results.add(checkResult);
      }
    }

    String actualValues = results.stream().map(check -> String.valueOf(check.actualValue())).collect(joining(", "));
    return new CheckResult(ACCEPT, actualValues);
  }

  @Override
  public String toString() {
    return getName() + ": " + conditions.stream().map(WebElementCondition::toString).collect(joining(" and "));
  }
}
