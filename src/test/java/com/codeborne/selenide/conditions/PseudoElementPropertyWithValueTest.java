package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.conditions.PseudoElementPropertyWithValue.JS_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class PseudoElementPropertyWithValueTest {

  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  @BeforeEach
  void setUp() {
    when(driver.executeJavaScript(JS_CODE, element, ":before", "content")).thenReturn("hello");
  }

  @Test
  void apply() {
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "hello")
      .apply(driver, element)).isTrue();
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "Hello")
      .apply(driver, element)).isTrue();
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "dummy")
      .apply(driver, element)).isFalse();
  }

  @Test
  void actualValue() {
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "hello")
      .actualValue(driver, element)).isEqualTo(":before {content: hello;}");
  }

  @Test
  void tostring() {
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "hello"))
      .hasToString("pseudo-element :before {content: hello;}");
  }
}
