package it.mobile.ios;

import org.junit.jupiter.api.Test;

/**
 * Inspired by <a href="https://applitools.com/blog/how-to-write-appium-ios-test/">...</a>
 * but migrated to Selenide.
 */
public class CalculatorTest extends BaseIosCalculatorTest {
  @Test
  void addNumbers() {
    CalculatorPage page = new CalculatorPage();

    page
      .enterTwoNumbersAndCompute("4", "6")
      .verifySum("10");

    page
      .enterTwoNumbersAndCompute("111111", "222222")
      .verifySum("333333");
  }
}

