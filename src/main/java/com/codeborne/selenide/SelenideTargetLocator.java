package com.codeborne.selenide;

import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class SelenideTargetLocator implements TargetLocator {
  private final TargetLocator delegate;

  SelenideTargetLocator(TargetLocator delegate) {
    this.delegate = delegate;
  }

  @Override
  public WebDriver frame(int index) {
    return delegate.frame(index);
  }

  @Override
  public WebDriver frame(String nameOrId) {
    return delegate.frame(nameOrId);
  }

  @Override
  public WebDriver frame(WebElement frameElement) {
    return delegate.frame(frameElement);
  }

  @Override
  public WebDriver parentFrame() {
    return delegate.parentFrame();
  }

  @Override
  public WebDriver window(String nameOrHandle) {
    return delegate.window(nameOrHandle);
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
    return delegate.alert();
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
        delegate.frame(driver.findElement(By.cssSelector(selector)));
      }
      catch (NoSuchElementException e) {
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
    List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
    delegate.window(windowHandles.get(index));
    return driver;
  }
}
