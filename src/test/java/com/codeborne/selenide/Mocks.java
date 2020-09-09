package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks {
  public static SelenideElement mockElement(String text) {
    return mockElement("div", text);
  }

  public static SelenideElement mockElement(String tag, String text) {
    SelenideElement element = mock(SelenideElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    return element;
  }

  public static WebElement mockWebElement(String tag, String text) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    when(element.isDisplayed()).thenReturn(true);
    return element;
  }

  public static WebElementsCollection mockCollection(String description, WebElement... elements) {
    Driver driver = mock(Driver.class);
    when(driver.config()).thenReturn(new SelenideConfig());

    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.description()).thenReturn(description);
    when(collection.getElements()).thenReturn(asList(elements));
    for (int i = 0; i < elements.length; i++) {
      when(collection.getElement(i)).thenReturn(elements[i]);
    }
    return collection;
  }
}
