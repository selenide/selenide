package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class MatchesCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final Matches matchesCommand = new Matches();

  @Test
  void testExecuteMethodWhenNoElementFound() {
    when(locator.getWebElement()).thenReturn(null);
    assertThat(matchesCommand.execute(proxy, locator, new Object[]{Condition.disabled}))
      .isFalse();
  }

  @Test
  void testExecuteMethodWhenElementDoesntMeetCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertThat(matchesCommand.execute(proxy, locator, new Object[]{Condition.disabled}))
      .isFalse();
  }

  @Test
  void testExecuteMethodWhenElementMeetsCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertThat(matchesCommand.execute(proxy, locator, new Object[]{Condition.enabled}))
      .isTrue();
  }

  @Test
  void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    catchExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    assertThat(matchesCommand.execute(proxy, locator, new Object[]{Condition.enabled}))
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

  @Test
  void testExecuteMethodWhenRunTimeExceptionIsThrown() {
    assertThatThrownBy(() -> catchExecuteMethodWithException(new RuntimeException("invalid selector")))
      .isInstanceOf(InvalidSelectorException.class);
  }
}
