package com.codeborne.selenide;

import com.codeborne.selenide.ex.AlertNotFoundException;
import com.codeborne.selenide.ex.FrameNotFoundException;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.ex.WindowNotFoundException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.frameToBeAvailableAndSwitchToIt;

@ParametersAreNonnullByDefault
public class SelenideTargetLocator implements TargetLocator {
  private final Driver driver;
  private final WebDriver webDriver;
  private final Config config;
  private final TargetLocator delegate;

  public SelenideTargetLocator(Driver driver) {
    this.driver = driver;
    this.config = driver.config();
    this.webDriver = driver.getWebDriver();
    this.delegate = webDriver.switchTo();
  }

  @Override
  @Nonnull
  public WebDriver frame(int index) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(index));
    } catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with index: " + index, e);
    } catch (InvalidArgumentException e) {
      if (isFirefox62Bug(e) || isChrome75Error(e)) {
        throw frameNotFoundError("No frame found with index: " + index, e);
      }
      throw e;
    }
  }

  @Override
  @Nonnull
  public WebDriver frame(String nameOrId) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(nameOrId));
    } catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with id/name: " + nameOrId, e);
    } catch (InvalidArgumentException e) {
      if (isFirefox62Bug(e)) {
        throw frameNotFoundError("No frame found with id/name: " + nameOrId, e);
      }
      throw e;
    }
  }

  @Override
  @Nonnull
  public WebDriver frame(WebElement frameElement) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(frameElement));
    } catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with element: " + frameElement, e);
    } catch (InvalidArgumentException e) {
      if (isFirefox62Bug(e)) {
        throw frameNotFoundError("No frame found with element: " + frameElement, e);
      }
      throw e;
    }
  }

  private boolean isFirefox62Bug(InvalidArgumentException e) {
    return e.getMessage().contains("untagged enum FrameId");
  }

  private boolean isChrome75Error(InvalidArgumentException e) {
    return e.getMessage().contains("invalid argument: 'id' out of range");
  }

  @Override
  @Nonnull
  public WebDriver parentFrame() {
    return delegate.parentFrame();
  }

  @Override
  @Nonnull
  public WebDriver defaultContent() {
    return delegate.defaultContent();
  }

  @Override
  @Nonnull
  public WebElement activeElement() {
    return delegate.activeElement();
  }

  @Override
  @Nonnull
  public Alert alert() {
    try {
      return Wait().until(alertIsPresent());
    } catch (TimeoutException e) {
      throw alertNotFoundError(e);
    }
  }

  /**
   * Switch to the inner frame (last child frame in given sequence)
   */
  @Nonnull
  public WebDriver innerFrame(String... frames) {
    delegate.defaultContent();

    for (String frame : frames) {
      try {
        String selector = String.format("frame#%1$s,frame[name=%1$s],iframe#%1$s,iframe[name=%1$s]", frame);
        Wait().until(frameToBeAvailableAndSwitchToIt_fixed(By.cssSelector(selector)));
      }
      catch (NoSuchElementException | TimeoutException e) {
        throw frameNotFoundError("No frame found with id/name = " + frame, e);
      }
    }

    return webDriver;
  }

  private static ExpectedCondition<WebDriver> frameToBeAvailableAndSwitchToIt_fixed(final By locator) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      @Nullable
      public WebDriver apply(@SuppressWarnings("NullableProblems") WebDriver driver) {
        try {
          return driver.switchTo().frame(driver.findElement(locator));
        } catch (WebDriverException e) {
          return null;
        }
      }

      @Override
      @CheckReturnValue
      @Nonnull
      public String toString() {
        return "frame to be available: " + locator;
      }
    };
  }

  private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(String nameOrHandleOrTitle) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      @Nullable
      public WebDriver apply(@SuppressWarnings("NullableProblems") WebDriver driver) {
        try {
          return driver.switchTo().window(nameOrHandleOrTitle);
        } catch (NoSuchWindowException windowWithNameOrHandleNotFound) {
          try {
            return windowByTitle(driver, nameOrHandleOrTitle);
          } catch (NoSuchWindowException e) {
            return null;
          }
        }
      }

      @Override
      @CheckReturnValue
      @Nonnull
      public String toString() {
        return "window to be available by name or handle or title: " + nameOrHandleOrTitle;
      }
    };
  }

  private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(final int index) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      @Nullable
      public WebDriver apply(@SuppressWarnings("NullableProblems") WebDriver driver) {
        try {
          List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
          return driver.switchTo().window(windowHandles.get(index));
        } catch (IndexOutOfBoundsException windowWithIndexNotFound) {
          return null;
        }
      }

      @Override
      @CheckReturnValue
      @Nonnull
      public String toString() {
        return "window to be available by index: " + index;
      }
    };
  }

  /**
   * Switch to window/tab by index
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   * @param index index of window (0-based)
   */
  @CheckReturnValue
  @Nonnull
  public WebDriver window(int index) {
    try {
      return Wait().until(windowToBeAvailableAndSwitchToIt(index));
    }
    catch (TimeoutException e) {
      throw windowNotFoundError("No window found with index: " + index, e);
    }
  }

  /**
   * Switch to window/tab by index with a configurable timeout
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   * @param index index of window (0-based)
   * @param duration the timeout duration. It overrides default Config.timeout()
   */
  @CheckReturnValue
  @Nonnull
  public WebDriver window(int index, Duration duration) {
    try {
      return Wait(duration).until(windowToBeAvailableAndSwitchToIt(index));
    } catch (TimeoutException e) {
      throw windowNotFoundError("No window found with index: " + index, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   */
  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver window(String nameOrHandleOrTitle) {
    try {
      return Wait().until(windowToBeAvailableAndSwitchToIt(nameOrHandleOrTitle));
    } catch (TimeoutException e) {
      throw windowNotFoundError("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title with a configurable timeout
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   * @param duration the timeout duration. It overrides default Config.timeout()
   */
  @CheckReturnValue
  @Nonnull
  public WebDriver window(String nameOrHandleOrTitle, Duration duration) {
    try {
      return Wait(duration).until(windowToBeAvailableAndSwitchToIt(nameOrHandleOrTitle));
    } catch (TimeoutException e) {
      throw windowNotFoundError("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
    }
  }

  @Override
  public WebDriver newWindow(WindowType typeHint) {
    return webDriver.switchTo().newWindow(typeHint);
  }

  /**
   * Switch to window/tab by name/handle/title except some windows handles
   * @param title title of window/tab
   */
  @CheckReturnValue
  @Nonnull
  private static WebDriver windowByTitle(WebDriver driver, String title) {
    Set<String> windowHandles = driver.getWindowHandles();

    for (String windowHandle : windowHandles) {
      driver.switchTo().window(windowHandle);
      if (title.equals(driver.getTitle())) {
        return driver;
      }
    }
    throw new NoSuchWindowException("Window with title not found: " + title);
  }

  private SelenideWait Wait() {
    return new SelenideWait(webDriver, config.timeout(), config.pollingInterval());
  }

  private SelenideWait Wait(Duration timeout) {
    return new SelenideWait(webDriver, timeout.toMillis(), config.pollingInterval());
  }

  private Error frameNotFoundError(String message, Throwable cause) {
    FrameNotFoundException error = new FrameNotFoundException(driver, message, cause);
    return UIAssertionError.wrap(driver, error, config.timeout());
  }

  private Error windowNotFoundError(String message, Throwable cause) {
    WindowNotFoundException error = new WindowNotFoundException(driver, message, cause);
    return UIAssertionError.wrap(driver, error, config.timeout());
  }

  private Error alertNotFoundError(Throwable cause) {
    AlertNotFoundException error = new AlertNotFoundException(driver, "Alert not found", cause);
    return UIAssertionError.wrap(driver, error, config.timeout());
  }
}
