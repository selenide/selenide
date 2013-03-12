package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementProxy;
import com.google.common.collect.Collections2;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.WebDriverRunner.fail;

public class ElementsCollection extends ArrayList<SelenideElement> {
  public ElementsCollection(Collection<? extends WebElement> elements) {
    super(elements.size());
    for (WebElement element : elements) {
      add(WebElementProxy.wrap(element));
    }
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
