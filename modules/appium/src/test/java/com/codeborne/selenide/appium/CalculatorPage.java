package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

class CalculatorPage {
  @AndroidFindBy(id = "op_add")
  SelenideElement plus;

  @AndroidFindBy(id = "eq")
  SelenideElement equal;

  @AndroidFindBy(id = "digit_2")
  WebElement number2;

  @AndroidFindBy(id = "digit_4")
  SelenideElement number4;

  @AndroidFindBy(id = "formula")
  SelenideElement result;
}
