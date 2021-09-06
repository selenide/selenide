package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetClosestWithAttributeAndValueCommandTest implements WithAssertions {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetClosestWithAttributeAndValue getClosestWithAttributeNameAndValueCommand =
    new GetClosestWithAttributeAndValue();

  @Test
  void testExecuteMethodWithAttributeNameAndValue() {
    String attributeName = "test-argument";
    String attributeValue = "test-value";
    when(mockedElement.getAttribute(attributeName)).thenReturn(attributeValue);
    when(locator.find(proxy, By.xpath(String.format("ancestor::*[@%s='%s']", attributeName, attributeValue)), 0))
      .thenReturn(mockedElement);
    assertThat(getClosestWithAttributeNameAndValueCommand
      .execute(proxy, locator, new Object[]{attributeName, attributeValue, "something more"})
    ).isEqualTo(mockedElement);
  }
}
