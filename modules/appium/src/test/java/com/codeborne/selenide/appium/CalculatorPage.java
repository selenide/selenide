package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.support.FindBy;

public class CalculatorPage {
  @FindBy(id = "com.android.calculator2:id/plus")
  SelenideElement plus;

  @FindBy(id = "com.android.calculator2:id/equal")
  SelenideElement equal;

  @FindBy(id = "com.android.calculator2:id/digit2")
  SelenideElement number2;

  @FindBy(id = "com.android.calculator2:id/digit4")
  SelenideElement number4;

  @AndroidFindBy(className = "android.widget.EditText")
  SelenideElement result;
}
