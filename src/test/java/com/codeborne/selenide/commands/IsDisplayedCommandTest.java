package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class IsDisplayedCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
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
}
