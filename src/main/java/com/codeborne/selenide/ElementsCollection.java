package com.codeborne.selenide;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.fail;

public class ElementsCollection extends ArrayList<WebElement> {
  public ElementsCollection(List<WebElement> elements) {
    super(elements);
  }

  public void shouldHaveSize(int expectedSize) {
    // TODO wait until size = expectedSize
    if (expectedSize != size()) {
      fail("List size is " + size()+ ", but expected size is " + expectedSize);
    }
  }
}
