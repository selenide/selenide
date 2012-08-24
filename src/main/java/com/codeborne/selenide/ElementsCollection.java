package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ElementsCollection extends ArrayList<WebElement> implements WebElement {
  public ElementsCollection(List<WebElement> elements) {
    super(elements);
  }

  public void click() {
    for (WebElement webElement : this) {
      webElement.click();
    }
  }

  public void submit() {
    for (WebElement webElement : this) {
      webElement.submit();
    }
  }

  public void sendKeys(CharSequence... keysToSend) {
    for (WebElement webElement : this) {
      webElement.sendKeys(keysToSend);
    }
  }

  public String getTagName() {
    throw new UnsupportedOperationException();
  }

  public String getAttribute(String name) {
    throw new UnsupportedOperationException();
  }

  public boolean isSelected() {
    throw new UnsupportedOperationException();
  }

  public boolean isEnabled() {
    throw new UnsupportedOperationException();
  }

  public String getText() {
    throw new UnsupportedOperationException();
  }

  public List<WebElement> findElements(By by) {
    List<WebElement> result = new ArrayList<WebElement>();
    for (WebElement webElement : this) {
      result.addAll(webElement.findElements(by));
    }
    return result;
  }

  public WebElement findElement(By by) {
    for (WebElement webElement : this) {
      WebElement element = webElement.findElement(by);
      if (element != null)
        return element;
    }
    return null;
  }

  public boolean isDisplayed() {
    throw new UnsupportedOperationException();
  }

  public Point getLocation() {
    throw new UnsupportedOperationException();
  }

  public Dimension getSize() {
    throw new UnsupportedOperationException();
  }

  public String getCssValue(String propertyName) {
    throw new UnsupportedOperationException();
  }
}
