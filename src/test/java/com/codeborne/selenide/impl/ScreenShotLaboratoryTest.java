package com.codeborne.selenide.impl;

import org.junit.Test;

import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;

public class ScreenShotLaboratoryTest {
  ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override protected long timestamp() {
      return 12356789L;
    }
  };

  @Test
  public void composesScreenshotNameFromTestClassAndMethod() {
    String expected = "MyTest/helloWorldTest.12356789".replace('/', separatorChar);
    assertEquals(expected, screenshots.getScreenshotFileName("MyTest", "helloWorldTest"));

    String expectedFileName = ("com/codeborne/selenide/integrationtests/SelenideMethodsTest/" +
        "userCanListMatchingSubElements.12356789").replace('/', separatorChar);
    assertEquals(expectedFileName,
        screenshots.getScreenshotFileName("com.codeborne.selenide." +
            "integrationtests.SelenideMethodsTest", "userCanListMatchingSubElements"));
  }
}
