package integration.ios;

import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Inspired by <a href="https://applitools.com/blog/how-to-write-appium-ios-test/">...</a>
 * but migrated to Selenide.
 */
@ExtendWith(TextReportExtension.class)
public class CalculatorTest extends BaseIOSTest {
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

