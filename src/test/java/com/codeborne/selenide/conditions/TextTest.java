package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TextTest {
  private final String defaultText = "Hello World";
  private Text text;
  private WebElement mWebElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    text = new Text(defaultText);
  }

  @Test
  void testApplyForNonSelectText() {
    when(mWebElement.getText()).thenReturn(defaultText);
    Assertions.assertTrue(text.apply(mWebElement));
    when(mWebElement.getText()).thenReturn("Hello");
    Assertions.assertFalse(text.apply(mWebElement));
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

    Assertions.assertFalse(text.apply(mWebElement));

    when(element2.getText()).thenReturn(" " + element2Text);

    Assertions.assertTrue(text.apply(mWebElement));
  }
}
