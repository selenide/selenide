package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.FileContent;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class DragAndDropTo implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    SelenideElement target;
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing target argument");
    }
    else if (args[0] instanceof String) {
      target = ElementFinder.wrap(locator.driver(), By.cssSelector((String) args[0]));
    }
    else if (args[0] instanceof WebElement) {
      target = WebElementWrapper.wrap(locator.driver(), (WebElement) args[0]);
    }
    else {
      throw new IllegalArgumentException("Unknown target type: " + args[0] +
        " (only String or WebElement are supported)");
    }
    target.shouldBe(visible);
    if (args.length == 2 && args[1] instanceof DragAndDropOptions) {
      dragAndDrop(locator, target, (DragAndDropOptions) args[1]);
    } else {
      dragAndDropViaActions(locator, target);
    }
    return proxy;
  }

  @Nonnull
  @CheckReturnValue
  private void dragAndDrop(WebElementSource locator, SelenideElement target, DragAndDropOptions options) {
    switch (options.getMethod()) {
      case JS:
        dragAndDropViaJs(locator.driver(), locator.getWebElement(), target);
        break;
      case DEFAULT:
        dragAndDropViaActions(locator, target);
        break;
      default:
        throw new IllegalArgumentException("Drag and Drop method not defined!");
    }
  }

  private void dragAndDropViaActions(WebElementSource locator, SelenideElement target) {
    new Actions(locator.driver().getWebDriver()).dragAndDrop(locator.getWebElement(), target).perform();
  }

  private void dragAndDropViaJs(Driver driver, WebElement from, WebElement to) {
    StringBuilder js = new StringBuilder(new FileContent("drag_and_drop_script.js").content());
    js.append("dragAndDrop(arguments[0], arguments[1])");
    driver.executeJavaScript(js.toString(), from, to);
  }

}
