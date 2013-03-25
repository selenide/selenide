package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementProxy;
import com.google.common.collect.Collections2;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.Condition.not;

public class ElementsCollection extends ArrayList<SelenideElement> {
  public ElementsCollection(Collection<? extends WebElement> elements) {
    super(elements.size());
    for (WebElement element : elements) {
      add(WebElementProxy.wrap(element));
    }
  }

  public void shouldHaveSize(int expectedSize) {
    shouldHave(CollectionCondition.size(expectedSize));
  }

  public void shouldHave(CollectionCondition condition) {
    // TODO wait until condition applies
    if (!condition.apply(this)) {
      condition.fail(this);
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

  public String[] getTexts() {
    return getTexts(this);
  }

  protected static String[] getTexts(Collection<SelenideElement> elements) {
    String[] texts = new String[elements.size()];
    int i = 0;
    for (SelenideElement element : elements) {
      texts[i++] = element.getText();
    }
    return texts;
  }
}
