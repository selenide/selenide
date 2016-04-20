package com.codeborne.selenide.impl;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ScreenShotLaboratoryTest {
  ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override protected long timestamp() {
      return 12356789L;
    }

    @Override
    public String takeScreenShot(String fileName) {
      addToHistory(new File(fileName));
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

  @Test
  public void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertEquals("ui/MyTest/test_some_method/12356789.0", screenshots.takeScreenShot());
    assertEquals("ui/MyTest/test_some_method/12356789.1", screenshots.takeScreenShot());
    assertEquals("ui/MyTest/test_some_method/12356789.2", screenshots.takeScreenShot());

    List<File> contextScreenshots = screenshots.finishContext();
    assertEquals(3, contextScreenshots.size());
    assertEquals(new File("ui/MyTest/test_some_method/12356789.0"), contextScreenshots.get(0));
    assertEquals(new File("ui/MyTest/test_some_method/12356789.1"), contextScreenshots.get(1));
    assertEquals(new File("ui/MyTest/test_some_method/12356789.2"), contextScreenshots.get(2));
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

    List<File> allScreenshots = screenshots.getScreenshots();
    assertEquals(5, allScreenshots.size());
    assertEquals(new File("ui/MyTest/test_some_method/12356789.0"), allScreenshots.get(0));
    assertEquals(new File("ui/MyTest/test_some_method/12356789.1"), allScreenshots.get(1));
    assertEquals(new File("ui/YourTest/test_another_method/12356789.2"), allScreenshots.get(2));
    assertEquals("12356789.3", allScreenshots.get(3).toString());
    assertEquals("12356789.4", allScreenshots.get(4).toString());
  }

  @Test
  public void canGetLastScreenshot() {
    assertNull(screenshots.getLastScreenshot());
    
    screenshots.takeScreenShot();
    assertEquals("12356789.0", screenshots.getLastScreenshot().toString());

    screenshots.takeScreenShot();
    assertEquals("12356789.1", screenshots.getLastScreenshot().toString());

    screenshots.takeScreenShot();
    assertEquals("12356789.2", screenshots.getLastScreenshot().toString());
  }
}
