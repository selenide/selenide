package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.FileContent;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DragAndDropOptions.usingJavaScript;

@ParametersAreNonnullByDefault
public class DragAndDropTo implements Command<SelenideElement> {
  private static final FileContent js = new FileContent("drag_and_drop_script.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    SelenideElement target = findTarget(locator.driver(), args);
    target.shouldBe(visible);

    DragAndDropOptions options = new Arguments(args)
      .ofType(DragAndDropOptions.class)
      .orElse(usingJavaScript());

    dragAndDrop(locator, target, options);
    return proxy;
  }

  @Nonnull
  protected SelenideElement findTarget(Driver driver, @Nullable Object[] args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing target argument");
    }
    else if (args[0] instanceof String) {
      return ElementFinder.wrap(driver, By.cssSelector((String) args[0]));
    }
    else if (args[0] instanceof WebElement) {
      return WebElementWrapper.wrap(driver, (WebElement) args[0]);
    }
    else {
      throw new IllegalArgumentException("Unknown target type: " + args[0] +
        " (only String or WebElement are supported)");
    }
  }

  private void dragAndDrop(WebElementSource locator, SelenideElement target, DragAndDropOptions options) {
    switch (options.getMethod()) {
      case JS:
        dragAndDropUsingJavaScript(locator.driver(), locator.getWebElement(), target.getWrappedElement());
        break;
      case ACTIONS:
        dragAndDropUsingActions(locator.driver(), locator.getWebElement(), target.getWrappedElement());
        break;
      default:
        throw new IllegalArgumentException("Drag and Drop method not defined!");
    }
  }

  private void dragAndDropUsingActions(Driver driver, WebElement from, WebElement target) {
    new Actions(driver.getWebDriver()).dragAndDrop(from, target).perform();
  }

  private void dragAndDropUsingJavaScript(Driver driver, WebElement from, WebElement to) {
    driver.executeJavaScript(js.content() + "; dragAndDrop(arguments[0], arguments[1])", from, to);
  }
}
