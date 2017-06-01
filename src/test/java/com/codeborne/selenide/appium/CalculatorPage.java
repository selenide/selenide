package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.support.FindBy;

class CalculatorPage {
  @FindBy(id = "op_add")
  SelenideElement plus;

  @FindBy(id = "eq")
  SelenideElement equal;

  @FindBy(id = "digit_2")
  SelenideElement number2;

  @FindBy(id = "digit_4")
  SelenideElement number4;

  @AndroidFindBy(id = "formula")
  SelenideElement result;
}
