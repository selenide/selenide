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

final class GetLastChildTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement webElement = mock();
  private final Find find = mock();
  private final GetLastChild command = new GetLastChild(find);

  @Test
  void findsLastChildUsingXpath() {
    when(find.execute(any(), any(), any(), anyInt())).thenReturn(webElement);
    assertThat(command.execute(proxy, locator, null)).isEqualTo(webElement);
    verify(find).execute(proxy, locator, By.xpath("*[last()]"), 0);
  }
}
