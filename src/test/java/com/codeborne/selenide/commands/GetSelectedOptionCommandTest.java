package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSelectedOptionCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final String mockedElement1Text = "Element text2";
  private final GetSelectedOption getSelectedOptionCommand = new GetSelectedOption();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(new DriverStub());
    SelenideElement mockedElement = mock(SelenideElement.class);
    WebElement mockedElement1 = mock(WebElement.class);
    WebElement mockedElement2 = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);
    when(mockedElement.getTagName()).thenReturn("select");
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(asList(mockedElement1, mockedElement2));
    when(mockedElement1.isSelected()).thenReturn(true);
    when(mockedElement1.getText()).thenReturn(mockedElement1Text);
    when(mockedElement2.isSelected()).thenReturn(false);
  }

  @Test
  void testExecuteMethod() {
    SelenideElement selectedElement = getSelectedOptionCommand.execute(proxy, locator, new Object[]{"something more"});
    assertThat(selectedElement.getText())
      .isEqualTo(mockedElement1Text);
  }
}
