package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * Appium-specific element describer.
 * <p>
 * Sample output:
 * <p>
 * Element should have text '666' {By.id: result}
 * Element: '<TextView class="android.widget.TextView" id="com.android.calculator2:id/result" name="6" displayed="true" checked="false" enabled="true" focused="false" bounds="[0,183][1080,584]" contentDescription="null" package="com.android.calculator2">6</TextView>'
 * Screenshot: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.png
 * Page source: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.html
 */
@ParametersAreNonnullByDefault
public class AppiumElementDescriber implements ElementDescriber {
  private static final Logger logger = LoggerFactory.getLogger(AppiumElementDescriber.class);
  private static final Pattern RE_IOS_UNSUPPORTED_ATTRIBUTE = compile(".*The attribute '\\w+' is unknown.*", DOTALL);

  @Nonnull
  @Override
  public String fully(Driver driver, @Nullable WebElement element) {
    if (element == null) {
      return "null";
    }

    return new Builder(element)
      .appendTagName()
      .appendAttributes(supportedAttributes(driver))
      .finish()
      .build();
  }

  protected List<String> supportedAttributes(Driver driver) {
    if (driver.getWebDriver() instanceof AndroidDriver) {
      return androidAttributes();
    }
    else if (driver.getWebDriver() instanceof IOSDriver) {
      return iosAttributes();
    }
    else {
      return genericAttributes();
    }
  }

  protected List<String> androidAttributes() {
    return asList(
      "resource-id", "checked", "content-desc",
      "enabled", "focused", "package",
      "name", "className", "bounds", "displayed"
    );
  }

  protected List<String> iosAttributes() {
    return asList("enabled", "selected", "name", "type", "value", "visible");
  }

  protected List<String> genericAttributes() {
    return asList("checked", "content-desc", "enabled", "name", "displayed");
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

    private Builder appendAttributes(List<String> names) {
      names.forEach(this::appendAttribute);
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
      catch (WebDriverException e) {
        if (isUnsupportedAttributeError(e)) {
          logger.debug("{}: {}", errorMessage.get(), e.toString());
        }
        else {
          logger.info("{}: {}", errorMessage.get(), e.toString());
        }
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

  static boolean isUnsupportedAttributeError(WebDriverException e) {
    return e instanceof UnsupportedCommandException
      || RE_IOS_UNSUPPORTED_ATTRIBUTE.matcher(e.getMessage()).matches();
  }
}
