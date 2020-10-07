package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class AttributeWithValueTest {
  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  @BeforeEach
  void setUp() {
    when(element.getAttribute("data-id")).thenReturn("actual");
  }

  @Test
  void apply() {
    assertThat(new AttributeWithValue("data-id", "actual").apply(driver, element)).isTrue();
    assertThat(new AttributeWithValue("data-id", "expected").apply(driver, element)).isFalse();

    assertThat(new MatchAttributeWithValue("data-id", "act.*").apply(driver, element)).isTrue();
    assertThat(new MatchAttributeWithValue("data-id", "exp.*").apply(driver, element)).isFalse();
  }

  @Test
  void actualValue() {
    assertThat(new AttributeWithValue("data-id", "expected").actualValue(driver, element)).isEqualTo("data-id=\"actual\"");

    assertThat(new MatchAttributeWithValue("data-id", "expected").actualValue(driver, element)).isEqualTo("data-id=\"actual\"");
  }

  @Test
  void tostring() {
    assertThat(new AttributeWithValue("data-id", "expected")).hasToString("attribute data-id=\"expected\"");

    assertThat(new MatchAttributeWithValue("data-id", "exp.*")).hasToString("match attribute data-id=\"exp.*\"");
  }
}
