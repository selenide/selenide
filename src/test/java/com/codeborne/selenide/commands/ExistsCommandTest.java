package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExistsCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final WebElement element = mock();
  private final Exists existsCommand = new Exists();

  @Test
  void elementExists() {
    when(locator.getWebElement()).thenReturn(element);
    assertThat(existsCommand.execute(proxy, locator, null))
      .isTrue();
  }

  @Test
  void elementDoesNotExist_ifRaisedWebDriverException() {
    doThrow(new WebDriverException("element not found")).when(locator).getWebElement();
    assertThat(existsCommand.execute(proxy, locator, null))
      .isFalse();
  }

  @Test
  void elementDoesNotExist_ifRaisedElementNotFound() {
    doThrow(ElementNotFound.class).when(locator).getWebElement();
    assertThat(existsCommand.execute(proxy, locator, null))
      .isFalse();
  }

  @Test
  void elementDoesNotExist_ifRaisedIndexOutOfBoundsException() {
    doThrow(new IndexOutOfBoundsException("Out of bounds")).when(locator).getWebElement();
    assertThat(existsCommand.execute(proxy, locator, null))
      .isFalse();
  }

  @Test
  void invalidSelectorException_shouldBeThrownAsIs() {
    doThrow(new InvalidSelectorException("invalid xpath")).when(locator).getWebElement();
    assertThatThrownBy(() -> existsCommand.execute(proxy, locator, null))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageStartingWith("invalid xpath");
  }
}
