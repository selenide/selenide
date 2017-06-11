package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ElementShouldNotTest {

  @Test
  public void testToString() {
    String searchCriteria = "by.name: selenide";
    String prefix = "be ";
    String message = "message";
    WebElement webElementMock = mock(WebElement.class);
    Throwable exception = new Throwable("Error message");
    ElementShouldNot elementShould = new ElementShouldNot(searchCriteria, prefix, message, Condition.appear, webElementMock, exception);
    String expectedString = "Element should not be visible {by.name: selenide} because message\n" +
        "Element: '<null displayed:false></null>'\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Throwable: Error message";
    assertEquals(expectedString, elementShould.toString());
  }
}
