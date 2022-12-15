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

final class GetPrecedingCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();

  @Test
  void findsPrecedingElementUsingXpath() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);

    assertThat(new GetPreceding().execute(proxy, locator, new Object[]{42})).isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("preceding-sibling::*[43]"), 0);
  }
}
