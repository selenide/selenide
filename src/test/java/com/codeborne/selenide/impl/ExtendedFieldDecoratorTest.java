package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShouldableWebElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExtendedFieldDecoratorTest {

  TestPage page = new TestPage();
  WebDriver webDriver = mock(WebDriver.class);
  ExtendedFieldDecorator fieldDecorator = new ExtendedFieldDecorator(webDriver);

  @Test
  public void usesDefaultElementLocatorFactory() throws Exception {
    ExtendedFieldDecorator fieldDecorator = new ExtendedFieldDecorator(mock(WebDriver.class));
    assertTrue(fieldDecorator.getClass().getSuperclass().getDeclaredField("factory").getType().isAssignableFrom(DefaultElementLocatorFactory.class));
  }

  @Test
  public void decoratesShouldableWebElement() throws Exception {
    assertTrue(fieldDecorator.decorate(getClass().getClassLoader(), getField("username")) instanceof ShouldableWebElement);
  }

  private Field getField(String fieldName) throws NoSuchFieldException {
    return page.getClass().getDeclaredField(fieldName);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decoratesListOfShouldableWebElements() throws Exception {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));
    List<ShouldableWebElement> elements = (List<ShouldableWebElement>) fieldDecorator.decorate(getClass().getClassLoader(), getField("rows"));
    assertEquals(2, elements.size());
    verify(webDriver).findElements(any(By.class));
    assertTrue(elements.get(0) instanceof ShouldableWebElement);
  }

  @Test
  public void decoratesVanillaWebElements() throws Exception {
    assertFalse(fieldDecorator.decorate(getClass().getClassLoader(), getField("someDiv")) instanceof ShouldableWebElement);
    assertTrue(fieldDecorator.decorate(getClass().getClassLoader(), getField("someDiv")) instanceof WebElement);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decoratesListOfVanillaWebElements() throws Exception {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));
    List<WebElement> elements = (List<WebElement>) fieldDecorator.decorate(getClass().getClassLoader(), getField("data"));
    assertEquals(2, elements.size());
    verify(webDriver).findElements(any(By.class));
    assertTrue(elements.get(0) instanceof WebElement);
    assertFalse(elements.get(0) instanceof ShouldableWebElement);
  }

  @Test
  public void ignoresUnknownTypes() throws Exception {
    assertNull(fieldDecorator.decorate(getClass().getClassLoader(), getField("unsupportedField")));
  }

  public static class TestPage {
    ShouldableWebElement username;
    @FindBy(css = "table tbody tr")
    List<ShouldableWebElement> rows;
    WebElement someDiv;
    @FindBy(css = "table tbody tr")
    List<WebElement> data;
    String unsupportedField;
  }
}
