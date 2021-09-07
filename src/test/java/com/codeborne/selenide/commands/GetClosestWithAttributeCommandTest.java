package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetClosestWithAttributeCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetClosestWithAttribute getClosestWithAttributeNameCommand = new GetClosestWithAttribute();

  @Test
  void testExecuteMethodWithAttribute() {
    String attributeName = "test-argument";
    when(locator.find(proxy, By.xpath(String.format("ancestor::*[@%s][1]", attributeName)), 0))
      .thenReturn(mockedElement);
    assertThat(getClosestWithAttributeNameCommand
      .execute(proxy, locator, new Object[]{attributeName})
    ).isEqualTo(mockedElement);
  }
}
