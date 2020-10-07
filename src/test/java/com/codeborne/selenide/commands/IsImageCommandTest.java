package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class IsImageCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final IsImage isImageCommand = new IsImage();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWhenElementIsNotImage() {
    when(mockedElement.getTagName()).thenReturn("href");
    try {
      isImageCommand.execute(proxy, locator, new Object[]{"something more"});
    } catch (IllegalArgumentException exception) {
      assertThat(exception)
        .hasMessage("Method isImage() is only applicable for img elements");
    }
  }
}
