package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.disabled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ShouldNotBeCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final ShouldNotBe command = new ShouldNotBe();
  private final WebElement webElement = mock();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(webElement);
  }

  @Test
  void checksEveryConditionFromGivenParameters() {
    SelenideElement returnedElement = command.execute(proxy, locator, new Object[]{disabled});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(locator).checkCondition("be ", disabled, true);
  }
}
