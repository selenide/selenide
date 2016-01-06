package com.codeborne.selenide;

import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.frameToBeAvailableAndSwitchToIt;

public class SelenideTargetLocator implements TargetLocator {
  private final TargetLocator delegate;

  SelenideTargetLocator(TargetLocator delegate) {
    this.delegate = delegate;
  }

  @Override
  public WebDriver frame(int index) {
    return Wait().until(frameToBeAvailableAndSwitchToIt(index));
  }

  @Override
  public WebDriver frame(String nameOrId) {
    return Wait().until(frameToBeAvailableAndSwitchToIt(nameOrId));
  }

  @Override
  public WebDriver frame(WebElement frameElement) {
    return waitForFrame(frameElement);
  }

  protected WebDriver waitForFrame(WebElement element) {
    return Wait().until(frameToBeAvailableAndSwitchToIt(element));
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
    return Wait().until(alertIsPresent());
  }

  /**
   * Switch to the inner frame (last child frame in given sequence)
   */
  public WebDriver innerFrame(String... frames) {
    WebDriver driver = getWebDriver();
    delegate.defaultContent();

    for (String frame : frames) {
      try {
        String selector = String.format("frame#%1$s,frame[name=%1$s],iframe#%1$s,iframe[name=%1$s]", frame);
        waitForFrame(driver.findElement(By.cssSelector(selector)));
      }
      catch (NoSuchElementException | TimeoutException e) {
        throw new NoSuchFrameException("No frame found with id/name = " + frame, e);
      }
    }

    return driver;
  }

  /**
   * Switch to window/tab by index
   * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
   * @param index index of window (0-based)
   */
  public WebDriver window(int index) {
    WebDriver driver = getWebDriver();
    List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
    delegate.window(windowHandles.get(index));
    return driver;
  }

  /**
   * Switch to window/tab by name/handle/title
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   */
  @Override
  public WebDriver window(String nameOrHandleOrTitle) {
    try {
      return delegate.window(nameOrHandleOrTitle);
    }
    catch (NoSuchWindowException windowWithNameOrTitleNotFound) {
      return windowExceptHandles(nameOrHandleOrTitle);
    }
  }

  /**
   * Switch to window/tab by name/handle/title except some windows handles
   * @param nameOrHandleOrTitle name or handle or title of window/tab
   * @param exceptHandles window handles that should be ignored
   */
  protected WebDriver windowExceptHandles(String nameOrHandleOrTitle, String... exceptHandles) {
    WebDriver driver = getWebDriver();
    
    Set<String> windowHandles = driver.getWindowHandles();
    windowHandles.removeAll(Arrays.asList(exceptHandles));
    
    for (String windowHandle : windowHandles) {
      driver.switchTo().window(windowHandle);
      if (nameOrHandleOrTitle.equals(driver.getTitle())) {
        return driver;
      }
    }
    throw new NoSuchWindowException("Window with id/name/title not found: " + nameOrHandleOrTitle);
  }
}
