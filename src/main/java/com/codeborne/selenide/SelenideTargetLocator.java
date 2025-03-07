package com.codeborne.selenide;

import com.codeborne.selenide.ex.AlertNotFoundError;
import com.codeborne.selenide.ex.FrameNotFoundError;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.ex.WindowNotFoundError;
import com.codeborne.selenide.impl.windows.FrameByIdOrName;
import com.codeborne.selenide.impl.windows.WindowByIndex;
import com.codeborne.selenide.impl.windows.WindowByNameOrHandle;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Alert;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import java.time.Duration;

import static com.codeborne.selenide.impl.Lists.list;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.frameToBeAvailableAndSwitchToIt;

public class SelenideTargetLocator implements TargetLocator {
  private static final String SWITCH_TO = SelenideLogger.getReadableSubject("switchTo");

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

  /**
   * Switch to frame by index
   * NB! Order of frames can be different in different browsers, see Selenide tests.
   *
   * @param index index of frame (0-based)
   */
  @CanIgnoreReturnValue
  @Override
  public WebDriver frame(int index) {
    return frame(Wait(), index);
  }

  /**
   * Switch to frame by index with a configurable timeout
   * NB! Order of frames can be different in different browsers, see Selenide tests.
   *
   * @param index    index of frame (0-based)
   * @param timeout the timeout duration. It overrides default Config.timeout()
   */
  @CanIgnoreReturnValue
  public WebDriver frame(int index, Duration timeout) {
    return frame(Wait(timeoutMs(timeout)), index);
  }

  /**
   * Switch to frame by name or id
   *
   * @param nameOrId name or id of frame
   */
  @Override
  @CanIgnoreReturnValue
  public WebDriver frame(String nameOrId) {
    return frame(Wait(), nameOrId);
  }

  /**
   * Switch to frame by name or id with a configurable timeout
   *
   * @param nameOrId name or id of frame
   * @param timeout            the timeout duration. It overrides default Config.timeout()
   */
  @CanIgnoreReturnValue
  public WebDriver frame(String nameOrId, Duration timeout) {
    return frame(Wait(timeoutMs(timeout)), nameOrId);
  }

