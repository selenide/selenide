package com.codeborne.selenide.ex;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static com.codeborne.selenide.Screenshots.screenshots;
import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ErrorMessagesTest {

  private static String reportsUrl;

  @BeforeClass
  public static void rememberOldValues() {
    reportsUrl = Configuration.reportsUrl;
  }

  @AfterClass
  public static void restoreOldValues() {
    Configuration.screenshots = true;
    Configuration.savePageSource = true;
    Configuration.reportsUrl = reportsUrl;
    screenshots = new ScreenShotLaboratory();
  }

  @Before
  public void setUp() {
    Configuration.screenshots = true;
    screenshots = mock(ScreenShotLaboratory.class);
    doCallRealMethod().when(screenshots).formatScreenShotPath();
    Configuration.savePageSource = false;
  }

  @Test
  public void formatsTimeoutToReadable() {
    Locale.setDefault(Locale.UK);
    assertEquals("\nTimeout: 0 ms.", ErrorMessages.timeout(0));
    assertEquals("\nTimeout: 1 ms.", ErrorMessages.timeout(1));
    assertEquals("\nTimeout: 999 ms.", ErrorMessages.timeout(999));
    assertEquals("\nTimeout: 1 s.", ErrorMessages.timeout(1000));
    assertEquals("\nTimeout: 1.001 s.", ErrorMessages.timeout(1001));
    assertEquals("\nTimeout: 1.500 s.", ErrorMessages.timeout(1500));
    assertEquals("\nTimeout: 4 s.", ErrorMessages.timeout(4000));
  }

  @Test
  public void convertsScreenshotFileNameToCIUrl() {
    Configuration.reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    String currentDir = System.getProperty("user.dir");
    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    assertEquals("\nScreenshot: http://ci.mycompany.com/job/666/artifact/test-result/12345.png", screenshot);
  }

  @Test
  public void returnsScreenshotFileName() {
    Configuration.reportsUrl = null;
    String currentDir = System.getProperty("user.dir");
    if (separatorChar == '\\') {
      currentDir = '/' + currentDir.replace('\\', '/');
    }

    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    assertEquals("\nScreenshot: file:" + currentDir + "/test-result/12345.png", screenshot);
  }

  @Test
  public void doesNotAddScreenshot_if_screenshotsAreDisabled() {
    Configuration.screenshots = false;

    String screenshot = ErrorMessages.screenshot();
    assertEquals("", screenshot);
    verify(screenshots, never()).takeScreenShot();
  }

  @Test
  public void printHtmlPath_if_savePageSourceIsEnabled() {
    Configuration.savePageSource = true;
    Configuration.reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    String currentDir = System.getProperty("user.dir");
    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    assertEquals("\nScreenshot: http://ci.mycompany.com/job/666/artifact/test-result/12345.png"
                 + "\nPage source: http://ci.mycompany.com/job/666/artifact/test-result/12345.html", screenshot);
  }
}
