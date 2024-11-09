package com.codeborne.selenide.commands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.SetValueOptions;
import com.codeborne.selenide.ex.InvalidStateError;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueMethod.SEND_KEYS;
import static com.codeborne.selenide.SetValueOptions.withText;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SetValue extends FluentCommand {
  private final JavaScript js = new JavaScript("set-value.js");
  private final Clear clear;

  public SetValue() {
    this(inject(Clear.class));
  }

  protected SetValue(Clear clear) {
    this.clear = clear;
  }

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    Driver driver = locator.driver();
    SetValueOptions options = extractOptions(driver.config(), requireNonNull(args));
    setValueForTextInput(driver, locator, options);
  }

  private SetValueOptions extractOptions(Config config, Object[] args) {
    if (args[0] instanceof SetValueOptions options) {
      return options;
    }
    else {
      CharSequence text = firstOf(args);
      return withText(text).usingMethod(config.fastSetValue() ? JS : SEND_KEYS);
    }
  }

  private void setValueForTextInput(Driver driver, WebElementSource locator, SetValueOptions options) {
    WebElement element = locator.findAndAssertElementIsEditable();
    CharSequence value = firstNonNull(options.value(), "");

    if (options.method() == JS) {
      String error = setValueByJs(driver, element, value);
      if (isNotEmpty(error)) {
        String elementDescription = locator.description();
        throw new InvalidStateError(elementDescription, error);
      }
    }
    else {
      if (!value.isEmpty()) {
        clear.clear(driver, element);
        element.sendKeys(value);
      }
      else {
        clear.clearAndTrigger(driver, element);
      }
    }
  }

  private String setValueByJs(Driver driver, WebElement element, CharSequence text) {
    return requireNonNull(js.execute(driver, element, text));
  }
}
