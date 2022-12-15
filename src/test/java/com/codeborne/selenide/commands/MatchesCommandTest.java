package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class MatchesCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();
  private final Matches command = new Matches();

  @Test
  void whenNoElementFound() {
    when(locator.getWebElement()).thenReturn(null);
    assertThat(command.execute(proxy, locator, new Object[]{disabled}))
      .isFalse();
  }

  @Test
  void elementDoesNotMeetCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertThat(command.execute(proxy, locator, new Object[]{disabled}))
      .isFalse();
  }

  @Test
  void elementMeetsCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertThat(command.execute(proxy, locator, new Object[]{enabled}))
      .isTrue();
  }

  @Test
  void webDriverExceptionIsThrown() {
    doThrow(new WebDriverException()).when(locator).getWebElement();
    assertThat(command.execute(proxy, locator, new Object[]{enabled}))
      .isFalse();
  }

  @Test
  void notFoundExceptionIsThrown() {
    doThrow(new NotFoundException()).when(locator).getWebElement();
    assertThat(command.execute(proxy, locator, new Object[]{enabled}))
      .isFalse();
  }

  @Test
  void indexOutOfBoundsExceptionIsThrown() {
    doThrow(new IndexOutOfBoundsException()).when(locator).getWebElement();
    assertThat(command.execute(proxy, locator, new Object[]{enabled}))
      .isFalse();
  }

  @Test
  void invalidSelectorIsThrownAsIs() {
    doThrow(new InvalidSelectorException("wrong xpath")).when(locator).getWebElement();
    assertThatThrownBy(() -> command.execute(proxy, locator, new Object[]{enabled}))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageStartingWith("wrong xpath");
  }
}
