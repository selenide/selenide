package com.codeborne.selenide.ex;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static com.codeborne.selenide.Screenshots.screenshots;
import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ErrorMessagesTest {

  private static String reportsUrl;

  @BeforeClass
  public static void rememberOldValues() {
    reportsUrl = Configuration.reportsUrl;
  }

  @AfterClass
  public static void restoreOldValues() {
    Configuration.reportsUrl = reportsUrl;
    screenshots = new ScreenShotLaboratory();
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
    screenshots = mock(ScreenShotLaboratory.class);
    doCallRealMethod().when(screenshots).formatScreenShotPath();
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

    screenshots = mock(ScreenShotLaboratory.class);
    doCallRealMethod().when(screenshots).formatScreenShotPath();
    doReturn(currentDir + "/test-result/12345.png").when(screenshots).takeScreenShot();

    String screenshot = ErrorMessages.screenshot();
    assertEquals("\nScreenshot: file:" + currentDir + "/test-result/12345.png", screenshot);
  }
}
