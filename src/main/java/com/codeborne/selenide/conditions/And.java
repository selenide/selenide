package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.conditions.ConditionHelpers.negateMissingElementTolerance;
import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class And extends Condition {

  private final List<Condition> conditions;
  private Condition lastFailedCondition;

  public And(String name, List<Condition> conditions) {
    super(name);
    this.conditions = conditions;
  }

  @Nonnull
  @Override
  public Condition negate() {
    return new Not(this, negateMissingElementTolerance(conditions));
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
    String conditionsToString = conditions.stream().map(Condition::toString).collect(joining(" and "));
    return String.format("%s: %s", getName(), conditionsToString);
  }
}
