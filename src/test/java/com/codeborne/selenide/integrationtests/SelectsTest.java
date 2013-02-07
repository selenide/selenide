package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.ShouldableWebElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.checked;
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
    ShouldableWebElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionByValue("myrambler.ru");

    select.getSelectedOption().shouldBe(checked);
    assertEquals("myrambler.ru", select.getSelectedValue());
    assertEquals("@myrambler.ru", select.getSelectedText());
  }

  @Test
  public void userCanSelectOptionByValueOldWay() {
    By select = By.xpath("//select[@name='domain']");
    selectOption(select, "myrambler.ru");
    assertEquals("myrambler.ru", getSelectedValue(select));
    assertEquals("@myrambler.ru", getSelectedText(select));
  }

  @Test
  public void userCanSelectOptionByText() {
    ShouldableWebElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOption("@мыло.ру");

    select.getSelectedOption().shouldBe(checked);
    assertEquals("мыло.ру", select.getSelectedValue());
    assertEquals("@мыло.ру", select.getSelectedText());
  }

  @Test
  public void userCanSelectOptionByTextOldWay() {
    By select = By.xpath("//select[@name='domain']");
    selectOptionByText(select, "@мыло.ру");
    assertEquals("мыло.ру", getSelectedValue(select));
    assertEquals("@мыло.ру", getSelectedText(select));
  }
}
