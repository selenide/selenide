package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetClosestCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetClosest getClosestCommand = new GetClosest();

  @Test
  void testExecuteMethodWithTagsStartsWithDot() {
    String argument = ".class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute(argument)).thenReturn(elementAttribute);
    when(locator.find(proxy,
      By.xpath(
        String.format("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][1]",
          argument.substring(1))),
      0)).
      thenReturn(mockedElement);
    assertThat(getClosestCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(mockedElement);
  }

  @Test
  void testExecuteMethodWithTagsThatDontStartsWithDot() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute(argument)).thenReturn(elementAttribute);
    when(locator.find(proxy, By.xpath(String.format("ancestor::%s[1]", argument)), 0)).thenReturn(mockedElement);
    assertThat(getClosestCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(mockedElement);
  }
}
