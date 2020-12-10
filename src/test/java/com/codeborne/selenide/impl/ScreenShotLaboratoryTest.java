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
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static java.io.File.separatorChar;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.resourceToByteArray;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.BYTES;

final class ScreenShotLaboratoryTest implements WithAssertions {
  private final String dir = System.getProperty("user.dir");
  private final String workingDirectory = new File(dir).toURI().toString().replaceAll("/$", "");
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
    String expected = "/build/reports/tests/MyTest/helloWorldTest.12356789.png";
    assertThat(screenshots.takeScreenShot(driver, "MyTest", "helloWorldTest").getImage())
      .isEqualTo(workingDirectory + expected);

    String expectedFileName = workingDirectory + "/build/reports/tests/org/selenide/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.12356789.png";
    assertThat(screenshots.takeScreenShot(
      driver,
      "org.selenide.SelenideMethodsTest",
      "userCanListMatchingSubElements").getImage())
      .isEqualTo(expectedFileName);
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.0.png");
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.1.png");
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/12356789.2.png");
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.0.png");
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.1.png");
    assertThat(screenshots.takeScreenshot(driver).getImage())
      .isEqualTo(workingDirectory + "/build/reports/tests/ui/MyTest/test_some_method/12356789.2.png");

    List<File> contextScreenshots = screenshots.finishContext();
    assertThat(contextScreenshots).hasSize(3);
    assertThat(contextScreenshots.get(0))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));
    assertThat(contextScreenshots.get(1))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));
    assertThat(contextScreenshots.get(2))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.2.png"));
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
    assertThat(allScreenshots).hasSize(5);
    assertThat(allScreenshots.get(0))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));
    assertThat(allScreenshots.get(1))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));
    assertThat(allScreenshots.get(2))
      .hasToString(dir + normalize("/build/reports/tests/ui/YourTest/test_another_method/12356789.2.png"));
    assertThat(allScreenshots.get(3))
      .hasToString(dir + normalize("/build/reports/tests/12356789.3.png"));
    assertThat(allScreenshots.get(4))
      .hasToString(dir + normalize("/build/reports/tests/12356789.4.png"));
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
    assertThat(allThreadScreenshots).hasSize(5);
    assertThat(allThreadScreenshots.get(0))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.0.png"));
    assertThat(allThreadScreenshots.get(1))
      .hasToString(dir + normalize("/build/reports/tests/ui/MyTest/test_some_method/12356789.1.png"));
    assertThat(allThreadScreenshots.get(2))
      .hasToString(dir + normalize("/build/reports/tests/ui/YourTest/test_another_method/12356789.2.png"));
    assertThat(allThreadScreenshots.get(3))
      .hasToString(dir + normalize("/build/reports/tests/12356789.3.png"));
    assertThat(allThreadScreenshots.get(4))
      .hasToString(dir + normalize("/build/reports/tests/12356789.4.png"));
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
    assertThat(contextScreenshots).hasSize(3);
    assertThat(screenshots.getContextScreenshots()).isEmpty();
  }

  @Test
  void canGetLastScreenshot() {
    assertThat(screenshots.getLastScreenshot()).isNull();

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize("/build/reports/tests/12356789.0.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize("/build/reports/tests/12356789.1.png"));

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize("/build/reports/tests/12356789.2.png"));
  }

  @Test
  void canGetLastThreadScreenshot() {
    assertThat(screenshots.getLastThreadScreenshot())
      .isEmpty();

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.0.png").getAbsoluteFile());

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.1.png").getAbsoluteFile());

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File("build/reports/tests/12356789.2.png").getAbsoluteFile());
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
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.0.png").getAbsoluteFile());

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.1.png").getAbsoluteFile());

    screenshots.takeScreenShot(driver);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File("build/reports/tests/ui/MyTest/test_some_method/12356789.2.png").getAbsoluteFile());
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

  @Test
  void convertsScreenshotFileNameToCIUrl() throws IOException {
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");
    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    String screenshot = screenshots.takeScreenshot(driver).summary();
    assertThat(screenshot)
      .startsWith(String.format("%nScreenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/"))
      .endsWith(".png");
  }

  @Test
  void convertsReportUrlForOutsideSavedScreenshot() throws IOException {
    String reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    config.reportsUrl(reportsUrl);

    // directory, that not in 'user.dir'
    config.reportsFolder(Files.createTempDirectory("artifacts-storage").toFile().getAbsolutePath());

    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    String screenshot = screenshots.takeScreenshot(driver).summary();
    assertThat(screenshot)
      .as("Concatenate reportUrl with screenshot file name if it saved outside of build/project home dir")
      .startsWith(String.format("%nScreenshot: %s", reportsUrl + new File(screenshot).getName()));
  }

  @Test
  void returnsScreenshotFileName() throws IOException {
    config.reportsUrl(null);
    String currentDir = System.getProperty("user.dir");
    if (separatorChar == '\\') {
      currentDir = '/' + currentDir.replace('\\', '/');
    }

    currentDir = currentDir.replace(" ", "%20"); //the screenshot path uses %20 instead of the space character

    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    String screenshot = screenshots.takeScreenshot(driver).summary();
    assertThat(screenshot)
      .startsWith(String.format("%nScreenshot: file:%s/build/reports/tests/", currentDir))
      .endsWith(".png");
  }

  @Test
  void doesNotAddScreenshot_if_screenshotsAreDisabled() {
    config.screenshots(false);

    String screenshot = screenshots.takeScreenshot(driver).summary();
    assertThat(screenshot).isNullOrEmpty();
    verify(webDriver, never()).getScreenshotAs(any());
  }

  @Test
  void printHtmlPath_if_savePageSourceIsEnabled() throws IOException {
    config.savePageSource(true);
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");
    doReturn(new File("build/reports/page123.html")).when(extractor).extract(eq(config), eq(webDriver), any());
    doReturn(resourceToByteArray("/screenshot.png")).when(webDriver).getScreenshotAs(BYTES);

    Screenshot screenshot = screenshots.takeScreenshot(driver);
    assertThat(screenshot.summary()).isEqualTo(
      lineSeparator() + "Screenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/12356789.0.png" +
        lineSeparator() + "Page source: http://ci.mycompany.com/job/666/artifact/build/reports/page123.html");
  }

  private String normalize(String path) {
    return separatorChar == '\\' ? path.replace('/', separatorChar) : path;
  }
}
