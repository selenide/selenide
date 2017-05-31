package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.TakesScreenshot;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class Helper {
  static void assertScreenshot(UIAssertionError expected) {
    if (WebDriverRunner.getWebDriver() instanceof TakesScreenshot) {
      assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
    }
  }
}
