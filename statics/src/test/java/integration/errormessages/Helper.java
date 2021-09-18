package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.TakesScreenshot;

import static org.assertj.core.api.Assertions.assertThat;

public class Helper {
  public static void assertScreenshot(UIAssertionError expected) {
    if (WebDriverRunner.getWebDriver() instanceof TakesScreenshot) {
      assertThat(expected.getScreenshot().getImage())
        .contains(Configuration.reportsFolder);
    }
  }
}
