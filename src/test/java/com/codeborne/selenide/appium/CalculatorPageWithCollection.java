package com.codeborne.selenide.appium;

import com.codeborne.selenide.ElementsCollection;
import io.appium.java_client.pagefactory.AndroidFindBy;

import static com.codeborne.selenide.CollectionCondition.size;

class CalculatorPageWithCollection {
  @AndroidFindBy(id = "op_add")
  ElementsCollection plus;

  @AndroidFindBy(id = "eq")
  ElementsCollection equal;

  @AndroidFindBy(id = "digit_2")
  ElementsCollection number2;

  @AndroidFindBy(id = "digit_4")
  ElementsCollection number4;

  @AndroidFindBy(id = "formula")
  ElementsCollection result;

  public void selectDigit2() {
    number2.shouldHave(size(1)).get(0).click();
  }
}
