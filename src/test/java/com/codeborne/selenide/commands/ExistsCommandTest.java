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
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExistsCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private WebElement element;
  private Exists existsCommand;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element = mock(WebElement.class);
    existsCommand = new Exists();
  }

  @Test
  void testExistExecuteMethod() {
    when(locator.getWebElement()).thenReturn(null);
    assertThat(existsCommand.execute(proxy, locator, null))
      .isFalse();
    when(locator.getWebElement()).thenReturn(element);
    assertThat(existsCommand.execute(proxy, locator, null))
      .isTrue();
  }

  @Test
  void testExistExecuteMethodWithWebDriverException() {
    checkExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void checkExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    assertThat(existsCommand.execute(proxy, locator, null))
      .isFalse();
  }

  @Test
  void testExistExecuteMethodElementNotFoundException() {
    Driver driver = new DriverStub();
    checkExecuteMethodWithException(new ElementNotFound(driver, "", Condition.appear));
  }

  @Test
  void testExistsExecuteMethodInvalidSelectorException() {
    assertThatThrownBy(() -> checkExecuteMethodWithException(new InvalidSelectorException("Element is not selectable")))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void testExistsExecuteMethodWithIndexOutOfBoundException() {
    checkExecuteMethodWithException(new IndexOutOfBoundsException("Out of bound"));
  }
}
