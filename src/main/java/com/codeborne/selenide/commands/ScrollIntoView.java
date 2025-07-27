package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.ScrollIntoViewOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ScrollIntoViewOptions.Block.end;
import static com.codeborne.selenide.ScrollIntoViewOptions.Block.start;
import static com.codeborne.selenide.ScrollIntoViewOptions.Inline.nearest;
import static com.codeborne.selenide.ScrollIntoViewOptions.instant;
import static com.codeborne.selenide.commands.Util.firstOf;

public class ScrollIntoView extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    Object parameter = firstOf(args);
    Object scrollOptions =
      parameter instanceof ScrollIntoViewOptions options ? options.toJson() :
        parameter.equals(Boolean.TRUE) ? instant().block(start).inline(nearest).toJson() :
          parameter.equals(Boolean.FALSE) ? instant().block(end).inline(nearest).toJson() :
            parameter;

    WebElement webElement = locator.getWebElement();
    locator.driver().executeJavaScript("arguments[0].scrollIntoView(" + scrollOptions + ")", webElement);
  }
}
