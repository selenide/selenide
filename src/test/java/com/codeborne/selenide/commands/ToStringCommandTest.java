package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToStringCommandTest implements WithAssertions {
  private SelenideElement proxy = mock(SelenideElement.class);
  private Driver driver = new DriverStub();
  private WebElementSource locator = mock(WebElementSource.class);
  private WebElement mockedFoundElement = mock(WebElement.class);
  private ToString toStringCommand = new ToString();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testExecuteMethod() {
    when(mockedFoundElement.isSelected()).thenReturn(true);
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    String elementText = "text";
    when(mockedFoundElement.getText()).thenReturn(elementText);
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo("<null selected:true>text</null>");
  }

  @Test
  void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    doThrow(new WebDriverException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .contains("WebDriverException");
  }

  @Test
  void testExecuteMethodWhenElementNotFoundIsThrown() {
    doThrow(new ElementNotFound(driver, By.name(""), Condition.visible)).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo("Element not found {By.name: }");
  }

  @Test
  void testExecuteMethodWhenIndexOutOfBoundExceptionIsThrown() {
    doThrow(new IndexOutOfBoundsException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo("java.lang.IndexOutOfBoundsException");
  }
}
