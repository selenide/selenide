package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class Or extends Condition {

  private final List<? extends Condition> conditions;

  /**
   * Ctor.
   *
   * @param name       condition name
   * @param conditions conditions list
   * @throws IllegalArgumentException if {@code conditions} is empty
   */
  public Or(String name, List<? extends Condition> conditions) {
    super(name, checkedConditionsListCtorArg(conditions).stream().anyMatch(Condition::missingElementSatisfiesCondition));
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
    return new Not(this, conditions.stream().map(Condition::negate).anyMatch(Condition::missingElementSatisfiesCondition));
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    for (Condition c : conditions) {
      if (c.apply(driver, element)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return conditions.stream().map(condition -> condition.actualValue(driver, element)).collect(joining(", "));
  }

  @Override
  public String toString() {
    return getName() + ": " + conditions.stream().map(Condition::toString).collect(joining(" or "));
  }
}
