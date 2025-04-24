package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Stopwatch.sleepAtLeast;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNull;

public class Type extends FluentCommand {
  private final Clear clear;

  public Type() {
    this(inject(Clear.class));
  }

  protected Type(Clear clear) {
    this.clear = clear;
  }

  @Override
  protected void execute(WebElementSource locator, Object[] args) {
    TypeOptions typeOptions = extractOptions(requireNonNull(args));
    clearField(locator, typeOptions);

    WebElement element = findElement(locator);
    typeIntoField(element, typeOptions);
  }

  protected WebElement findElement(WebElementSource locator) {
    return locator.findAndAssertElementIsEditable();
  }

  protected TypeOptions extractOptions(Object[] args) {
    if (args[0] instanceof TypeOptions options) {
      return options;
    } else {
      return TypeOptions.text(firstOf(args));
    }
  }

  protected void clearField(WebElementSource locator, TypeOptions typeOptions) {
    if (typeOptions.shouldClearFieldBeforeTyping()) {
      if (!typeOptions.textToType().isEmpty()) {
        clear.clear(locator.driver(), locator.getWebElement());
      } else {
        clear.clearAndTrigger(locator.driver(), locator.getWebElement());
      }
    }
  }

  private void typeIntoField(WebElement element, TypeOptions typeOptions) {
    int textLength = typeOptions.textToType().length();
    for (int i = 0; i < textLength; i++) {
      CharSequence character = typeOptions.textToType().subSequence(i, i + 1);
      element.sendKeys(character);
      sleepAtLeast(typeOptions.timeDelay().toMillis());
    }
  }
}
