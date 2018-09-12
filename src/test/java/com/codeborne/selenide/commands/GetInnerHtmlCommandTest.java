package com.codeborne.selenide.commands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetInnerHtmlCommandTest implements WithAssertions {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetInnerHtml getInnerHtmlCommand = new GetInnerHtml();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void uses_innerHTML_attribute_if_not_htmlUnit() {
    when(locator.driver()).thenReturn(new DriverStub("firefox"));

    when(mockedElement.getAttribute("innerHTML")).thenReturn("hello");
    assertThat(getInnerHtmlCommand.execute(proxy, locator, null)).isEqualTo("hello");
  }

  @Test
  void callsJavaScript_innerHTML_attribute_if_htmlUnit() {
    Driver driver = mock(Driver.class);
    when(driver.browser()).thenReturn(new Browser("htmlunit", false), null, null);
    when(driver.executeJavaScript(anyString(), any())).thenReturn("hello");
    when(locator.driver()).thenReturn(driver);
    when(mockedElement.getAttribute("innerHTML")).thenReturn("not supported");

    assertThat(getInnerHtmlCommand.execute(proxy, locator, null)).isEqualTo("hello");

    verify(driver).executeJavaScript("return arguments[0].innerHTML", mockedElement);
  }
}
