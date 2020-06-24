package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class And extends Condition {

  private final List<Condition> conditions;
  private Condition lastFailedCondition;

  public And(String name, List<Condition> conditions) {
    super(name);
    this.conditions = conditions;
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
    return lastFailedCondition == null ? super.toString() : lastFailedCondition.toString();
  }
}
