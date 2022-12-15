package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class FindCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement element = mock();
  private final Find command = new Find();

  @Test
  void noArgs() {
    assertThatThrownBy(() -> command.execute(proxy, locator))
      .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void zeroLengthArgs() {
    when(locator.find(any(), any(), anyInt())).thenReturn(element);
    assertThat(command.execute(proxy, locator, By.xpath(".."))).isEqualTo(element);
    verify(locator).find(proxy, By.xpath(".."), 0);
  }

  @Test
  void moreThenOneArg() {
    when(locator.find(any(), any(), anyInt())).thenReturn(element);
    assertThat(command.execute(proxy, locator, By.xpath(".."), 1)).isEqualTo(element);
    verify(locator).find(proxy, By.xpath(".."), 1);
  }
}
