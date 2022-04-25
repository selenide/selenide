package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.Field;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class SelenidePageFactoryTest {
  private final TestPage page = new TestPage();
  private final Config config = new SelenideConfig();
  private final WebDriver webDriver = mock(WebDriver.class);
  private final Driver driver = new DriverStub(config, new Browser("netscape navigator", false), webDriver, null);
  private final SelenidePageFactory pageFactory = new SelenidePageFactory();
  private final ClassLoader cl = getClass().getClassLoader();

  @Test
  void decoratesSelenideElement() throws NoSuchFieldException {
    assertThat(pageFactory.decorate(cl, driver, null, getField("username"), new ByIdOrName("username")))
      .isInstanceOf(SelenideElement.class);
  }

  @Test
  void decoratesElementsCollection() throws NoSuchFieldException {
    TestPageWithElementsCollection page = new TestPageWithElementsCollection();

    Object decoratedField = pageFactory.decorate(cl, driver, null,
      getField(page, "rows"), By.cssSelector("table tbody tr"));

    assertThat(decoratedField).isInstanceOf(ElementsCollection.class);
    verifyNoMoreInteractions(webDriver);
  }

  @Test
  void decoratesListOfSelenideElements() throws NoSuchFieldException {
    WebElement element1 = mock(WebElement.class);
    WebElement element2 = mock(WebElement.class);
    when(webDriver.findElements(any(By.class))).thenReturn(asList(element1, element2));

    Object decoratedField = pageFactory.decorate(cl, driver, null, getField("rows"), By.cssSelector("table tbody tr"));
    assertThat(decoratedField).isInstanceOf(ElementsCollection.class);
    verifyNoMoreInteractions(webDriver);

    ElementsCollection elementsCollection = (ElementsCollection) decoratedField;
    Object[] elements = elementsCollection.toArray();
    assertThat(elements).hasSize(2);
    verify(webDriver).findElements(By.cssSelector("table tbody tr"));
    assertThat(elements[0]).isInstanceOf(SelenideElement.class);
    assertThat(elements[0]).isInstanceOf(SelenideElement.class);
  }

  @Test
  void decoratesVanillaWebElements() throws NoSuchFieldException {
    final Object someDiv = pageFactory.decorate(cl, driver, null, getField("someDiv"), new ByIdOrName("someDiv"));
    assertThat(someDiv).isNotNull();
    assertThat(someDiv)
      .withFailMessage("someDiv should be instance of SelenideElement. Actual class: " + someDiv.getClass())
      .isInstanceOf(SelenideElement.class);
  }

  @Test
  @SuppressWarnings("unchecked")
  void decoratesListOfVanillaWebElements() throws NoSuchFieldException {
    WebElement element1 = mock(WebElement.class);
    WebElement element2 = mock(WebElement.class);
    when(webDriver.findElements(any(By.class))).thenReturn(asList(element1, element2));

    List<WebElement> elements = (List<WebElement>) pageFactory.decorate(cl, driver, null,
      getField("data"), By.cssSelector("table tbody tr"));

    assertThat(elements).hasSize(2);
    verify(webDriver).findElements(By.cssSelector("table tbody tr"));
    assertThat(elements.get(0)).isInstanceOf(SelenideElement.class);
    assertThat(elements.get(1)).isInstanceOf(SelenideElement.class);
  }

  @Test
  void ignoresUnknownTypes() throws NoSuchFieldException {
    assertThat(pageFactory.decorate(cl, driver, null, getField("unsupportedField"), new ByIdOrName("unsupportedField")))
      .isNull();
  }

  @Test
  void decoratesElementsContainerWithItsSubElements() throws NoSuchFieldException {
    StatusBlock status = (StatusBlock) pageFactory.decorate(cl, driver, null, getField("status"), By.id("status"));
    WebElement statusElement = mockWebElement("div", "the status");
    WebElement lastLogin = mockWebElement("div", "03.03.2003");
    WebElement name = mockWebElement("div", "lena");
    when(webDriver.findElement(any())).thenReturn(statusElement);
    when(statusElement.findElement(By.className("last-login"))).thenReturn(lastLogin);
    when(statusElement.findElement(By.className("name"))).thenReturn(name);

    assertThat(status).isNotNull();
    assertThat(status.getSelf()).isNotNull();
    status.getSelf().shouldHave(text("the status"));
    verify(webDriver).findElement(By.id("status"));

    assertThat(status.lastLogin).isNotNull();
    status.lastLogin.shouldHave(text("03.03.2003"));
    verify(statusElement).findElement(By.className("last-login"));

    assertThat(status.name).isNotNull();
    status.name.shouldHave(text("lena"));
    verify(statusElement).findElement(By.className("name"));
  }

  @SuppressWarnings("unchecked")
  @Test
  void decoratesElementsContainerListWithItsSubElements() throws NoSuchFieldException {
    WebElement statusElement1 = mockWebElement("div", "status element1 text");
    WebElement statusElement2 = mockWebElement("div", "status element2 text");
    when(webDriver.findElement(any())).thenReturn(statusElement1);
    when(webDriver.findElements(any())).thenReturn(asList(statusElement1, statusElement2));

    WebElement lastLogin1 = mockWebElement("div", "01.01.2001");
    when(statusElement1.findElement(By.className("last-login"))).thenReturn(lastLogin1);
    WebElement name1 = mockWebElement("div", "john");
    when(statusElement1.findElement(By.className("name"))).thenReturn(name1);
    WebElement lastLogin2 = mockWebElement("div", "02.02.2002");
    when(statusElement2.findElement(By.className("last-login"))).thenReturn(lastLogin2);
    WebElement name2 = mockWebElement("div", "katie");
    when(statusElement2.findElement(By.className("name"))).thenReturn(name2);

    Object decoratedField = pageFactory.decorate(cl, driver, null,
      getField("statusHistory"), By.cssSelector("table.history tr.status"));

    assertThat(decoratedField).isInstanceOf(List.class);
    List<StatusBlock> statusHistory = (List<StatusBlock>) decoratedField;
    assertThat(statusHistory).isNotNull();
    verify(webDriver, never()).findElements(By.cssSelector("table.history tr.status"));
    assertThat(statusHistory).hasSize(2);
    verify(webDriver).findElements(By.cssSelector("table.history tr.status"));

    checkItemInStatusHistory(statusElement1, statusHistory.get(0), "status element1 text", "01.01.2001", "john");
    checkItemInStatusHistory(statusElement2, statusHistory.get(1), "status element2 text", "02.02.2002", "katie");
  }

  private void checkItemInStatusHistory(WebElement statusElement, StatusBlock status, String text, String lastLogin, String name) {
    assertThat(status.getSelf().getText()).isEqualTo(text);

    status.lastLogin.shouldHave(text(lastLogin));
    verify(statusElement).findElement(By.className("last-login"));

    status.name.shouldHave(text(name));
    verify(statusElement).findElement(By.className("name"));
  }

  private Field getField(String fieldName) throws NoSuchFieldException {
    return getField(page, fieldName);
  }

  private <T> Field getField(T page, String fieldName) throws NoSuchFieldException {
    return page.getClass().getDeclaredField(fieldName);
  }

  @SuppressWarnings("unused")
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

  @SuppressWarnings("unused")
  static class TestPageWithElementsCollection {
    @FindBy(css = "table tbody tr")
    ElementsCollection rows;
  }
}
