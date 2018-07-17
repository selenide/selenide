package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
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

class MatchesCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private Matches matchesCommand;

  @BeforeEach
  void setup() {
    matchesCommand = new Matches();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
  }

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
