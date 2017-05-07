package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ElementShouldTest {

  @Test
  public void testToString() {
    String searchCriteria = "by.name: selenide";
    String prefix = "be ";
    WebElement webElementMock = Mockito.mock(WebElement.class);
    Exception exception = new Exception("Error message");
    ElementShould elementShould = new ElementShould(searchCriteria, prefix, Condition.appear, webElementMock, exception);
    String expectedString = "Element should be visible {by.name: selenide}\n" +
        "Element: '<null displayed:false></null>'\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Exception: Error message";
    assertEquals(expectedString, elementShould.toString());
  }
}
