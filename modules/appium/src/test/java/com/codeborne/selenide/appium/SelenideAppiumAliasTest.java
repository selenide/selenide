package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

class SelenideAppiumAliasTest {

  @BeforeEach
  void setUp() {
    AndroidDriver androidDriver = mock();
    when(androidDriver.getCapabilities()).thenReturn(new DesiredCapabilities());
    when(androidDriver.getBiDi()).thenThrow(BiDiException.class);
    WebDriverRunner.setWebDriver(androidDriver);
  }

  @AfterEach
  void tearDown() {
    Selenide.closeWebDriver();
  }

  @Test
  void element_is_alias_for_dollar() {
    By selector = By.id("submit");
    assertThat(SelenideAppium.element(selector))
      .hasToString(SelenideAppium.$(selector).toString());
  }

  @Test
  void element_with_index_is_alias_for_dollar() {
    By selector = By.className("android.widget.TextView");
    assertThat(SelenideAppium.element(selector, 2))
      .hasToString(SelenideAppium.$(selector, 2).toString());
  }

  @Test
  void elements_is_alias_for_dollar_dollar() {
    By selector = By.className("android.widget.TextView");
    assertThat(SelenideAppium.elements(selector))
      .hasToString(SelenideAppium.$$(selector).toString());
  }

  @Test
  void element_from_webElement_is_alias_for_dollar() {
    WebElement raw = mock();
    assertThat(SelenideAppium.element(raw))
      .hasToString(SelenideAppium.$(raw).toString());
  }

  @Test
  void elements_from_collection_is_alias_for_dollar_dollar() {
    List<WebElement> raw = List.of(mock(), mock());
    assertThat(SelenideAppium.elements(raw))
      .hasToString(SelenideAppium.$$(raw).toString());
  }
}
