package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetSiblingCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetSibling getSiblingCommand = new GetSibling();

  @Test
  void executeMethod() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);

    assertThat(getSiblingCommand.execute(proxy, locator, new Object[]{0})).isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("following-sibling::*[1]"), 0);
  }
}
