package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class IsImageCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();
  private final IsImage command = new IsImage();

  @Test
  void isNotApplicableForNonImages() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.getTagName()).thenReturn("href");
    assertThatThrownBy(() -> command.execute(proxy, locator, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Method isImage() is only applicable for img elements");
  }
}
