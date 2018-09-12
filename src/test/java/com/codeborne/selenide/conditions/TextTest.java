package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TextTest implements WithAssertions {
  private final String defaultText = "Hello World";
  private Text text;
  private Driver driver = mock(Driver.class);
  private WebElement mWebElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    text = new Text(defaultText);
  }

  @Test
  void testApplyForNonSelectText() {
    when(mWebElement.getText()).thenReturn(defaultText);
    assertThat(text.apply(driver, mWebElement)).isTrue();

    when(mWebElement.getText()).thenReturn("Hello");
    assertThat(text.apply(driver, mWebElement)).isFalse();
  }

  @Test
  void testApplyForSelectTagName() {
    WebElement element1 = mock(WebElement.class);
    String element1Text = "Hello";
    WebElement element2 = mock(WebElement.class);
    String element2Text = "World";

    when(mWebElement.findElements(By.tagName("option"))).thenReturn(asList(element1, element2));

    when(element1.isSelected()).thenReturn(true);
    when(element1.getText()).thenReturn(element1Text);

    when(element2.isSelected()).thenReturn(true);
    when(element2.getText()).thenReturn(element2Text);

    when(mWebElement.getTagName()).thenReturn("select");
    assertThat(text.apply(driver, mWebElement)).isFalse();

    when(element2.getText()).thenReturn(" " + element2Text);
    assertThat(text.apply(driver, mWebElement)).isTrue();
  }
}