  @CanIgnoreReturnValue
  private WebDriver frame(SelenideWait wait, int index) {
    return SelenideLogger.get(String.format("frame(index: %s)", index), SWITCH_TO, () -> {
      try {
        return wait.until(frameToBeAvailableAndSwitchToIt(index));
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
    });
  }

  private WebDriver frame(SelenideWait wait, String nameOrId) {
    return SelenideLogger.get("frame(" + nameOrId + ")", SWITCH_TO, () -> {
      try {
        return wait.until(frameToBeAvailableAndSwitchToIt(nameOrId));
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
    });
  }

  @CanIgnoreReturnValue
  @Override
  public WebDriver frame(WebElement frameElement) {
    return SelenideLogger.get(String.format("frame(%s)", frameElement), SWITCH_TO, () -> {
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
    });
  }

  private boolean isFirefox62Bug(InvalidArgumentException e) {
    return e.getMessage().contains("untagged enum FrameId");
  }

  private boolean isChrome75Error(InvalidArgumentException e) {
    return e.getMessage().contains("invalid argument: 'id' out of range");
  }

  @CanIgnoreReturnValue
  @Override
  public WebDriver parentFrame() {
    return SelenideLogger.get("parent frame", SWITCH_TO, delegate::parentFrame);
  }

  @CanIgnoreReturnValue
  @Override
  public WebDriver defaultContent() {
    return SelenideLogger.get("default context", SWITCH_TO, delegate::defaultContent);
  }

  @CanIgnoreReturnValue
  @Override
  public WebElement activeElement() {
    return SelenideLogger.get("active element", SWITCH_TO, delegate::activeElement);
  }

  @CanIgnoreReturnValue
  @Override
  public Alert alert() {
    return alert(Duration.ofMillis(config.timeout()));
  }

  @CanIgnoreReturnValue
  public Alert alert(@Nullable Duration timeout) {
    long timeoutMs = timeoutMs(timeout);
    try {
      return Wait(timeoutMs).until(alertIsPresent());
    }
    catch (TimeoutException e) {
      throw alertNotFoundError(e, timeoutMs);
    }
  }

  /**
   * Switch to the inner frame (last child frame in given sequence).
   * <p>
   * This method
   * <ol>
   *  <li> switches to the root frame (aka "default content"),</li>
   *  <li> switches to "firstFrame",</li>
   *  <li> switches to every of "otherFrames".</li>
   * </ol>
   */
  public void innerFrame(String firstFrame, String... otherFrames) {
    defaultContent();

    for (String frame : list(firstFrame, otherFrames)) {
      SelenideLogger.run(String.format("frame(%s)", frame), SWITCH_TO, () -> {
        try {
          Wait().until(new FrameByIdOrName(frame));
        }
        catch (NoSuchElementException | TimeoutException e) {
          throw frameNotFoundError("No frame found with id/name = " + frame, e);
        }
      });
    }
  }

  /**
   * Switch to window/tab by index
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   *
   * @param index index of window (0-based)
   */
  @CanIgnoreReturnValue
  public WebDriver window(int index) {
    return window(Wait(), index);
  }


  /**
   * Switch to window/tab by index with a configurable timeout
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   *
   * @param index    index of window (0-based)
   * @param timeout the timeout duration. It overrides default Config.timeout()
   */
  @CanIgnoreReturnValue
  public WebDriver window(int index, Duration timeout) {
    return window(Wait(timeoutMs(timeout)), index);
  }

  /**
   * Switch to window/tab by name/handle/title
   *
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   */
  @CanIgnoreReturnValue
  @Override
  public WebDriver window(String nameOrHandleOrTitle) {
    return window(Wait(), nameOrHandleOrTitle);
  }

  /**
   * Switch to window/tab by name/handle/title with a configurable timeout
   *
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   * @param timeout            the timeout duration. It overrides default Config.timeout()
   */
  @CanIgnoreReturnValue
  public WebDriver window(String nameOrHandleOrTitle, Duration timeout) {
    return window(Wait(timeoutMs(timeout)), nameOrHandleOrTitle);
  }

  private WebDriver window(SelenideWait wait, int index) {
    return SelenideLogger.get(String.format("window(index: %s)", index), SWITCH_TO, () -> {
      try {
        return wait.until(new WindowByIndex(index));
      }
      catch (TimeoutException e) {
        throw windowNotFoundError("No window found with index: " + index, e);
      }
    });
  }

  private WebDriver window(SelenideWait wait, String nameOrHandleOrTitle) {
    return SelenideLogger.get(String.format("window(%s)", nameOrHandleOrTitle), SWITCH_TO, () -> {
      try {
        return wait.until(new WindowByNameOrHandle(nameOrHandleOrTitle));
      }
      catch (TimeoutException e) {
        throw windowNotFoundError("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
      }
    });
  }

  @CanIgnoreReturnValue
  @Override
  public WebDriver newWindow(WindowType typeHint) {
    return webDriver.switchTo().newWindow(typeHint);
  }

  private SelenideWait Wait() {
    return Wait(timeoutMs(null));
  }

  private SelenideWait Wait(long timeoutMs) {
    return new SelenideWait(webDriver, timeoutMs, config.pollingInterval());
  }

  private long timeoutMs(@Nullable Duration timeout) {
    return timeout == null ? config.timeout() : timeout.toMillis();
  }

  private Error frameNotFoundError(String message, Throwable cause) {
    FrameNotFoundError error = new FrameNotFoundError(message, cause);
    return UIAssertionError.wrap(driver, error, config.timeout());
  }

  private Error windowNotFoundError(String message, Throwable cause) {
    WindowNotFoundError error = new WindowNotFoundError(message, cause);
    return UIAssertionError.wrap(driver, error, config.timeout());
  }

  private Error alertNotFoundError(Throwable cause, long timeoutMs) {
    var error = new AlertNotFoundError(cause);
    return UIAssertionError.wrap(driver, error, timeoutMs);
  }
}
