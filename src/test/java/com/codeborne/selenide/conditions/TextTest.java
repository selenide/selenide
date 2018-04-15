package com.codeborne.selenide.conditions;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextTest {
  private final String defaultText = "Hello World";
  private Text text;
  private WebElement mWebElement = mock(WebElement.class);

  @Before
  public void setup() {
    text = new Text(defaultText);
  }

  @Test
  public void testApplyForNonSelectText() {
    when(mWebElement.getText()).thenReturn(defaultText);
    assertTrue(text.apply(mWebElement));
    when(mWebElement.getText()).thenReturn("Hello");
    assertFalse(text.apply(mWebElement));
  }

  @Test
  public void testApplyForSelectTagName() {
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

    assertFalse(text.apply(mWebElement));

    when(element2.getText()).thenReturn(" " + element2Text);

    assertTrue(text.apply(mWebElement));
  }
}
