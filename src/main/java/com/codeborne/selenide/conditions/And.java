package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class And extends Condition {
  private final List<? extends Condition> conditions;

  /**
   * Ctor.
   *
   * @param name       condition name
   * @param conditions conditions list
   * @throws IllegalArgumentException if {@code conditions} is empty
   */
  public And(String name, List<? extends Condition> conditions) {
    super(name, checkedConditionsListCtorArg(conditions).stream().allMatch(Condition::missingElementSatisfiesCondition));
    this.conditions = conditions;
  }

  private static List<? extends Condition> checkedConditionsListCtorArg(List<? extends Condition> conditions) {
    if (conditions.isEmpty()) {
      throw new IllegalArgumentException("conditions list is empty");
    }
    return conditions;
  }

  @Nonnull
  @Override
  public Condition negate() {
    return new Not(this, conditions.stream().map(Condition::negate).allMatch(Condition::missingElementSatisfiesCondition));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    List<CheckResult> results = new ArrayList<>();

    for (Condition c : conditions) {
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

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return getName() + ": " + conditions.stream().map(Condition::toString).collect(joining(" and "));
  }
}
