package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class And extends Condition {

  private final List<? extends Condition> conditions;
  private Condition lastFailedCondition;

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

  @CheckReturnValue
  @Override
  public boolean apply(Driver driver, WebElement element) {
    lastFailedCondition = null;
    for (Condition c : conditions) {
      if (!c.apply(driver, element)) {
        lastFailedCondition = c;
        return false;
      }
    }
    return true;
  }

  @CheckReturnValue
  @Override
  public String actualValue(Driver driver, WebElement element) {
    return lastFailedCondition == null ? null : lastFailedCondition.actualValue(driver, element);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return getName() + ": " + conditions.stream().map(Condition::toString).collect(joining(" and "));
  }
}
