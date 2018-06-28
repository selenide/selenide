package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement element1;
  private Find findCommand;

  @BeforeEach
  void setup() {
    findCommand = new Find();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
  }

  @Test
  void testExecuteMethodWithNoArgsPassed() {
    assertThatThrownBy(() -> findCommand.execute(proxy, locator))
      .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void testExecuteMethodWithZeroLengthArgs() {
    when(locator.find(proxy, By.xpath(".."), 0)).thenReturn(element1);
    assertThat(findCommand.execute(proxy, locator, By.xpath("..")))
      .isEqualTo(element1);
  }

  @Test
  void testExecuteMethodWithMoreThenOneArgsList() {
    when(locator.find(proxy, By.xpath(".."), 1)).thenReturn(element1);
    assertThat(findCommand.execute(proxy, locator, By.xpath(".."), 1))
      .isEqualTo(element1);
  }
}
