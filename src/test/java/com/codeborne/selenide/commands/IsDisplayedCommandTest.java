package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class IsDisplayedCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final WebElement mockedElement = mock(WebElement.class);
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

  /**
   * Workaround for https://github.com/SeleniumHQ/selenium/issues/9266
   * Can be removed after upgrading to Selenium 4.
   */
  @Test
  void ignoresNPE_from_isDisplayed() {
    RemoteWebElement webElement = mock(RemoteWebElement.class);
    when(webElement.isDisplayed()).thenThrow(new NullPointerException());
    when(locator.getWebElement()).thenReturn(webElement);

    assertThat(command.execute(proxy, locator, null)).isFalse();
  }
}
