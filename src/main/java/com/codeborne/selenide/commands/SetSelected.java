package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.ex.InvalidStateError;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.Util.firstOf;

public class SetSelected extends FluentCommand {
  private final Click click;

  public SetSelected() {
    this.click = new Click();
  }

  public SetSelected(Click click) {
    this.click = click;
  }

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    boolean selected = firstOf(args);
    WebElement element = locator.getWebElement();
    if (!element.isDisplayed()) {
      throw new InvalidStateError(locator.description(), "Cannot change invisible element");
    }
    String tag = element.getTagName();
    if (!"option".equals(tag)) {
      if ("input".equals(tag)) {
        String type = element.getAttribute("type");
        if (!"checkbox".equals(type) && !"radio".equals(type)) {
          throw new InvalidStateError(locator.description(), "Only use setSelected on checkbox/option/radio");
        }
      }
      else {
        throw new InvalidStateError(locator.description(), "Only use setSelected on checkbox/option/radio");
      }
    }
    if (element.getAttribute("readonly") != null || element.getAttribute("disabled") != null) {
      throw new InvalidStateError(locator.description(), "Cannot change value of readonly/disabled element");
    }
    if (element.isSelected() != selected) {
      click.execute(locator, NO_ARGS);
    }
  }
}
