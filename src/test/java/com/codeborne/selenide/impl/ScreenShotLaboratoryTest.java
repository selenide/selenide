package com.codeborne.selenide.impl;

import java.io.File;
import java.util.List;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

import static java.io.File.separatorChar;

class ScreenShotLaboratoryTest extends UnitTest {
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
    assertThat(screenshots.takeScreenShot("MyTest", "helloWorldTest"))
      .isEqualTo(expected);

    String expectedFileName = ("com/codeborne/selenide/integrationtests/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.12356789").replace('/', separatorChar);
    assertThat(screenshots.takeScreenShot(
      "com.codeborne.selenide.integrationtests.SelenideMethodsTest",
      "userCanListMatchingSubElements"))
      .isEqualTo(expectedFileName);
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("12356789.0");
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("12356789.1");
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("12356789.2");
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("ui/MyTest/test_some_method/12356789.0");
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("ui/MyTest/test_some_method/12356789.1");
    assertThat(screenshots.takeScreenShot())
      .isEqualTo("ui/MyTest/test_some_method/12356789.2");

    List<File> contextScreenshots = screenshots.finishContext();
    assertThat(contextScreenshots)
      .hasSize(3);
    assertThat(contextScreenshots.get(0))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.0"));
    assertThat(contextScreenshots.get(1))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.1"));
    assertThat(contextScreenshots.get(2))
      .isEqualTo((new File("ui/MyTest/test_some_method/12356789.2")));
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
    assertThat(allScreenshots)
      .hasSize(5);
    assertThat(allScreenshots.get(0))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.0"));
    assertThat(allScreenshots.get(1))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.1"));
    assertThat(allScreenshots.get(2))
      .isEqualTo((new File("ui/YourTest/test_another_method/12356789.2")));
    assertThat(allScreenshots.get(3))
      .hasToString("12356789.3");
    assertThat(allScreenshots.get(4))
      .hasToString("12356789.4");
  }

  @Test
  void canGetLastScreenshot() {
    assertThat(screenshots.getLastScreenshot())
      .isNull();

    screenshots.takeScreenShot();
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.0");

    screenshots.takeScreenShot();
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.1");

    screenshots.takeScreenShot();
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.2");
  }
}
