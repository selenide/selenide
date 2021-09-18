package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public class DummyWebDriver implements WebDriver {
  @Override
  public void get(String url) { }

  @Override
  public String getCurrentUrl() {
    return "https://some.test.com";
  }

  @Override
  public String getTitle() {
    return "page title";
  }

  @Override
  public List<WebElement> findElements(By by) {
    return emptyList();
  }

  @Override
  public WebElement findElement(By by) {
    throw new NoSuchElementException(by.toString());
  }

  @Override
  public String getPageSource() {
    return "<html>mocked driver</html>";
  }

  @Override
  public void close() { }

  @Override
  public void quit() { }

  @Override
  public Set<String> getWindowHandles() {
    return emptySet();
  }

  @Override
  public String getWindowHandle() {
    return "w-1";
  }

  @Override
  public TargetLocator switchTo() {
    return null;
  }

  @Override
  public Navigation navigate() {
    return null;
  }

  @Override
  public Options manage() {
    return null;
  }
}
