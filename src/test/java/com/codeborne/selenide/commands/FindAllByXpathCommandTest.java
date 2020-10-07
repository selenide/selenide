package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class FindAllByXpathCommandTest implements WithAssertions {
  private final WebElement parentWebElement = mockWebElement("div", "I am parent");
  private final SelenideElement parentSelenideElement = mockElement("div", "I am parent");
  private final WebElementSource locator = mock(WebElementSource.class);
  private final WebElement webElement = mockWebElement("div", "Default Text");
  private final SelenideElement selenideElement = mockElement("div", "Default Text");
  private final FindAllByXpath findAllByXpathCommand = new FindAllByXpath();

  @BeforeEach
  void setup() {
    when(parentSelenideElement.toWebElement()).thenReturn(parentWebElement);
    when(selenideElement.toWebElement()).thenReturn(webElement);
    when(selenideElement.isSelected()).thenReturn(true);
    when(locator.driver()).thenReturn(new DriverStub());
  }

  @Test
  void executeMethodWithNoArgsPassed() {
    assertThatThrownBy(() -> findAllByXpathCommand.execute(parentSelenideElement, locator))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Missing arguments");
  }

  @Test
  void executeMethodWithZeroLengthArgs() {
    when(parentWebElement.findElement(any())).thenReturn(webElement);
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parentSelenideElement, locator, ".");

    assertThat(findAllCommandCollection.first().getText()).isEqualTo("Default Text");

    verify(parentWebElement).findElement(By.xpath("."));
    verify(parentSelenideElement, never()).findElements(any());
  }

  @Test
  void executeMethodWithMoreThenOneArgsList() {
    when(parentWebElement.findElement(any())).thenReturn(webElement);
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parentSelenideElement, locator, ".", "/..");

    assertThat(findAllCommandCollection.first().getText()).isEqualTo("Default Text");

    verify(parentWebElement).findElement(By.xpath("."));
    verify(parentSelenideElement, never()).findElements(any());
  }
}
