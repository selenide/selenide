package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ShouldNotBeCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final ShouldNotBe shouldNotBeCommand = new ShouldNotBe();
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    ShouldNotBe shouldNotBe = new ShouldNotBe();
    Field prefixField = shouldNotBe.getClass().getSuperclass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(shouldNotBe);
    assertThat(prefix)
      .isEqualToIgnoringWhitespace("be");
  }

  @Test
  void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldNotBeCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldNotBeCommand.execute(proxy, locator, new Object[]{"hello"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
