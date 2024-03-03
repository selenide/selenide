package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;

@ParametersAreNonnullByDefault
public class Hidden extends WebElementCondition {
  public Hidden() {
    super("hidden", true);
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    try {
      boolean hidden = !element.isDisplayed();
      return new CheckResult(hidden, hidden ? "hidden" : "visible");
    }
    catch (StaleElementReferenceException | NoSuchElementException elementHasDisappeared) {
      return new CheckResult(ACCEPT, "hidden:true");
    }
  }

  @Nonnull
  @Override
  public WebElementCondition negate() {
    return new Not(this, false);
  }
}
