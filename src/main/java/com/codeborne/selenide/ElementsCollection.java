package com.codeborne.selenide;

import com.google.common.collect.Collections2;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.WebDriverRunner.fail;

public class ElementsCollection extends ArrayList<WebElement> {
  public ElementsCollection(Collection<WebElement> elements) {
    super(elements);
  }

  public void shouldHaveSize(int expectedSize) {
    // TODO wait until size = expectedSize
    if (expectedSize != size()) {
      fail("List size is " + size()+ ", but expected size is " + expectedSize);
    }
  }

  public ElementsCollection filter(Condition filter) {
    return new ElementsCollection(Collections2.filter(this, filter));
  }
}
