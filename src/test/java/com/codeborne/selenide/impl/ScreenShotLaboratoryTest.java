package com.codeborne.selenide.impl;

import org.junit.Test;

import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;

public class ScreenShotLaboratoryTest {
  ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override protected long timestamp() {
      return 12356789L;
    }

    @Override
    public String takeScreenShot(String fileName) {
      return fileName;
    }
  };

  @Test
  public void composesScreenshotNameFromTestClassAndMethod() {
    String expected = "MyTest/helloWorldTest.12356789".replace('/', separatorChar);
    assertEquals(expected, screenshots.takeScreenShot("MyTest", "helloWorldTest"));

    String expectedFileName = ("com/codeborne/selenide/integrationtests/SelenideMethodsTest/" +
        "userCanListMatchingSubElements.12356789").replace('/', separatorChar);
    assertEquals(expectedFileName,
        screenshots.takeScreenShot("com.codeborne.selenide." +
            "integrationtests.SelenideMethodsTest", "userCanListMatchingSubElements"));
  }

  @Test
  public void composesScreenshotNameAsTimestampPlusCounter() {
    assertEquals("12356789.0", screenshots.takeScreenShot());
    assertEquals("12356789.1", screenshots.takeScreenShot());
    assertEquals("12356789.2", screenshots.takeScreenShot());
  }
}
