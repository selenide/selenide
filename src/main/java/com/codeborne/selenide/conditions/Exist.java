package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class Exist extends Condition {
  public Exist() {
    super("exist");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    try {
      element.isDisplayed();
      return new CheckResult(ACCEPT, "exists");
    }
    catch (StaleElementReferenceException e) {
      return new CheckResult(REJECT, "does not exist");
    }
  }

  @Nonnull
  @Override
  public Condition negate() {
    return new Not(this, true);
  }
}
