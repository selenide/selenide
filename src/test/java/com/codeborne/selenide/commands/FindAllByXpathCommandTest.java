package com.codeborne.selenide.commands;

import java.util.Collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllByXpathCommandTest implements WithAssertions {
  private SelenideElement parent;
  private WebElementSource locator;
  private SelenideElement element1;
  private FindAllByXpath findAllByXpathCommand;
  private String defaultText = "Default Text";

  @BeforeEach
  void setup() {
    findAllByXpathCommand = new FindAllByXpath();
    parent = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
    when(element1.getText()).thenReturn(defaultText);
    when(element1.isSelected()).thenReturn(true);
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
