package com.codeborne.selenide;

import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.ACTIONS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropTarget.Deprecated.DEPRECATED;

@ParametersAreNonnullByDefault
public class DragAndDropOptions {
  private final DragAndDropTarget target;
  private final DragAndDropMethod method;

  public DragAndDropOptions(DragAndDropTarget target, DragAndDropMethod method) {
    this.target = target;
    this.method = method;
  }

  @CheckReturnValue
  @Nonnull
  public static DragAndDropOptions to(String cssSelector) {
    return new DragAndDropOptions(new DragAndDropTarget.CssSelector(cssSelector), JS);
  }

  @CheckReturnValue
  @Nonnull
  public static DragAndDropOptions to(WebElement element) {
    return new DragAndDropOptions(new DragAndDropTarget.Element(element), JS);
  }

  @CheckReturnValue
  @Nonnull
  public DragAndDropOptions usingJS() {
    return new DragAndDropOptions(target, JS);
  }

  @CheckReturnValue
  @Nonnull
  public DragAndDropOptions usingSeleniumActions() {
    return new DragAndDropOptions(target, ACTIONS);
  }

  /**
   * @deprecated use {@link DragAndDropOptions#to(WebElement)}.{@link DragAndDropOptions#usingJS()} instead
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public static DragAndDropOptions usingJavaScript() {
    return new DragAndDropOptions(DEPRECATED, JS);
  }

  /**
   * @deprecated use {@link DragAndDropOptions#to(WebElement)}.{@link DragAndDropOptions#usingSeleniumActions()} instead
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public static DragAndDropOptions usingActions() {
    return new DragAndDropOptions(DEPRECATED, ACTIONS);
  }

  @CheckReturnValue
  @Nonnull
  public DragAndDropMethod getMethod() {
    return method;
  }

  @CheckReturnValue
  @Nonnull
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

    // Remove after all deprecated methods are gone
    class Deprecated implements DragAndDropTarget {

      public static final Deprecated DEPRECATED = new Deprecated();

      @Override
      public SelenideElement toSelenideElement(Driver driver) {
        throw new IllegalArgumentException("Incorrect usage of deprecated API of DragAndDropOptions");
      }

      @Override
      public String toString() {
        return "deprecated";
      }
    }
  }

  @Override
  public String toString() {
    return String.format("target: %s, method: %s", target, method);
  }
}

