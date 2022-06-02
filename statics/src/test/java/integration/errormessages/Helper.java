package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import org.assertj.core.api.Condition;
import org.openqa.selenium.TakesScreenshot;

import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assertions.assertThat;

public class Helper {
  static void assertScreenshot(UIAssertionError expected) {
    if (WebDriverRunner.getWebDriver() instanceof TakesScreenshot) {
      assertThat(expected.getScreenshot().getImage())
        .contains(Configuration.reportsFolder);
    }
  }

  static Condition<Throwable> screenshot() {
    return new Condition<Throwable>("Screenshot in folder " + Configuration.reportsFolder) {
      @Override
      public boolean matches(Throwable assertionError) {
        if (!(assertionError instanceof UIAssertionError)) return false;
        if (!(WebDriverRunner.getWebDriver() instanceof TakesScreenshot)) return true;

        String image = ((UIAssertionError) assertionError).getScreenshot().getImage();
        return image != null && image.contains(Configuration.reportsFolder);
      }
    };
  }

  static Condition<Throwable> screenshot(String path) {
    return new Condition<Throwable>("Screenshot in sub-folder " + path) {
      @Override
      public boolean matches(Throwable assertionError) {
        if (!(assertionError instanceof UIAssertionError)) return false;
        if (!(WebDriverRunner.getWebDriver() instanceof TakesScreenshot)) return true;

        String image = ((UIAssertionError) assertionError).getScreenshot().getImage();
        return image != null &&
          image.startsWith("http://ci.org/build/reports/tests" + path) &&
          image.matches("http.+/\\d+\\.\\d+\\.(png|html)");
      }
    };
  }

  static Condition<Throwable> webElementNotFound(String selector) {
    return new Condition<Throwable>("Selenium error message for missing element " + selector) {
      @Override
      public boolean matches(Throwable assertionError) {
        if (isFirefox()) {
          return assertionError.getMessage().startsWith("Unable to locate element: " + selector);
        }
        else {
          return assertionError.getMessage()
            .startsWith("no such element: Unable to locate element: {\"method\":\"css selector\",\"selector\":\"" + selector + "\"}");
        }
      }
    };
  }
}
