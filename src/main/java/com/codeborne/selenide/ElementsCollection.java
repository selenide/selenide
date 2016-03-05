package com.codeborne.selenide;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class ElementsCollection extends ArrayList<WebElement> implements WebElement {
  public ElementsCollection(List<WebElement> elements) {
    super(elements);
  }

  @Override
  public void click() {
    for (WebElement webElement : this) {
      webElement.click();
    }
  }

  @Override
  public void submit() {
    for (WebElement webElement : this) {
      webElement.submit();
    }
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    for (WebElement webElement : this) {
      webElement.sendKeys(keysToSend);
    }
  }

  @Override
  public String getTagName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAttribute(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSelected() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getText() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<WebElement> findElements(By by) {
    List<WebElement> result = new ArrayList<WebElement>();
    for (WebElement webElement : this) {
      result.addAll(webElement.findElements(by));
    }
    return result;
  }

  @Override
  public WebElement findElement(By by) {
    for (WebElement webElement : this) {
      WebElement element = webElement.findElement(by);
      if (element != null)
        return element;
    }
    return null;
  }

  @Override
  public boolean isDisplayed() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Point getLocation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Dimension getSize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rectangle getRect() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getCssValue(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
    throw new UnsupportedOperationException();
  }
}
