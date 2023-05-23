package it.mobile.ios;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;

public class CalculatorPage {
  private final By firstNumber = By.name("IntegerA");
  private final By secondNumber = By.name("IntegerB");
  private final By computeSumButton = By.name("ComputeSumButton");
  private final By answer = By.name("Answer");

  public CalculatorPage enterTwoNumbersAndCompute(String first, String second) {
    typeFirstNumber(first);
    typeSecondNumber(second);
    compute();
    return this;
  }

  public CalculatorPage typeFirstNumber(String number) {
    SelenideElement firstNoElement = $(firstNumber);
    firstNoElement.setValue(number);
    return this;
  }

  public CalculatorPage typeSecondNumber(String number) {
    $(secondNumber).setValue(number);
    return this;
  }

  public CalculatorPage compute() {
    $(computeSumButton).click();
    return this;
  }

  public void verifySum(String expectedSum) {
    $(answer)
      .shouldBe(visible)
      .shouldHave(exactText(expectedSum));
  }
}
