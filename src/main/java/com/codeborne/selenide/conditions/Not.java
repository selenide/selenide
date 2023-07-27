package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class Not extends Condition {
  private final Condition condition;

  public Not(Condition originalCondition, boolean absentElementMatchesCondition) {
    super("not " + originalCondition.getName(), absentElementMatchesCondition);
    this.condition = originalCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult check = condition.check(driver, element);
    return new CheckResult(check.verdict() == ACCEPT ? REJECT : ACCEPT, check.message(), check.actualValue(), check.timestamp());
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return "not " + condition.toString();
  }
}
