package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;

public class Hidden extends WebElementCondition {
  public Hidden() {
    super("hidden", true);
  }

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

  @Override
  public WebElementCondition negate() {
    return new Not(this, false);
  }
}
