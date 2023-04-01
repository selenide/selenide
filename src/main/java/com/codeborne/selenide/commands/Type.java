package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Plugins.inject;

public class Type implements Command<SelenideElement> {

  private final Clear clear;

  public Type() {
    this(inject(Clear.class));
  }

  protected Type(Clear clear) {
    this.clear = clear;
  }

  @Nullable
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
    TypeOptions typeOptions = extractOptions(Objects.requireNonNull(args));
    clearField(proxy, locator, typeOptions);

    WebElement element = locator.findAndAssertElementIsEditable();
    typeIntoField(element, typeOptions);
    return proxy;
  }

  private TypeOptions extractOptions(Object[] args) {
    if (args[0] instanceof TypeOptions options) {
      return options;
    } else {
      CharSequence text = firstOf(args);
      return TypeOptions.with(text);
    }
  }

  private void typeIntoField(WebElement element, TypeOptions typeOptions) {
    for (char character : typeOptions.textToType().toCharArray()) {
      element.sendKeys(String.valueOf(character));
      Stopwatch.sleepAtLeast(typeOptions.timeDelay().toMillis());
    }
  }

  private void clearField(SelenideElement proxy, WebElementSource locator, TypeOptions typeOptions) {
    if (typeOptions.shouldClearFieldBeforeTyping()) {
      if (typeOptions.textToType().length() > 0) {
        clear.clear(locator.driver(), proxy);
      } else {
        clear.clearAndTrigger(locator.driver(), proxy);
      }
    }
  }
}
