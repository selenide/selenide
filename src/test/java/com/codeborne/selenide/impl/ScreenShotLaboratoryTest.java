package com.codeborne.selenide.impl;

import org.junit.Test;

import java.util.List;

import static java.io.File.separatorChar;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ScreenShotLaboratoryTest {
  ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override protected long timestamp() {
      return 12356789L;
    }

    @Override
    public String takeScreenShot(String fileName) {
      return addToHistory(fileName);
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

  @Test
  public void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertEquals("ui/MyTest/test_some_method/12356789.0", screenshots.takeScreenShot());
    assertEquals("ui/MyTest/test_some_method/12356789.1", screenshots.takeScreenShot());
    assertEquals("ui/MyTest/test_some_method/12356789.2", screenshots.takeScreenShot());

    List<String> contextScreenshots = screenshots.finishContext();
    assertEquals(asList(
        "ui/MyTest/test_some_method/12356789.0",
        "ui/MyTest/test_some_method/12356789.1",
        "ui/MyTest/test_some_method/12356789.2"
    ), contextScreenshots);
  }

  @Test
  public void collectsAllScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot();
    screenshots.takeScreenShot();
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot();
    screenshots.finishContext();
    screenshots.takeScreenShot();
    screenshots.takeScreenShot();

    assertEquals(asList(
        "ui/MyTest/test_some_method/12356789.0",
        "ui/MyTest/test_some_method/12356789.1",
        "ui/YourTest/test_another_method/12356789.2",
        "12356789.3",
        "12356789.4"
    ), screenshots.getScreenshots());
  }
}
