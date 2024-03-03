package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class Exist extends WebElementCondition {
  public Exist() {
    super("exist");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    try {
      WebElement originalWebElement = (element instanceof SelenideElement se) ? se.toWebElement() : element;
      originalWebElement.isDisplayed();
      return new CheckResult(ACCEPT, "exists");
    }
    catch (StaleElementReferenceException | NoSuchElementException e) {
      return new CheckResult(REJECT, "does not exist");
    }
  }

  @Nonnull
  @Override
  public WebElementCondition negate() {
    return new Not(this, true);
  }
}
