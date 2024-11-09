package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

public class Not extends WebElementCondition {
  private final WebElementCondition condition;

  public Not(WebElementCondition originalCondition, boolean absentElementMatchesCondition) {
    super("not " + originalCondition.getName(), absentElementMatchesCondition);
    this.condition = originalCondition;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult check = condition.check(driver, element);
    return new CheckResult(check.verdict() == ACCEPT ? REJECT : ACCEPT, check.message(), check.actualValue(), check.timestamp());
  }

  @Override
  public String toString() {
    return "not " + condition;
  }
}
