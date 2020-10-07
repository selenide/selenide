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

final class ShouldNotCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final ShouldNot shouldNotCommand = new ShouldNot();
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    ShouldNot shouldNot = new ShouldNot();
    Field prefixField = shouldNot.getClass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(shouldNot);
    assertThat(prefix)
      .isNullOrEmpty();
  }

  @Test
  void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldNotCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldNotCommand.execute(proxy, locator, new Object[]{"hello"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
