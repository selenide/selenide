package com.codeborne.selenide;

import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks {
  public static SelenideElement mockElement(String text) {
    return mockElement("div", text);
  }

  public static SelenideElement mockElement(String tag, String text) {
    SelenideElement element = mock();
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    return element;
  }

  public static WebElement elementWithAttribute(String name, String value) {
    WebElement element = mock();
    when(element.getAttribute(name)).thenReturn(value);
    return element;
  }

  public static SelenideElement mockSelect(WebElement... options) {
    SelenideElement select = mockElement("select", "");
    when(select.isSelected()).thenReturn(true);
    when(select.isEnabled()).thenReturn(true);
    when(select.findElements(By.tagName("option"))).thenReturn(asList(options));
    return select;
  }

  public static WebElement option(String text) {
    return option(text, false);
  }

  public static WebElement option(String text, boolean selected) {
    WebElement option = mockElement("option", text);
    when(option.isSelected()).thenReturn(selected);
    return option;
  }

  public static GetSelectedOptionText selectWithSelectedText(String text) {
    return new DummyGetSelectedOptionText(text);
  }

  public static WebElement mockWebElement(String tag, String text) {
    WebElement element = mock();
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    return element;
  }

  public static ElementsCollection mockCollection(WebElement... elements) {
    return new ElementsCollection(mockCollection("", elements));
  }

  public static CollectionSource mockCollection(String description, WebElement... elements) {
    Driver driver = new DriverStub(new SelenideConfig());

    CollectionSource collection = mock();
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn(description);
    when(collection.shortDescription()).thenReturn(description);
    when(collection.description()).thenReturn(description);
    when(collection.getAlias()).thenReturn(NONE);
    when(collection.getElements()).thenReturn(asList(elements));
    for (int i = 0; i < elements.length; i++) {
      when(collection.getElement(i)).thenReturn(elements[i]);
    }
    return collection;
  }
}
