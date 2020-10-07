package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class FindCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement element1 = mock(SelenideElement.class);
  private final Find findCommand = new Find();

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
