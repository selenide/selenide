package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllByXpathCommandTest implements WithAssertions {
  private SelenideElement parent = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement element1 = mock(SelenideElement.class);
  private FindAllByXpath findAllByXpathCommand = new FindAllByXpath();
  private String defaultText = "Default Text";

  @BeforeEach
  void setup() {
    when(element1.getText()).thenReturn(defaultText);
    when(element1.isSelected()).thenReturn(true);
    when(locator.driver()).thenReturn(new DriverStub());
  }

  @Test
  void testExecuteMethodWithNoArgsPassed() {
    assertThatThrownBy(() -> findAllByXpathCommand.execute(parent, locator))
      .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void testExecuteMethodWithZeroLengthArgs() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".");
    assertThat(findAllCommandCollection.first().getText())
      .isEqualTo(defaultText);
  }

  @Test
  void testExecuteMethodWithMoreThenOneArgsList() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".", "/..");
    assertThat(findAllCommandCollection.first().getText())
      .isEqualTo(defaultText);
  }
}
