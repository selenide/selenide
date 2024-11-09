package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetParentCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement webElement = mock();

  @Test
  void findsParentElementUsingXpath() {
    when(locator.find(any(), any(), anyInt())).thenReturn(webElement);
    assertThat(new GetParent().execute(proxy, locator, null))
      .isEqualTo(webElement);
    verify(locator).find(proxy, By.xpath(".."), 0);
  }
}
