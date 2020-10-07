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

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSelectedOptionsCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final GetSelectedOptions getSelectedOptionsCommand = new GetSelectedOptions();
  private List<WebElement> mMockedElementsList;

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(new DriverStub());
    SelenideElement mockedElement = mock(SelenideElement.class);
    WebElement mockedElement1 = mock(WebElement.class);
    WebElement mockedElement2 = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);
    when(mockedElement.getTagName()).thenReturn("select");
    mMockedElementsList = asList(mockedElement1, mockedElement2);
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(mMockedElementsList);
    when(mockedElement1.isSelected()).thenReturn(true);
    when(mockedElement1.getText()).thenReturn("Element text1");
    when(mockedElement2.isSelected()).thenReturn(true);
    when(mockedElement2.getText()).thenReturn("Element text2");
  }

  @Test
  void testExecuteMethod() {
    ElementsCollection elementsCollection = getSelectedOptionsCommand.execute(proxy, locator, new Object[]{"something more"});
    final List<String> foundTexts = elementsCollection.stream().map(SelenideElement::getText).collect(Collectors.toList());

    assertThat(mMockedElementsList)
      .extracting(WebElement::getText)
      .isEqualTo(foundTexts);
  }
}
