package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.END;
import static org.openqa.selenium.Keys.HOME;
import static org.openqa.selenium.Keys.SHIFT;
import static org.openqa.selenium.Keys.TAB;
import static org.openqa.selenium.Keys.chord;

/**
 * Clean the input field value.
 * <br>
 * <p>
 *  The standard Selenium method {@link WebElement#clear()} does not help in case of
 *  "tricky" inputs generated by Vue.js, React and other fancy frameworks.
 * </p>
 * <br>
 * <p>
 *  That's why we need to clear the field value by emulating real user actions:
 * </p>
 * <ol>
 *   <li>Select the whole text</li>
 *   <li>press "Backspace"</li>
 * </ol>
 */
@ParametersAreNonnullByDefault
public class Clear implements Command<SelenideElement> {
  @Nonnull
  @CheckReturnValue
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement input = locator.findAndAssertElementIsEditable();
    execute(locator.driver(), input);
    return proxy;
  }

  /*
   * This is the shortest keys combination I found in May 2022.
   *
   * It seems to work in Firefox, Chrome on Mac and on Linux smoothly.
   */
  public void execute(Driver driver, WebElement input) {
    input.sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, TAB);
  }
}
