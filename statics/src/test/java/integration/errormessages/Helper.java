package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import org.assertj.core.api.Condition;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public class Helper {
  private static final Logger log = LoggerFactory.getLogger(Helper.class);

  @Nonnull
  @CheckReturnValue
  static String getReportsFolder() {
    String folder = Configuration.headless ? Configuration.browser + "_headless" : Configuration.browser;
    return "statics/build/reports/tests/" + folder;
  }

  @Nonnull
  @CheckReturnValue
  static String path(String className, String methodName) {
    return "http://ci.org/" + Configuration.reportsFolder + "/integration/errormessages/" + className + "/" + methodName;
  }

  @Nonnull
  @CheckReturnValue
  static Condition<Throwable> screenshot() {
    return new Condition<>("Screenshot in folder " + Configuration.reportsFolder) {
      @Override
      public boolean matches(Throwable assertionError) {
        if (!(assertionError instanceof UIAssertionError)) return false;
        if (!(WebDriverRunner.getWebDriver() instanceof TakesScreenshot)) return true;

        String image = ((UIAssertionError) assertionError).getScreenshot().getImage();
        log.info("image: {}", image);
        log.info("reportsFolder: {}", Configuration.reportsFolder);
        log.info("reportsFolder.abs: {}", Paths.get(Configuration.reportsFolder).toAbsolutePath());
        try {
          log.info("reportsFolder.url: {}", Paths.get(Configuration.reportsFolder).toAbsolutePath().toUri().toURL().toExternalForm());

          return image != null && image.contains(
            Paths.get(Configuration.reportsFolder).toAbsolutePath().toUri().toURL().toExternalForm());
        }
        catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Nonnull
  @CheckReturnValue
  static Condition<Throwable> screenshot(String path) {
    return new Condition<>("Screenshot in sub-folder " + path) {
      @Override
      public boolean matches(Throwable assertionError) {
        if (!(assertionError instanceof UIAssertionError)) return false;
        if (!(WebDriverRunner.getWebDriver() instanceof TakesScreenshot)) return true;

        String image = ((UIAssertionError) assertionError).getScreenshot().getImage();
        return image != null &&
          image.startsWith(path) &&
          image.matches("http.+/\\d+\\.\\d+\\.(png|html)");
      }
    };
  }

  @Nonnull
  @CheckReturnValue
  static Condition<Throwable> webElementNotFound(String selector) {
    return new Condition<>("Selenium error message for missing element " + selector) {
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

  public static void verifyScreenshotSize(File screenshot, int width, int height) throws IOException {
    BufferedImage img = ImageIO.read(screenshot);
    log.info("Verify screenshot {} of size {}x{}", screenshot.getAbsolutePath(), img.getWidth(), img.getHeight());
    if (nearlyEqual(img.getWidth(), width * 2) && nearlyEqual(img.getHeight(), height * 2)) {
      // it's Retina display, it has 2x more pixels
      log.info("Screenshot matches {}x{} on Retina display", width, height);
    }
    else {
      assertThat(img.getWidth()).isBetween(width - 50, width + 50);
      assertThat(img.getHeight()).isBetween(height - 50, height + 50);
    }
  }

  private static boolean nearlyEqual(int actual, int expected) {
    return actual > expected - 50 && actual < expected + 50;
  }
}
