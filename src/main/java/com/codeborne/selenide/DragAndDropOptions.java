package com.codeborne.selenide;

import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.ACTIONS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;

public class DragAndDropOptions {
  private final DragAndDropTarget target;
  private final DragAndDropMethod method;

  public DragAndDropOptions(DragAndDropTarget target, DragAndDropMethod method) {
    this.target = target;
    this.method = method;
  }

  public static DragAndDropOptions to(String cssSelector) {
    return new DragAndDropOptions(new DragAndDropTarget.CssSelector(cssSelector), JS);
  }

  public static DragAndDropOptions to(WebElement element) {
    return new DragAndDropOptions(new DragAndDropTarget.Element(element), JS);
  }

  public DragAndDropOptions usingJS() {
    return new DragAndDropOptions(target, JS);
  }

  public DragAndDropOptions usingSeleniumActions() {
    return new DragAndDropOptions(target, ACTIONS);
  }

  public DragAndDropMethod getMethod() {
    return method;
  }

  public SelenideElement getTarget(Driver driver) {
    return target.toSelenideElement(driver);
  }

  public enum DragAndDropMethod {

    /**
     * Executing drag and drop via Selenium Actions
     */
    ACTIONS,

    /**
     * Executing drag and drop via JS script
     */
    JS
  }

  @FunctionalInterface
  public interface DragAndDropTarget {

    SelenideElement toSelenideElement(Driver driver);

    class CssSelector implements DragAndDropTarget {
      private final By cssSelector;

      public CssSelector(String cssSelector) {
        this.cssSelector = By.cssSelector(cssSelector);
      }

      @Override
      public SelenideElement toSelenideElement(Driver driver) {
        return ElementFinder.wrap(driver, cssSelector);
      }

      @Override
      public String toString() {
        return cssSelector.toString();
      }
    }

    class Element implements DragAndDropTarget {
      private final WebElement element;

      public Element(WebElement element) {
        this.element = element;
      }

      @Override
      public SelenideElement toSelenideElement(Driver driver) {
        return WebElementWrapper.wrap(driver, element);
      }

      @Override
      public String toString() {
        return element.toString();
      }
    }
  }

  @Override
  public String toString() {
    return String.format("target: %s, method: %s", target, method);
  }
}

