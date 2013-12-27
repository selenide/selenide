package com.codeborne.selenide.ex;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ErrorMessagesTest {
  @Test
  public void formatsTimeoutToReadable() {
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
    WebDriverRunner.screenshots = mock(ScreenShotLaboratory.class);
    doReturn(currentDir + "/test-result/12345.png").when(WebDriverRunner.screenshots).takeScreenShot();

    assertEquals("\nScreenshot: http://ci.mycompany.com/job/666/artifact/test-result/12345.png",
        ErrorMessages.screenshot());
  }
}
