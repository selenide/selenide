package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class IsDisplayedCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final WebElement mockedElement = mock(WebElement.class);
  private final IsDisplayed isDisplayedCommand = new IsDisplayed();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWhenElementIsNotPresent() {
    assertThat(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isFalse();
  }

  @Test
  void testExecuteMethodWhenElementIsNotDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(false);
    assertThat(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isFalse();
  }

  @Test
  void testExecuteMethodWhenElementIsDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(true);
    assertThat(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isTrue();
  }

  @Test
  void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    catchExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    when(locator.getWebElement()).thenReturn(mockedElement);
    doThrow(exception).when(mockedElement).isDisplayed();
    assertThat(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isFalse();
  }

  @Test
  void testExecuteMethodWhenNotFoundExceptionIsThrown() {
    catchExecuteMethodWithException(new NotFoundException());
  }

  @Test
  void testExecuteMethodWhenIndexOutOfBoundsExceptionIsThrown() {
    catchExecuteMethodWithException(new IndexOutOfBoundsException());
  }

  @Test
  void testExecuteMethodWhenExceptionWithInvalidSelectorException() {
    assertThatThrownBy(() -> catchExecuteMethodWithException(new NotFoundException("invalid selector")))
      .isInstanceOf(InvalidSelectorException.class);
  }

  /**
   * Workaround for https://github.com/SeleniumHQ/selenium/issues/9266
   * Can be removed after upgrading to Selenium 4.
   */
  @Test
  void ignoresNPE_from_isDisplayed() {
    RemoteWebElement webElement = mock(RemoteWebElement.class);
    when(webElement.isDisplayed()).thenThrow(new NullPointerException());
    when(locator.getWebElement()).thenReturn(webElement);
    Boolean isDisplayed = isDisplayedCommand.execute(proxy, locator, null);
    assertThat(isDisplayed).isFalse();
  }
}
