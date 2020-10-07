package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelenideFieldDecoratorTest implements WithAssertions {
  private TestPage page = new TestPage();
  private Config config = mock(Config.class);
  private WebDriver webDriver = mock(WebDriver.class);
  private Driver driver = new DriverStub(config, null, webDriver, null);
  private SelenidePageFactory pageFactory = new SelenidePageFactory();
  private SelenideFieldDecorator fieldDecorator = new SelenideFieldDecorator(pageFactory, driver, webDriver);

  @Test
  void usesDefaultElementLocatorFactory() throws NoSuchFieldException {
    SelenideFieldDecorator fieldDecorator = new SelenideFieldDecorator(pageFactory, driver, webDriver);
    Field factoryField = fieldDecorator.getClass().getSuperclass().getDeclaredField("factory");
    assertThat(factoryField.getType())
      .isAssignableFrom(DefaultElementLocatorFactory.class);
  }

  @Test
  void decoratesSelenideElement() throws NoSuchFieldException {
    assertThat(fieldDecorator.decorate(getClass().getClassLoader(), getField("username")))
      .isInstanceOf(SelenideElement.class);
  }

  private Field getField(String fieldName) throws NoSuchFieldException {
    return page.getClass().getDeclaredField(fieldName);
  }

  @Test
  @SuppressWarnings("unchecked")
  void decoratesListOfSelenideElements() throws NoSuchFieldException {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));

    Object decoratedField = fieldDecorator.decorate(getClass().getClassLoader(), getField("rows"));

    assertThat(decoratedField)
      .isInstanceOf(List.class);
    List<SelenideElement> elements = (List<SelenideElement>) decoratedField;
    assertThat(elements)
      .hasSize(2);
    verify(webDriver).findElements(any(By.class));
    assertThat(elements.get(0))
      .isInstanceOf(SelenideElement.class);
    assertThat(elements.get(1))
      .isInstanceOf(SelenideElement.class);
  }

  @Test
  void decoratesVanillaWebElements() throws NoSuchFieldException {
    final Object someDiv = fieldDecorator.decorate(getClass().getClassLoader(), getField("someDiv"));
    assertThat(someDiv)
      .withFailMessage("someDiv should not be instance of SelenideElement. Actual class: " + someDiv.getClass())
      .isInstanceOf(SelenideElement.class);
  }

  @Test
  @SuppressWarnings("unchecked")
  void decoratesListOfVanillaWebElements() throws NoSuchFieldException {
    when(webDriver.findElements(any(By.class))).thenReturn(asList(mock(WebElement.class), mock(WebElement.class)));
    List<WebElement> elements = (List<WebElement>) fieldDecorator.decorate(getClass().getClassLoader(), getField("data"));
    assertThat(elements)
      .hasSize(2);
    verify(webDriver).findElements(any(By.class));
    assertThat(elements.get(0))
      .isInstanceOf(WebElement.class);
    assertThat(elements.get(1))
      .isNotInstanceOf(SelenideElement.class);
  }

  @Test
  void ignoresUnknownTypes() throws NoSuchFieldException {
    assertThat(fieldDecorator.decorate(getClass().getClassLoader(), getField("unsupportedField")))
      .isNull();
  }

  @Test
  void decoratesElementsContainerWithItsSubElements() throws NoSuchFieldException {
    StatusBlock status = (StatusBlock) fieldDecorator.decorate(getClass().getClassLoader(), getField("status"));
    WebElement statusElement = mock(WebElement.class);
    when(webDriver.findElement(By.id("status"))).thenReturn(statusElement);
    when(statusElement.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement.findElement(By.className("name"))).thenReturn(mock(WebElement.class));

    assertThat(status)
      .isNotNull();
    assertThat(status.getSelf())
      .isNotNull();
    status.getSelf().getText();
    verify(webDriver).findElement(By.id("status"));
    assertThat(status.lastLogin)
      .isNotNull();
    status.lastLogin.getText();
    verify(statusElement).findElement(By.className("last-login"));
    assertThat(status.name)
      .isNotNull();
    status.name.getText();
    verify(statusElement).findElement(By.className("name"));
  }

  @SuppressWarnings("unchecked")
  @Test
  void decoratesElementsContainerListWithItsSubElements() throws NoSuchFieldException {
    WebElement statusElement1 = mock(WebElement.class);
    WebElement statusElement2 = mock(WebElement.class);
    when(webDriver.findElements(any(By.class))).thenReturn(asList(statusElement1, statusElement2));
    when(statusElement1.getText()).thenReturn("status element1 text");
    when(statusElement1.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement1.findElement(By.className("name"))).thenReturn(mock(WebElement.class));
    when(statusElement2.findElement(By.className("last-login"))).thenReturn(mock(WebElement.class));
    when(statusElement2.findElement(By.className("name"))).thenReturn(mock(WebElement.class));

    Object decoratedField = fieldDecorator.decorate(getClass().getClassLoader(), getField("statusHistory"));

    assertThat(decoratedField)
      .isInstanceOf(List.class);
    List<StatusBlock> statusHistory = (List<StatusBlock>) decoratedField;
    assertThat(statusHistory)
      .isNotNull();
    verify(webDriver).findElements(By.cssSelector("table.history tr.status"));
    assertThat(statusHistory)
      .hasSize(2);
    assertThat(statusHistory.get(0).getSelf().getText())
      .isEqualTo("status element1 text");
    assertThat(statusHistory.get(0).lastLogin)
      .isNotNull();
    statusHistory.get(0).lastLogin.getText();
    verify(statusElement1).findElement(By.className("last-login"));
    assertThat(statusHistory.get(0).name)
      .isNotNull();
    statusHistory.get(0).name.getText();
    verify(statusElement1).findElement(By.className("name"));
  }

  static class TestPage {
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

  static class StatusBlock extends ElementsContainer {
    @FindBy(className = "last-login")
    SelenideElement lastLogin;

    @FindBy(className = "name")
    SelenideElement name;
  }
}
