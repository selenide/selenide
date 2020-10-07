package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExistsCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final WebElement element = mock(WebElement.class);
  private final Exists existsCommand = new Exists();

  @Test
  void testExistExecuteMethod() {
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
