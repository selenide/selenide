package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetPseudoValueCommandTest implements WithAssertions {

  private static final String PSEUDO_ELEMENT_NAME = ":before";
  private static final String PROPERTY_NAME = "content";
  private static final String ACTUAL_VALUE = "hello";

  private SelenideElement proxy;
  private WebElementSource locator;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    WebElement element = mock(WebElement.class);
    Driver driver = mock(Driver.class);

    when(locator.driver()).thenReturn(driver);
    when(locator.getWebElement()).thenReturn(element);
    when(driver.executeJavaScript(GetPseudoValue.JS_CODE, element, PSEUDO_ELEMENT_NAME, PROPERTY_NAME))
      .thenReturn(ACTUAL_VALUE);
  }

  @Test
  void testExecuteMethod() {
    assertThat(new GetPseudoValue().execute(proxy, locator, new Object[]{PSEUDO_ELEMENT_NAME, PROPERTY_NAME}))
      .isEqualTo(ACTUAL_VALUE);
  }
}
