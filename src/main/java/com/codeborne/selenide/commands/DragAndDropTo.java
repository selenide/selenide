package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;

@ParametersAreNonnullByDefault
public class DragAndDropTo implements Command<SelenideElement> {
  private static final JavaScript js = new JavaScript("drag_and_drop_script.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    DragAndDropOptions options = dragAndDropOptions(args);

    DragAndDropMethod method = options.getMethod();
    SelenideElement target = options.getTarget(locator.driver());
    target.shouldBe(visible);

    dragAndDrop(locator, target, method);

    return proxy;
  }

  @CheckReturnValue
  @Nonnull
  private DragAndDropOptions dragAndDropOptions(@Nullable Object[] args) {
    if (args != null && args.length > 0) {
      if (args[0] instanceof DragAndDropOptions options)
        return options;

      //handle deprecated methods calls
      DragAndDropMethod method = JS;
      if (args.length > 1 && args[1] instanceof DragAndDropOptions dragAndDropOptions)
        method = dragAndDropOptions.getMethod();

      DragAndDropOptions.DragAndDropTarget target = null;
      if (args[0] instanceof String cssSelector)
        target = new DragAndDropOptions.DragAndDropTarget.CssSelector(cssSelector);
      else if (args[0] instanceof WebElement webElement)
        target = new DragAndDropOptions.DragAndDropTarget.Element(webElement);

      if (target != null) return new DragAndDropOptions(target, method);
    }

    throw new IllegalArgumentException("Missing Drag and Drop options argument");
  }

  private void dragAndDrop(WebElementSource locator, SelenideElement target, DragAndDropMethod method) {
    switch (method) {
      case JS -> dragAndDropUsingJavaScript(locator.driver(), locator.getWebElement(), target.getWrappedElement());
      case ACTIONS -> dragAndDropUsingActions(locator.driver(), locator.getWebElement(), target.getWrappedElement());
      default -> throw new IllegalArgumentException("Drag and Drop method not defined!");
    }
  }

  private void dragAndDropUsingActions(Driver driver, WebElement from, WebElement target) {
    driver.actions().dragAndDrop(from, target).perform();
  }

  private void dragAndDropUsingJavaScript(Driver driver, WebElement from, WebElement to) {
    js.execute(driver, from, to);
  }
}
