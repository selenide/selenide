package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.io.File.separatorChar;
import static org.apache.commons.io.IOUtils.resourceToByteArray;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.openqa.selenium.OutputType.BYTES;

final class ScreenShotLaboratoryTest implements WithAssertions {
  private final ChromeDriver webDriver = mock(ChromeDriver.class);
  private final SelenideConfig config = new SelenideConfig().savePageSource(false);
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);

  private final ScreenShotLaboratory screenshots = new ScreenShotLaboratory() {
    @Override
    public String takeScreenShot(@Nonnull Driver driver, @Nonnull String fileName) {
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
    assertThat(screenshots.takeScreenShot(null, "MyTest", "helloWorldTest"))
      .isEqualTo(expected);

    String expectedFileName = ("com/codeborne/selenide/integrationtests/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.12356789").replace('/', separatorChar);
    assertThat(screenshots.takeScreenShot(
      null,
      "com.codeborne.selenide.integrationtests.SelenideMethodsTest",
      "userCanListMatchingSubElements"))
      .isEqualTo(expectedFileName);
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("12356789.0");
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("12356789.1");
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("12356789.2");
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("ui/MyTest/test_some_method/12356789.0");
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("ui/MyTest/test_some_method/12356789.1");
    assertThat(screenshots.takeScreenShot(null))
      .isEqualTo("ui/MyTest/test_some_method/12356789.2");

    List<File> contextScreenshots = screenshots.finishContext();
    assertThat(contextScreenshots)
      .hasSize(3);
    assertThat(contextScreenshots.get(0))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.0"));
    assertThat(contextScreenshots.get(1))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.1"));
    assertThat(contextScreenshots.get(2))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.2"));
  }

  @Test
  void collectsAllScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot(null);
    screenshots.finishContext();
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);

    List<File> allScreenshots = screenshots.getScreenshots();
    assertThat(allScreenshots)
      .hasSize(5);
    assertThat(allScreenshots.get(0))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.0"));
    assertThat(allScreenshots.get(1))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.1"));
    assertThat(allScreenshots.get(2))
      .isEqualTo(new File("ui/YourTest/test_another_method/12356789.2"));
    assertThat(allScreenshots.get(3))
      .hasToString("12356789.3");
    assertThat(allScreenshots.get(4))
      .hasToString("12356789.4");
  }

  @Test
  void collectsAllThreadScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot(null);
    screenshots.finishContext();
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);

    List<File> allThreadScreenshots = screenshots.getThreadScreenshots();
    assertThat(allThreadScreenshots)
      .hasSize(5);
    assertThat(allThreadScreenshots.get(0))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.0"));
    assertThat(allThreadScreenshots.get(1))
      .isEqualTo(new File("ui/MyTest/test_some_method/12356789.1"));
    assertThat(allThreadScreenshots.get(2))
      .isEqualTo(new File("ui/YourTest/test_another_method/12356789.2"));
    assertThat(allThreadScreenshots.get(3))
      .hasToString("12356789.3");
    assertThat(allThreadScreenshots.get(4))
      .hasToString("12356789.4");
  }

  @Test
  void collectsContextScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);
    screenshots.takeScreenShot(null);

    List<File> contextScreenshots = screenshots.getContextScreenshots();
    assertThat(contextScreenshots)
      .hasSize(3);

    screenshots.finishContext();
    assertThat(contextScreenshots)
      .hasSize(3);
    assertThat(screenshots.getContextScreenshots())
      .isEmpty();
  }

  @Test
  void canGetLastScreenshot() {
    assertThat(screenshots.getLastScreenshot())
      .isNull();

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.0");

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.1");

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("12356789.2");
  }

  @Test
  void canGetLastThreadScreenshot() {
    assertThat(screenshots.getLastThreadScreenshot())
      .isEmpty();

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("12356789.0"));

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("12356789.1"));

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("12356789.2"));
  }

  @Test
  void canGetLastContextScreenshot() {
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();

    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();
    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("ui/MyTest/test_some_method/12356789.0"));

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("ui/MyTest/test_some_method/12356789.1"));

    screenshots.takeScreenShot(null);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("ui/MyTest/test_some_method/12356789.2"));
  }

  @Test
  void canFormatScreenShotPathWithSpaces() throws IOException {
    ScreenShotLaboratory screenshots = new ScreenShotLaboratory();
    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    config.reportsUrl("http://ci.org/job/123/artifact");
    config.reportsFolder("build/reports/path with spaces/");

    String screenShot = screenshots.formatScreenShotPath(driver);

    assertThat(screenShot)
      .contains("http://ci.org/job/123/artifact/build/reports/path%20with%20spaces/");
  }

  @Test
  void doNotEncodeReportsURL() throws IOException {
    ScreenShotLaboratory screenshots = new ScreenShotLaboratory();
    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    config.reportsUrl("http://ci.org/path%20with%spaces/");

    String screenShotPath = screenshots.formatScreenShotPath(driver);

    assertThat(screenShotPath)
      .contains("http://ci.org/path%20with%spaces/");
  }

  @Test
  void encodePath() {
    assertThat(screenshots.encodePath("/foo bar/boom room/pdf")).isEqualTo("/foo%20bar/boom%20room/pdf");
  }
}
