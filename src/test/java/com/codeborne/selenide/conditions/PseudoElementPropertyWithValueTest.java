package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PseudoElementPropertyWithValueTest {

  private static final String PSEUDO_ELEMENT_NAME = ":before";
  private static final String PROPERTY_NAME = "content";
  private static final String ACTUAL_VALUE = "hello";

  private Driver driver = mock(Driver.class);
  private WebElement element = mock(WebElement.class);

  @BeforeEach
  void setUp() {
    when(driver.executeJavaScript(PseudoElementPropertyWithValue.JS_CODE, element, PSEUDO_ELEMENT_NAME, PROPERTY_NAME))
      .thenReturn(ACTUAL_VALUE);
  }

  @Test
  void apply() {
    assertThat(new PseudoElementPropertyWithValue(PSEUDO_ELEMENT_NAME, PROPERTY_NAME, ACTUAL_VALUE)
      .apply(driver, element)).isTrue();
    assertThat(new PseudoElementPropertyWithValue(PSEUDO_ELEMENT_NAME, PROPERTY_NAME, "Hello")
      .apply(driver, element)).isTrue();
    assertThat(new PseudoElementPropertyWithValue(PSEUDO_ELEMENT_NAME, PROPERTY_NAME, "dummy")
      .apply(driver, element)).isFalse();
  }

  @Test
  void actualValue() {
    assertThat(new PseudoElementPropertyWithValue(PSEUDO_ELEMENT_NAME, PROPERTY_NAME, ACTUAL_VALUE)
      .actualValue(driver, element)).isEqualTo(":before {content: hello;}");
  }

  @Test
  void tostring() {
    assertThat(new PseudoElementPropertyWithValue(PSEUDO_ELEMENT_NAME, PROPERTY_NAME, ACTUAL_VALUE))
      .hasToString("pseudo-element :before {content: hello;}");
  }
}
