package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
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

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * Appium-specific element describer.
 * <p>
 * Sample output:
 * <p>
 * <pre>{@code
 * Element should have text '666' {By.id: result}
 * Element:
 *  <TextView class="android.widget.TextView" id="com.android.calculator2:id/result" name="6" displayed="true"
 *            checked="false" enabled="true" focused="false" bounds="[0,183][1080,584]"
 *            contentDescription="null" package="com.android.calculator2">6</TextView>
 * Screenshot: file:/Users/andrei/projects/selenide/build/reports/tests/1599256941895.0.png
 * Page source: file:/Users/andrei/projects/selenide/build/reports/tests/1599256941895.0.html
 * }
 * </pre>
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

    return new Builder(element, driver.getWebDriver(), supportedAttributes(driver))
      .appendTagName()
      .appendAttributes()
      .finish()
      .build();
  }

  protected List<String> supportedAttributes(Driver driver) {
    if (instanceOf(driver, AndroidDriver.class)) {
      return androidAttributes();
    }
    else if (instanceOf(driver, IOSDriver.class)) {
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
    return asList("enabled", "selected", "name", "value", "visible");
  }

  protected List<String> genericAttributes() {
    return asList("checked", "content-desc", "enabled", "name", "displayed");
  }

  @Nonnull
  @Override
  public String briefly(Driver driver, @Nonnull WebElement element) {
    return new Builder(element, driver.getWebDriver(), supportedAttributes(driver))
      .appendTagName()
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
    private final WebDriver webDriver;
    private final List<String> supportedAttributes;
    private String tagName = "?";
    private String text = "?";
    private final StringBuilder sb = new StringBuilder();
    private WebDriverException unforgivableException;

    private Builder(WebElement element, WebDriver webDriver, List<String> supportedAttributes) {
      this.element = element;
      this.webDriver = webDriver;
      this.supportedAttributes = supportedAttributes;
    }

    private Builder appendTagName() {
      if (instanceOf(webDriver, AndroidDriver.class)) {
        getAttribute("class", className -> {
          tagName = removePackage(className);
        });
      }
      if ("?".equals(tagName)) {
        safeCall(element::getTagName, () -> "Failed to get tag name", tagName -> this.tagName = tagName);
      }
      sb.append("<").append(tagName);
      return this;
    }

    private Builder appendAttributes() {
      supportedAttributes.forEach(this::appendAttribute);
      return this;
    }

    private Builder appendAttribute(String name) {
      getAttribute(name, value -> {
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
      if (unforgivableException != null) return;

      try {
        String value = method.get();
        if (value != null) {
          resultHandler.accept(value);
        }
      }
      catch (StaleElementReferenceException | NoSuchElementException e) {
        unforgivableException = e;
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

      if (unforgivableException != null) {
        sb.append(unforgivableException);
      }
      else {
        appendText();
      }

      sb.append("</").append(tagName).append(">");
      return this;
    }

    private void appendText() {
      safeCall(element::getText, () -> "Failed to get text", text -> this.text = text);
      if ("?".equals(text)) {
        getAttribute("text", text -> this.text = text);
      }
      if ("?".equals(text)) {
        getAttribute("label", text -> this.text = text);
      }
      if ("?".equals(text)) {
        getAttribute("value", text -> this.text = text);
      }
      sb.append(text);
    }

    private String build() {
      return sb.toString();
    }
  }

  @Nonnull
  @CheckReturnValue
  static String removePackage(String className) {
    int i = className.lastIndexOf('.');
    return i < 0 ? className : className.substring(i + 1);
  }

  static boolean isUnsupportedAttributeError(WebDriverException e) {
    return e instanceof UnsupportedCommandException
      || RE_IOS_UNSUPPORTED_ATTRIBUTE.matcher(e.getMessage()).matches();
  }
}
