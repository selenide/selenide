package com.codeborne.selenide.impl;

import org.junit.Test;

import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;

public class ScreenShotLaboratoryTest {
  ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  @Test
  public void composesScreenshotNameFromTestClassAndMethod() {
    String expected = "MyTest/helloWorldTest".replace('/', separatorChar);
    assertEquals(expected, screenshots.getScreenshotFileName("MyTest", "helloWorldTest"));

    String expectedFileName = "com/codeborne/selenide/integrationtests/SelenideMethods/userCanListMatchingSubElements".replace('/', separatorChar);
    assertEquals(expectedFileName,
        screenshots.getScreenshotFileName("com.codeborne.selenide.integrationtests.SelenideMethods",
            "userCanListMatchingSubElements"));
  }
}
