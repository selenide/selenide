package com.codeborne.selenide.impl;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
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
import static org.mockito.Mockito.*;

public class SelenideFieldDecoratorTest {

  TestPage page = new TestPage();
  WebDriver webDriver = mock(WebDriver.class);
  SelenideFieldDecorator fieldDecorator = new SelenideFieldDecorator(webDriver);

  @Test
  public void usesDefaultElementLocatorFactory() throws NoSuchFieldException {
    SelenideFieldDecorator fieldDecorator = new SelenideFieldDecorator(mock(WebDriver.class));
    assertTrue(fieldDecorator.getClass().getSuperclass().getDeclaredField("factory").getType().isAssignableFrom(DefaultElementLocatorFactory.class));
  }

  @Test
  public void decoratesSelenideElement() throws NoSuchFieldException {
    assertTrue(fieldDecorator.decorate(getClass().getClassLoader(), getField("username")) instanceof SelenideElement);
  }

  private Field getField(String fieldName) throws NoSuchFieldException {
    return page.getClass().getDeclaredField(fieldName);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decoratesListOfSelenideElements() throws NoSuchFieldException {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));
    List<SelenideElement> elements = (List<SelenideElement>) fieldDecorator.decorate(getClass().getClassLoader(), getField("rows"));
    assertEquals(2, elements.size());
    verify(webDriver).findElements(any(By.class));
    assertTrue(elements.get(0) instanceof SelenideElement);
    assertTrue(elements.get(1) instanceof SelenideElement);
  }

  @Test
  public void decoratesVanillaWebElements() throws NoSuchFieldException {
    assertFalse(fieldDecorator.decorate(getClass().getClassLoader(), getField("someDiv")) instanceof SelenideElement);
    assertTrue(fieldDecorator.decorate(getClass().getClassLoader(), getField("someDiv")) instanceof WebElement);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decoratesListOfVanillaWebElements() throws NoSuchFieldException {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));
    List<WebElement> elements = (List<WebElement>) fieldDecorator.decorate(getClass().getClassLoader(), getField("data"));
    assertEquals(2, elements.size());
    verify(webDriver).findElements(any(By.class));
    assertTrue(elements.get(0) instanceof WebElement);
    assertFalse(elements.get(0) instanceof SelenideElement);
  }

  @Test
  public void ignoresUnknownTypes() throws NoSuchFieldException {
    assertNull(fieldDecorator.decorate(getClass().getClassLoader(), getField("unsupportedField")));
  }

  @Test
  public void decoratesElementsContainerWithItsSubElements() throws NoSuchFieldException {
    StatusBlock status = (StatusBlock) fieldDecorator.decorate(getClass().getClassLoader(), getField("status"));
    WebElement statusElement = mock(WebElement.class);
    when(webDriver.findElement(By.id("status"))).thenReturn(statusElement);
    when(statusElement.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement.findElement(By.className("name"))).thenReturn(mock(WebElement.class));

    assertNotNull(status);
    assertNotNull(status.getSelf());
    status.getSelf().getText();
    verify(webDriver).findElement(By.id("status"));
    assertNotNull(status.lastLogin);
    status.lastLogin.getText();
    verify(statusElement).findElement(By.className("last-login"));
    assertNotNull(status.name);
    status.name.getText();
    verify(statusElement).findElement(By.className("name"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void decoratesElementsContainerListWithItsSubElements() throws NoSuchFieldException {
    WebElement statusElement1 = mock(WebElement.class);
    WebElement statusElement2 = mock(WebElement.class);
    when(webDriver.findElements(any(By.class))).thenReturn(asList(statusElement1, statusElement2));
    when(statusElement1.getText()).thenReturn("status element1 text");
    when(statusElement1.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement1.findElement(By.className("name"))).thenReturn(mock(WebElement.class));
    when(statusElement2.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement2.findElement(By.className("name"))).thenReturn(mock(WebElement.class));

    List<StatusBlock> statusHistory = (List<StatusBlock>) fieldDecorator.decorate(getClass().getClassLoader(), getField("statusHistory"));
    assertNotNull(statusHistory);
    verify(webDriver).findElements(By.cssSelector("table.history tr.status"));
    assertEquals(2, statusHistory.size());
    assertEquals("status element1 text", statusHistory.get(0).getSelf().getText());
    assertNotNull(statusHistory.get(0).lastLogin);
    statusHistory.get(0).lastLogin.getText();
    verify(statusElement1).findElement(By.className("last-login"));
    assertNotNull(statusHistory.get(0).name);
    statusHistory.get(0).name.getText();
    verify(statusElement1).findElement(By.className("name"));
  }


  public static class TestPage {
    SelenideElement username;
    @FindBy(css = "table tbody tr")
    List<SelenideElement> rows;
    WebElement someDiv;
    @FindBy(css = "table tbody tr")
    List<WebElement> data;
    String unsupportedField;

    @FindBy(id = "status")
    StatusBlock status;

    @FindBy(css = "table.history tr.status")
    List<StatusBlock> statusHistory;
  }

  public static class StatusBlock extends ElementsContainer {
    @FindBy(className = "last-login")
    SelenideElement lastLogin;

    @FindBy(className = "name")
    SelenideElement name;
  }
}
