package com.codeborne.selenide.ex;

import java.util.Locale;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Screenshots.screenshots;
import static java.io.File.separatorChar;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ErrorMessagesTest {

  private static String reportsUrl;

  @BeforeAll
  static void rememberOldValues() {
    reportsUrl = Configuration.reportsUrl;
  }

  @AfterAll
  static void restoreOldValues() {
    Configuration.screenshots = true;
    Configuration.savePageSource = true;
    Configuration.reportsUrl = reportsUrl;
    screenshots = new ScreenShotLaboratory();
  }

  @BeforeEach
  void setUp() {
    Configuration.screenshots = true;
    screenshots = mock(ScreenShotLaboratory.class);
    doCallRealMethod().when(screenshots).formatScreenShotPath();
    Configuration.savePageSource = false;
  }

  @Test
  void formatsTimeoutToReadable() {
    Locale.setDefault(Locale.UK);
    Assertions.assertEquals("\nTimeout: 0 ms.", ErrorMessages.timeout(0));
    Assertions.assertEquals("\nTimeout: 1 ms.", ErrorMessages.timeout(1));
    Assertions.assertEquals("\nTimeout: 999 ms.", ErrorMessages.timeout(999));
    Assertions.assertEquals("\nTimeout: 1 s.", ErrorMessages.timeout(1000));
    Assertions.assertEquals("\nTimeout: 1.001 s.", ErrorMessages.timeout(1001));
    Assertions.assertEquals("\nTimeout: 1.500 s.", ErrorMessages.timeout(1500));
    Assertions.assertEquals("\nTimeout: 4 s.", ErrorMessages.timeout(4000));
  }

  @Test
  void convertsScreenshotFileNameToCIUrl() {
    Configuration.reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    String currentDir = System.getProperty("user.dir");
    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    Assertions.assertEquals("\nScreenshot: http://ci.mycompany.com/job/666/artifact/test-result/12345.png", screenshot);
  }

  @Test
  void returnsScreenshotFileName() {
    Configuration.reportsUrl = null;
    String currentDir = System.getProperty("user.dir");
    if (separatorChar == '\\') {
      currentDir = '/' + currentDir.replace('\\', '/');
    }

    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    Assertions.assertEquals("\nScreenshot: file:" + currentDir + "/test-result/12345.png", screenshot);
  }

  @Test
  void doesNotAddScreenshot_if_screenshotsAreDisabled() {
    Configuration.screenshots = false;

    String screenshot = ErrorMessages.screenshot();
    Assertions.assertEquals("", screenshot);
    verify(screenshots, never()).takeScreenShot();
  }

  @Test
  void printHtmlPath_if_savePageSourceIsEnabled() {
    Configuration.savePageSource = true;
    Configuration.reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    String currentDir = System.getProperty("user.dir");
    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    Assertions.assertEquals("\nScreenshot: http://ci.mycompany.com/job/666/artifact/test-result/12345.png"
      + "\nPage source: http://ci.mycompany.com/job/666/artifact/test-result/12345.html", screenshot);
  }
}
