package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSelectedOptionCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final GetSelectedOption command = new GetSelectedOption();

  @Test
  void returnsFirstSelectedOption() {
    WebElement option1 = option("Tom", false);
    WebElement option2 = option("Jerry", true);
    SelenideElement select = mockSelect(option1, option2);
    when(locator.getWebElement()).thenReturn(select);

    SelenideElement selectedElement = command.execute(proxy, locator, null);
    assertThat(selectedElement.getText()).isEqualTo("Jerry");
  }
}
