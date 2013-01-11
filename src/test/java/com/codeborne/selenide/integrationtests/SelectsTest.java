package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.DOM.*;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;

public class SelectsTest {
  @Before
  public void openTestPage() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void userCanSelectOptionByValue() {
    $(By.xpath("//select[@name='domain']")).selectOptionByValue("myrambler.ru");
    assertEquals("myrambler.ru", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@myrambler.ru", getSelectedText(By.xpath("//select[@name='domain']")));
  }

  @Test
  public void userCanSelectOptionByValueOldWay() {
    selectOption(By.xpath("//select[@name='domain']"), "myrambler.ru");
    assertEquals("myrambler.ru", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@myrambler.ru", getSelectedText(By.xpath("//select[@name='domain']")));
  }

  @Test
  public void userCanSelectOptionByText() {
    $(By.xpath("//select[@name='domain']")).selectOption("@мыло.ру");
    assertEquals("мыло.ру", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@мыло.ру", getSelectedText(By.xpath("//select[@name='domain']")));
  }

  @Test
  public void userCanSelectOptionByTextOldWay() {
    selectOptionByText(By.xpath("//select[@name='domain']"), "@мыло.ру");
    assertEquals("мыло.ру", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@мыло.ру", getSelectedText(By.xpath("//select[@name='domain']")));
  }
}
