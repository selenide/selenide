package com.codeborne.selenide.appium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.appium.ScreenObject.screen;

public class CalculatorTest extends BaseTest {
  @Test
  public void plain() {
    $(By.id("digit_2")).click();
    $(By.id("op_add")).click();
    $(By.id("digit_4")).click();
    $(By.id("eq")).click();
    $(By.id("result")).shouldHave(text("6"));
  }

  @Test
  public void pageObject() {
    CalculatorPage calculatorPage = screen(CalculatorPage.class);
    calculatorPage.number2.click();
    calculatorPage.plus.click();
    calculatorPage.number4.click();
    calculatorPage.equal.click();
    calculatorPage.result.shouldHave(text("6"));
  }

  @Test
  public void pageObjectWithCollection() {
    CalculatorPageWithCollection calculatorPage = screen(CalculatorPageWithCollection.class);
    calculatorPage.selectDigit2();
    calculatorPage.plus.shouldHave(size(1)).get(0).click();
    calculatorPage.number4.shouldHave(size(1)).get(0).click();
    calculatorPage.equal.shouldHave(size(1)).get(0).click();
    calculatorPage.result.shouldHave(size(1)).get(0).shouldHave(text("6"));
  }
}
