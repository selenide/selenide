package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.fail;

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

  private WebElement first() {
    return get(0);
  }

  @Override
  public String getTagName() {
    return first().getTagName();
  }

  @Override
  public String getAttribute(String name) {
    return first().getAttribute(name);
  }

  @Override
  public boolean isSelected() {
    for (WebElement webElement : this) {
      if (!webElement.isSelected()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isEnabled() {
    for (WebElement webElement : this) {
      if (!webElement.isEnabled()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String getText() {
    return first().getText();
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
    for (WebElement webElement : this) {
      if (!webElement.isDisplayed()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Point getLocation() {
    return first().getLocation();
  }

  @Override
  public Dimension getSize() {
    return first().getSize();
  }

  @Override
  public String getCssValue(String propertyName) {
    return first().getCssValue(propertyName);
  }

  public void shouldHaveSize(int expectedSize) {
    // wait until size = expectedSize
    if (expectedSize != size()) {
      fail("List size is " + size()+ ", but expected size is " + expectedSize);
    }
  }
}
