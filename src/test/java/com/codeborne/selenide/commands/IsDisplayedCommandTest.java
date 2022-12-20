package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class IsDisplayedCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final WebElement mockedElement = mock();
  private final IsDisplayed command = new IsDisplayed();

  @BeforeEach
  void setUp() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void elementIsNotDisplayed() {
    when(mockedElement.isDisplayed()).thenReturn(false);
    assertThat(command.execute(proxy, locator, null)).isFalse();
    verify(mockedElement).isDisplayed();
  }

  @Test
  void elementIsDisplayed() {
    when(mockedElement.isDisplayed()).thenReturn(true);
    assertThat(command.execute(proxy, locator, null)).isTrue();
    verify(mockedElement).isDisplayed();
  }

  @Test
  void returnsFalse_whenWebDriverExceptionIsThrown() {
    doThrow(new WebDriverException()).when(mockedElement).isDisplayed();
    assertThat(command.execute(proxy, locator, null)).isFalse();
  }

  @Test
  void returnsFalse_whenNotFoundExceptionIsThrown() {
    doThrow(new NotFoundException()).when(mockedElement).isDisplayed();
    assertThat(command.execute(proxy, locator, null)).isFalse();
  }

  @Test
  void returnsFalse_whenIndexOutOfBoundsExceptionIsThrown() {
    doThrow(new IndexOutOfBoundsException()).when(mockedElement).isDisplayed();
    assertThat(command.execute(proxy, locator, null)).isFalse();
  }

  @Test
  void throwsInvalidSelectorExceptionAsIs() {
    doThrow(new InvalidSelectorException("wrong xpath")).when(mockedElement).isDisplayed();
    assertThatThrownBy(() -> command.execute(proxy, locator, null))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageStartingWith("wrong xpath");
  }
}
