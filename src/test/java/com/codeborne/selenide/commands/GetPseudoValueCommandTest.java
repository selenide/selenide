package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetPseudoValueCommandTest {

  private final Driver driver = mock();
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final WebElement element = mock();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
    when(locator.getWebElement()).thenReturn(element);
  }

  @Test
  void execute() {
    when(driver.executeJavaScript(any(), any(), any(), any())).thenReturn("hello");

    assertThat(new GetPseudoValue().execute(proxy, locator, new Object[]{":before", "content"}))
      .isEqualTo("hello");

    verify(driver).executeJavaScript(GetPseudoValue.JS_CODE, element, ":before", "content");
  }
}
