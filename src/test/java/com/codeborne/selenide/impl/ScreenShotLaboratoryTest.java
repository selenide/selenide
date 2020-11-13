package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.resourceToByteArray;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.BYTES;

final class ScreenShotLaboratoryTest implements WithAssertions {
  private final String workingDirectory = System.getProperty("user.dir");
  private final ChromeDriver webDriver = mock(ChromeDriver.class);
  private final SelenideConfig config = new SelenideConfig().savePageSource(false).reportsFolder("build/reports/tests");
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);
  private final Photographer photographer = mock(Photographer.class);
  private final PageSourceExtractor extractor = mock(PageSourceExtractor.class);
  private final Clock clock = new DummyClock(12356789L);
  private final ScreenShotLaboratory screenshots = new ScreenShotLaboratory(photographer, extractor, clock);

  @BeforeEach
  void setUp() {
    when(photographer.takeScreenshot(any(), eq(BYTES))).thenReturn(Optional.of("siski".getBytes(UTF_8)));
  }

  @Test
  void composesScreenshotNameFromTestClassAndMethod() {
    String expected = "/build/reports/tests/MyTest/helloWorldTest.12356789.png".replace('/', separatorChar);
    assertThat(screenshots.takeScreenShot(driver, "MyTest", "helloWorldTest"))
      .isEqualTo(workingDirectory + expected);

    String expectedFileName = (workingDirectory + "/build/reports/tests/org/selenide/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.12356789.png").replace('/', separatorChar);
    assertThat(screenshots.takeScreenShot(
      driver,
      "org.selenide.SelenideMethodsTest",
      "userCanListMatchingSubElements"))
      .isEqualTo(expectedFileName);
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.0.png");
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.1.png");
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.2.png");
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.0.png");
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.1.png");
    assertThat(screenshots.takeScreenShot(driver))
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.2.png");

    List<File> contextScreenshots = screenshots.finishContext();
    assertThat(contextScreenshots)
      .hasSize(3);
    assertThat(contextScreenshots.get(0))
      .isEqualTo(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));
    assertThat(contextScreenshots.get(1))
      .isEqualTo(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));
    assertThat(contextScreenshots.get(2))
      .isEqualTo(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.2.png"));
  }

  @Test
  void collectsAllScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot(driver);
    screenshots.finishContext();
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);

    List<File> allScreenshots = screenshots.getScreenshots();
    assertThat(allScreenshots)
      .hasSize(5);
    assertThat(allScreenshots.get(0))
      .isEqualTo(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));
    assertThat(allScreenshots.get(1))
      .isEqualTo(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));
    assertThat(allScreenshots.get(2))
      .isEqualTo(new File("build/reports/tests/ui/YourTest/test_another_method/12356789.2.png"));
    assertThat(allScreenshots.get(3))
      .hasToString("build/reports/tests/12356789.3.png");
    assertThat(allScreenshots.get(4))
      .hasToString("build/reports/tests/12356789.4.png");
  }

  @Test
  void collectsAllThreadScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenShot(driver);
    screenshots.finishContext();
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);

    List<File> allThreadScreenshots = screenshots.getThreadScreenshots();
    assertThat(allThreadScreenshots)
      .hasSize(5);
    assertThat(allThreadScreenshots.get(0))
      .hasToString("build/reports/tests/ui/MyTest/test_some_method/12356789.0.png");
    assertThat(allThreadScreenshots.get(1))
      .hasToString("build/reports/tests/ui/MyTest/test_some_method/12356789.1.png");
    assertThat(allThreadScreenshots.get(2))
      .hasToString("build/reports/tests/ui/YourTest/test_another_method/12356789.2.png");
    assertThat(allThreadScreenshots.get(3))
      .hasToString("build/reports/tests/12356789.3.png");
    assertThat(allThreadScreenshots.get(4))
      .hasToString("build/reports/tests/12356789.4.png");
  }

  @Test
  void collectsContextScreenshots() {
    screenshots.startContext("build/reports/tests/ui/MyTest/test_some_method/");
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);
    screenshots.takeScreenShot(driver);

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

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("build/reports/tests/12356789.0.png");

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("build/reports/tests/12356789.1.png");

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString("build/reports/tests/12356789.2.png");
  }

  @Test
  void canGetLastThreadScreenshot() {
    assertThat(screenshots.getLastThreadScreenshot())
      .isEmpty();

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.0.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.1.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.2.png"));
  }

  @Test
  void canGetLastContextScreenshot() {
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();

    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();
    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.2.png"));
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
