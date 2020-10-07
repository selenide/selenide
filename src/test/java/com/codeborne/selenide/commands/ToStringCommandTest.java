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

final class ToStringCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final Driver driver = new DriverStub();
  private final WebElementSource locator = mock(WebElementSource.class);
  private final WebElement mockedFoundElement = mock(WebElement.class);
  private final ToString toStringCommand = new ToString();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void executeMethod() {
    when(mockedFoundElement.isSelected()).thenReturn(true);
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    String elementText = "text";
    when(mockedFoundElement.getText()).thenReturn(elementText);
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo("<null selected:true>text</null>");
  }

  @Test
  void executeMethodWhenWebDriverExceptionIsThrown() {
    doThrow(new WebDriverException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .contains("WebDriverException");
  }

  @Test
  void executeMethodWhenElementNotFoundIsThrown() {
    doThrow(new ElementNotFound(driver, By.name(""), Condition.visible)).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo(String.format("Element not found {By.name: }%n" +
        "Expected: visible%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms."));
  }

  @Test
  void executeMethodWhenIndexOutOfBoundExceptionIsThrown() {
    doThrow(new IndexOutOfBoundsException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertThat(elementString)
      .isEqualTo("java.lang.IndexOutOfBoundsException");
  }
}
