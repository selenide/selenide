package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Selenide.$;

final class SpeedOfSetValueTest extends IntegrationTest {
  private long start;

  @BeforeEach
  void openTestPageWithJQuery() {
    Configuration.fastSetValue = false;
    openFile("page_with_100_inputs.html");
    start = System.currentTimeMillis();
  }

  @AfterEach
  void tearDown() {
    long end = System.currentTimeMillis();
    System.out.println("Test completed in " + (end - start) + " ms.");
  }

  @Test
  void speedOfDefaultClear() {
    for (int i = 0; i < 100; i++) {
      $("#username_" + i).clear();
    }
    $("#username_99").shouldHave(exactValue(""));
  }

  @Test
  void speedOfClearWithKeystrokes() {
    for (int i = 0; i < 100; i++) {
      clearValueWithKeystrokes($("#username_" + i));
    }
    $("#username_99").shouldHave(exactValue(""));
  }

  @Test
  void speedOfSlowSetValue() {
    Configuration.fastSetValue = false;
    fillValues();
  }

  @Test
  void speedOfFastSetValue() {
    Configuration.fastSetValue = true;
    fillValues();
  }

  private void fillValues() {
    for (int i = 0; i < 100; i++) {
      $("#username_" + i).setValue("tere-" + i);
    }
    $("#username_99").shouldHave(exactValue("tere-99"));
  }

  private void clearValueWithKeystrokes(WebElement element) {
    element.sendKeys(Keys.HOME);
    element.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
    element.sendKeys(Keys.chord(Keys.SHIFT, Keys.END));
    element.sendKeys(Keys.BACK_SPACE);
    element.sendKeys(Keys.BACK_SPACE);
  }
}
