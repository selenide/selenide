package com.codeborne.selenide;

import com.codeborne.selenide.ex.AlertNotFoundException;
import com.codeborne.selenide.ex.FrameNotFoundException;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.ex.WindowNotFoundException;
import com.codeborne.selenide.impl.windows.FrameByIdOrName;
import com.codeborne.selenide.impl.windows.WindowByIndex;
import com.codeborne.selenide.impl.windows.WindowByNameOrHandle;
import org.openqa.selenium.Alert;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

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
    }
    catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with index: " + index, e);
    }
    catch (InvalidArgumentException e) {
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
    }
    catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with id/name: " + nameOrId, e);
    }
    catch (InvalidArgumentException e) {
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
    }
    catch (NoSuchElementException | TimeoutException e) {
      throw frameNotFoundError("No frame found with element: " + frameElement, e);
    }
    catch (InvalidArgumentException e) {
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
    }
    catch (TimeoutException e) {
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
        Wait().until(new FrameByIdOrName(frame));
      }
      catch (NoSuchElementException | TimeoutException e) {
        throw frameNotFoundError("No frame found with id/name = " + frame, e);
      }
    }

    return webDriver;
  }

  /**
   * Switch to window/tab by index
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   *
   * @param index index of window (0-based)
   */
  @Nonnull
  public WebDriver window(int index) {
    try {
      return Wait().until(new WindowByIndex(index));
    }
    catch (TimeoutException e) {
      throw windowNotFoundError("No window found with index: " + index, e);
    }
  }

  /**
   * Switch to window/tab by index with a configurable timeout
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   *
   * @param index    index of window (0-based)
   * @param duration the timeout duration. It overrides default Config.timeout()
   */
  @Nonnull
  public WebDriver window(int index, Duration duration) {
    try {
      return Wait(duration).until(new WindowByIndex(index));
    }
    catch (TimeoutException e) {
      throw windowNotFoundError("No window found with index: " + index, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title
   *
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   */
  @Override
  @Nonnull
  public WebDriver window(String nameOrHandleOrTitle) {
    try {
      return Wait().until(new WindowByNameOrHandle(nameOrHandleOrTitle));
    }
    catch (TimeoutException e) {
      throw windowNotFoundError("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title with a configurable timeout
   *
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   * @param duration            the timeout duration. It overrides default Config.timeout()
   */
  @Nonnull
  public WebDriver window(String nameOrHandleOrTitle, Duration duration) {
    try {
      return Wait(duration).until(new WindowByNameOrHandle(nameOrHandleOrTitle));
    }
    catch (TimeoutException e) {
      throw windowNotFoundError("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
    }
  }

  @Override
  public WebDriver newWindow(WindowType typeHint) {
    return webDriver.switchTo().newWindow(typeHint);
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
