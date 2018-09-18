package com.codeborne.selenide;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.frameToBeAvailableAndSwitchToIt;

public class SelenideTargetLocator implements TargetLocator {
  private final WebDriver webDriver;
  private final Config config;
  private final TargetLocator delegate;

  public SelenideTargetLocator(Config config, WebDriver webDriver) {
    this.config = config;
    this.webDriver = webDriver;
    this.delegate = webDriver.switchTo();
  }

  @Override
  public WebDriver frame(int index) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(index));
    } catch (NoSuchElementException | TimeoutException e) {
      throw new NoSuchFrameException("No frame found with index: " + index, e);
    } catch (InvalidArgumentException e) {
      throw isFirefox62Bug(e) ? new NoSuchFrameException("No frame found with index: " + index, e) : e;
    }
  }

  @Override
  public WebDriver frame(String nameOrId) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(nameOrId));
    } catch (NoSuchElementException | TimeoutException e) {
      throw new NoSuchFrameException("No frame found with id/name: " + nameOrId, e);
    } catch (InvalidArgumentException e) {
      throw isFirefox62Bug(e) ? new NoSuchFrameException("No frame found with id/name: " + nameOrId, e) : e;
    }
  }

  @Override
  public WebDriver frame(WebElement frameElement) {
    try {
      return Wait().until(frameToBeAvailableAndSwitchToIt(frameElement));
    } catch (NoSuchElementException | TimeoutException e) {
      throw new NoSuchFrameException("No frame found with element: " + frameElement, e);
    } catch (InvalidArgumentException e) {
      throw isFirefox62Bug(e) ? new NoSuchFrameException("No frame found with element: " + frameElement, e) : e;
    }
  }

  private boolean isFirefox62Bug(InvalidArgumentException e) {
    return e.getMessage().contains("untagged enum FrameId");
  }

  @Override
  public WebDriver parentFrame() {
    return delegate.parentFrame();
  }

  @Override
  public WebDriver defaultContent() {
    return delegate.defaultContent();
  }

  @Override
  public WebElement activeElement() {
    return delegate.activeElement();
  }

  @Override
  public Alert alert() {
    try {
      return Wait().until(alertIsPresent());
    } catch (TimeoutException e) {
      throw new NoAlertPresentException("Alert not found", e);
    }
  }

  /**
   * Switch to the inner frame (last child frame in given sequence)
   */
  public WebDriver innerFrame(String... frames) {
    delegate.defaultContent();

    for (String frame : frames) {
      try {
        String selector = String.format("frame#%1$s,frame[name=%1$s],iframe#%1$s,iframe[name=%1$s]", frame);
        Wait().until(frameToBeAvailableAndSwitchToIt_fixed(By.cssSelector(selector)));
      }
      catch (NoSuchElementException | TimeoutException e) {
        throw new NoSuchFrameException("No frame found with id/name = " + frame, e);
      }
    }

    return webDriver;
  }

  private static ExpectedCondition<WebDriver> frameToBeAvailableAndSwitchToIt_fixed(final By locator) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      public WebDriver apply(WebDriver driver) {
        try {
          return driver.switchTo().frame(driver.findElement(locator));
        } catch (NoSuchFrameException e) {
          return null;
        } catch (WebDriverException e) {
          return null;
        }
      }

      @Override
      public String toString() {
        return "frame to be available: " + locator;
      }
    };
  }

  private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(String nameOrHandleOrTitle) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      public WebDriver apply(WebDriver driver) {
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
      public String toString() {
        return "window to be available by name or handle or title: " + nameOrHandleOrTitle;
      }
    };
  }

  private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(final int index) {
    return new ExpectedCondition<WebDriver>() {
      @Override
      public WebDriver apply(WebDriver driver) {
        try {
          List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
          return driver.switchTo().window(windowHandles.get(index));
        } catch (IndexOutOfBoundsException windowWithIndexNotFound) {
          return null;
        }
      }

      @Override
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
  public WebDriver window(int index) {
    try {
      return Wait().until(windowToBeAvailableAndSwitchToIt(index));
    }
    catch (TimeoutException e) {
      throw new NoSuchWindowException("No window found with index: " + index, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   */
  @Override
  public WebDriver window(String nameOrHandleOrTitle) {
    try {
      return Wait().until(windowToBeAvailableAndSwitchToIt(nameOrHandleOrTitle));
    } catch (TimeoutException e) {
      throw new NoSuchWindowException("No window found with name or handle or title: " + nameOrHandleOrTitle, e);
    }
  }

  /**
   * Switch to window/tab by name/handle/title except some windows handles
   * @param title title of window/tab
   */
  protected static WebDriver windowByTitle(WebDriver driver, String title) {
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
}
