package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.io.IOException;

import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNull;

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

    CharSequence charSequence = firstOf(requireNonNull(args));
    WebElement element = locator.findAndAssertElementIsEditable();
    String text = String.valueOf(charSequence);
    clearField(proxy, locator, charSequence);

    for (char character : text.toCharArray()) {
      element.sendKeys(String.valueOf(character));
      Stopwatch.sleepAtLeast(200);
    }
    return proxy;
  }

  private void clearField(SelenideElement proxy, WebElementSource locator, CharSequence charSequence) {
    if (charSequence.length() > 0) {
      clear.clear(locator.driver(), proxy);
    } else {
      clear.clearAndTrigger(locator.driver(), proxy);
    }
  }
}
