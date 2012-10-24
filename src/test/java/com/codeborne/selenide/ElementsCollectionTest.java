package com.codeborne.selenide;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementsCollectionTest {
  WebElement div = mock(WebElement.class);
  WebElement span = mock(WebElement.class);
  WebElement h1a = mock(WebElement.class);
  WebElement h1b = mock(WebElement.class);

  ElementsCollection elements = new ElementsCollection(asList(div, span));

  @Before
  public void mockElements() {
    when(div.getTagName()).thenReturn("div");
    when(span.getTagName()).thenReturn("span");
  }

  @Test
  public void hasAllUsualCollectionMethods() {
    assertEquals(2, elements.size());
    assertSame(div, elements.get(0));
    assertSame(span, elements.get(1));
  }

  @Test
  public void clicksEveryElement() {
    elements.click();
    verify(div).click();
    verify(span).click();
  }

  @Test
  public void findsFirstMatchedElement() {
    when(div.findElement(By.tagName("h1"))).thenReturn(h1a);
    when(span.findElement(By.tagName("h1"))).thenReturn(h1b);
    assertSame(h1a, elements.findElement(By.tagName("h1")));

    when(div.findElement(By.tagName("h1"))).thenReturn(null);
    assertSame(h1b, elements.findElement(By.tagName("h1")));

    when(span.findElement(By.tagName("h1"))).thenReturn(null);
    assertNull(elements.findElement(By.tagName("h1")));
  }

  @Test
  public void findsAllMatchedElements() {
    when(div.findElements(By.tagName("h1"))).thenReturn(asList(h1a));
    when(span.findElements(By.tagName("h1"))).thenReturn(asList(h1b));
    assertEquals(asList(h1a, h1b), elements.findElements(By.tagName("h1")));
  }

  @Test
  public void getAttributeOfFirstElement() {
    when(div.getAttribute("name")).thenReturn("name-div");
    when(span.getAttribute("name")).thenReturn("name-span");
    assertEquals("name-div", elements.getAttribute("name"));

    when(div.getAttribute("name")).thenReturn(null);
    assertNull(elements.getAttribute("name"));
  }

  @Test
  public void getsLocationOfFirsElement() {
    Point point = new Point(100, 200);
    when(div.getLocation()).thenReturn(point);
    assertSame(point, elements.getLocation());
  }
}
