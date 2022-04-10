package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SetValueOptions;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueMethod.SEND_KEYS;
import static com.codeborne.selenide.SetValueOptions.withText;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Events.events;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public class SetValue implements Command<SelenideElement> {
  private final JavaScript js = new JavaScript("set-value.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement element = locator.findAndAssertElementIsInteractable();
    Driver driver = locator.driver();
    SetValueOptions options = extractOptions(driver.config(), args);
    setValueForTextInput(driver, locator.description(), element, options);
    return proxy;
  }

  private SetValueOptions extractOptions(Config config, Object[] args) {
    if (args[0] instanceof SetValueOptions) {
      return firstOf(args);
    }
    else {
      CharSequence text = firstOf(args);
      return withText(text).usingMethod(config.fastSetValue() ? JS : SEND_KEYS);
    }
  }

  private void setValueForTextInput(Driver driver, String elementDescription, WebElement element, SetValueOptions options) {
    if (options.value() == null || options.value().length() == 0) {
      element.clear();
    }
    else if (options.method() == JS) {
      String error = setValueByJs(driver, element, options.value());
      if (isNotEmpty(error)) {
        throw new InvalidStateException(elementDescription, error);
      }
      else {
        events.fireEvent(driver, element, "keydown", "keypress", "input", "keyup", "change");
      }
    }
    else {
      element.clear();
      element.sendKeys(options.value());
    }
  }

  private String setValueByJs(Driver driver, WebElement element, CharSequence text) {
    return js.execute(driver, element, text);
  }
}
