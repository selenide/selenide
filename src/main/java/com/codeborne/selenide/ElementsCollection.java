package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementProxy;
import com.google.common.collect.Collections2;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.Condition.not;
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

  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(Collections2.filter(this, condition));
  }

  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(Collections2.filter(this, not(condition)));
  }

  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  public SelenideElement find(Condition condition) {
    return filter(condition).get(0);
  }

  public SelenideElement findBy(Condition condition) {
    return find(condition);
  }
}
