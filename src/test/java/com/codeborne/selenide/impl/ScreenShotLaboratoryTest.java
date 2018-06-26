package com.codeborne.selenide.impl;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.io.File.separatorChar;

class ScreenShotLaboratoryTest {
  private ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override
    public String takeScreenShot(String fileName) {
      addToHistory(new File(fileName));
      return fileName;
    }

    @Override
    protected long timestamp() {
      return 12356789L;
    }
  };

  @Test
  void composesScreenshotNameFromTestClassAndMethod() {
    String expected = "MyTest/helloWorldTest.12356789".replace('/', separatorChar);
    Assertions.assertEquals(expected, screenshots.takeScreenShot("MyTest", "helloWorldTest"));

    String expectedFileName = ("com/codeborne/selenide/integrationtests/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.12356789").replace('/', separatorChar);
    Assertions.assertEquals(expectedFileName,
      screenshots.takeScreenShot("com.codeborne.selenide." +
        "integrationtests.SelenideMethodsTest", "userCanListMatchingSubElements"));
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    Assertions.assertEquals("12356789.0", screenshots.takeScreenShot());
    Assertions.assertEquals("12356789.1", screenshots.takeScreenShot());
    Assertions.assertEquals("12356789.2", screenshots.takeScreenShot());
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    Assertions.assertEquals("ui/MyTest/test_some_method/12356789.0", screenshots.takeScreenShot());
    Assertions.assertEquals("ui/MyTest/test_some_method/12356789.1", screenshots.takeScreenShot());
    Assertions.assertEquals("ui/MyTest/test_some_method/12356789.2", screenshots.takeScreenShot());

    List<File> contextScreenshots = screenshots.finishContext();
    Assertions.assertEquals(3, contextScreenshots.size());
    Assertions.assertEquals(new File("ui/MyTest/test_some_method/12356789.0"), contextScreenshots.get(0));
    Assertions.assertEquals(new File("ui/MyTest/test_some_method/12356789.1"), contextScreenshots.get(1));
    Assertions.assertEquals(new File("ui/MyTest/test_some_method/12356789.2"), contextScreenshots.get(2));
  }

  @Test
  void collectsAllScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot();
    screenshots.takeScreenShot();
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot();
    screenshots.finishContext();
    screenshots.takeScreenShot();
    screenshots.takeScreenShot();

    List<File> allScreenshots = screenshots.getScreenshots();
    Assertions.assertEquals(5, allScreenshots.size());
    Assertions.assertEquals(new File("ui/MyTest/test_some_method/12356789.0"), allScreenshots.get(0));
    Assertions.assertEquals(new File("ui/MyTest/test_some_method/12356789.1"), allScreenshots.get(1));
    Assertions.assertEquals(new File("ui/YourTest/test_another_method/12356789.2"), allScreenshots.get(2));
    Assertions.assertEquals("12356789.3", allScreenshots.get(3).toString());
    Assertions.assertEquals("12356789.4", allScreenshots.get(4).toString());
  }

  @Test
  void canGetLastScreenshot() {
    Assertions.assertNull(screenshots.getLastScreenshot());

    screenshots.takeScreenShot();
    Assertions.assertEquals("12356789.0", screenshots.getLastScreenshot().toString());

    screenshots.takeScreenShot();
    Assertions.assertEquals("12356789.1", screenshots.getLastScreenshot().toString());

    screenshots.takeScreenShot();
    Assertions.assertEquals("12356789.2", screenshots.getLastScreenshot().toString());
  }
}
