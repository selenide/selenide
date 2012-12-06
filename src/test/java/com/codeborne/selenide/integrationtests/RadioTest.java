package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.DOM.getSelectedRadio;
import static com.codeborne.selenide.DOM.selectRadio;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RadioTest {
  @Before
  public void openTestPage() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void userCanSelectRadioButtonByValue() {
    assertNull(getSelectedRadio(By.name("me")));

    selectRadio(By.name("me"), "ready");
    assertEquals("ready", getSelectedRadio(By.name("me")).getAttribute("value"));
//    assertEquals("Я готов", getSelectedRadio(By.name("me")).getText()); // Text is empty for unknown reason :(
  }
}
