package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Appium-specific element describer.
 * <p>
 * Sample output:
 * <p>
 * Element should have text '666' {By.id: result}
 * Element: '<TextView class="android.widget.TextView" id="com.android.calculator2:id/result" name="6" displayed="true" checked="false" enabled="true" focused="false" bounds="[0,183][1080,584]" contentDescription="null" contentSize="{"width":1080,"height":401,"top":183,"left":0,"scrollableOffset":0,"touchPadding":24}" package="com.android.calculator2">6</TextView>'
 * Screenshot: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.png
 * Page source: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.html
 */
@ParametersAreNonnullByDefault
public class AppiumElementDescriber implements ElementDescriber {
  private static final Logger logger = LoggerFactory.getLogger(AppiumElementDescriber.class);

  @Nonnull
  @Override
  public String fully(Driver driver, @Nullable WebElement element) {
    if (element == null) {
      return "null";
    }

    return new Builder(element)
      .appendTagName()
      .appendAttribute("resource-id")
      .appendAttribute("checked")
      .appendAttribute("content-desc")
      .appendAttribute("enabled")
      .appendAttribute("focused")
      .appendAttribute("package")
      .appendAttribute("name")
      .appendAttribute("bounds")
      .appendAttribute("displayed")
      .appendAttribute("contentSize")
      .finish()
      .build();
  }

  @Nonnull
  @Override
  public String briefly(Driver driver, @Nonnull WebElement element) {
    return new Builder(element)
      .appendTagName()
      .appendAttribute("resource-id")
      .finish()
      .build();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String selector(By selector) {
    if (selector instanceof By.ByCssSelector) {
      return selector.toString()
        .replace("By.selector: ", "")
        .replace("By.cssSelector: ", "");
    }
    return selector.toString();
  }

  @ParametersAreNonnullByDefault
  private static class Builder {
    private final WebElement element;
    private String className = "?";
    private String tagName = "?";
    private String text = "?";
    private final StringBuilder sb = new StringBuilder();
    private StaleElementReferenceException staleElementException;

    private Builder(WebElement element) {
      this.element = element;
    }

    private Builder appendTagName() {
      getAttribute("class", (className) -> {
        this.className = className;
        tagName = className.replaceFirst(".+\\.(.+)", "$1");
      });
      if ("?".equals(tagName)) {
        safeCall(element::getTagName, () -> "Failed to get tag name", (tagName) -> {
          this.tagName = tagName;
        });
      }
      sb.append("<").append(tagName).append(" class=\"").append(className).append("\"");
      return this;
    }

    private Builder appendAttribute(String name) {
      getAttribute(name, (value) -> {
        sb.append(" ").append(name).append("=\"").append(value).append("\"");
      });
      return this;
    }

    private void getAttribute(String name, Consumer<String> attributeHandler) {
      safeCall(() -> element.getAttribute(name),
        () -> "Failed to get attribute " + name,
        attributeHandler);
    }

    private void safeCall(Supplier<String> method, Supplier<String> errorMessage, Consumer<String> resultHandler) {
      if (staleElementException != null) return;

      try {
        String value = method.get();
        if (value != null) {
          resultHandler.accept(value);
        }
      }
      catch (StaleElementReferenceException e) {
        staleElementException = e;
        logger.debug("{}: {}", errorMessage.get(), e.toString());
      }
      catch (UnsupportedCommandException e) {
        logger.debug("{}: {}", errorMessage.get(), e.toString());
      }
      catch (WebDriverException e) {
        logger.info("{}: {}", errorMessage.get(), e.toString());
      }
      catch (RuntimeException e) {
        logger.warn("{}", errorMessage.get(), e);
      }
    }

    public Builder finish() {
      sb.append(">");

      if (staleElementException != null) {
        sb.append(staleElementException);
      }
      else {
        appendText();
      }

      sb.append("</").append(tagName).append(">");
      return this;
    }

    private void appendText() {
      safeCall(element::getText, () -> "Failed to get text", (text) -> this.text = text);
      if ("?".equals(text)) {
        getAttribute("text", (text) -> this.text = text);
      }
      if ("?".equals(text)) {
        getAttribute("label", (text) -> this.text = text);
      }
      if ("?".equals(text)) {
        getAttribute("value", (text) -> this.text = text);
      }
      sb.append(text);
    }

    private String build() {
      return sb.toString();
    }
  }
}
