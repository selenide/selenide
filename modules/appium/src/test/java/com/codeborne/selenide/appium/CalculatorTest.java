package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class CalculatorTest {
  @Before
  public void setUp() {
    Configuration.browser = AndroidDriverProvider.class.getName();
  }

  @Test
  public void plain() {
    $(By.id("digit_2")).click();
    $(By.id("op_add")).click();
    $(By.id("digit_4")).click();
    $(By.id("eq")).click();
    $(By.id("formula")).shouldHave(text("6"));
  }

  @Test
  public void pageObject() {
    CalculatorPage page = new CalculatorPage();
    PageFactory.initElements(new SelenideAppiumFieldDecorator(getWebDriver()), page);
    page.number2.click();
    page.plus.click();
    page.number4.click();
    page.equal.click();
    page.result.shouldHave(text("6"));
  }
}
