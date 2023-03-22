package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.BYTES;

final class ScreenShotLaboratoryTest {
  private final String dir = System.getProperty("user.dir");
  private final String workingDirectory = new File(dir).toURI().toString().replaceAll("/$", "");
  private final ChromeDriver webDriver = mock();
  private final SelenideConfig config = new SelenideConfig().savePageSource(false).reportsFolder("build/reports/tests");
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);
  private final Photographer photographer = mock();
  private final PageSourceExtractor extractor = mock();
  private final long ts = System.currentTimeMillis();
  private final Clock clock = new DummyClock(ts);
  private final ScreenShotLaboratory screenshots = new ScreenShotLaboratory(photographer, extractor, clock);

  @BeforeEach
  void setUp() {
    when(photographer.takeScreenshot(any(), eq(BYTES))).thenAnswer((Answer<Optional<byte[]>>) invocation -> {
      return Optional.of("some png source".getBytes(UTF_8));
    });
  }

  @Test
  void composesScreenshotNameFromTestClassAndMethod() {
    String expected = String.format("/build/reports/tests/MyTest/helloWorldTest.%s.png", ts);
    assertThat(screenshots.takeScreenShot(driver, "MyTest", "helloWorldTest").getImage())
      .isEqualTo(workingDirectory + expected);

    String expectedFileName = String.format("%s/build/reports/tests/org/selenide/SelenideMethodsTest/" +
      "userCanListMatchingSubElements.%s.png", workingDirectory, ts);
    assertThat(screenshots.takeScreenShot(
      driver,
      "org.selenide.SelenideMethodsTest",
      "userCanListMatchingSubElements").getImage())
      .isEqualTo(expectedFileName);
  }

  @Test
  void composesScreenshotNameAsTimestampPlusCounter() {
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/%s.0.png", workingDirectory, ts));
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/%s.1.png", workingDirectory, ts));
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/%s.2.png", workingDirectory, ts));
  }

  @Test
  void screenshotsCanByGroupedByTests() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/ui/MyTest/test_some_method/%s.0.png", workingDirectory, ts));
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/ui/MyTest/test_some_method/%s.1.png", workingDirectory, ts));
    assertThat(screenshots.takeScreenshot(driver, true, false).getImage())
      .isEqualTo(String.format("%s/build/reports/tests/ui/MyTest/test_some_method/%s.2.png", workingDirectory, ts));

    List<Screenshot> contextScreenshots = screenshots.finishContext();
    assertThat(contextScreenshots).hasSize(3);
    assertThat(contextScreenshots.get(0).getImageFile())
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.0.png", ts)));
    assertThat(contextScreenshots.get(1).getImageFile())
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.1.png", ts)));
    assertThat(contextScreenshots.get(2).getImageFile())
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.2.png", ts)));
  }

  @Test
  void collectsAllScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenshot(driver, true, true);
    screenshots.finishContext();
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);

    List<File> allScreenshots = screenshots.getScreenshots();
    assertThat(allScreenshots).hasSize(5);
    assertThat(allScreenshots.get(0))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.0.png", ts)));
    assertThat(allScreenshots.get(1))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.1.png", ts)));
    assertThat(allScreenshots.get(2))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/YourTest/test_another_method/%s.2.png", ts)));
    assertThat(allScreenshots.get(3))
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.3.png", ts)));
    assertThat(allScreenshots.get(4))
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.4.png", ts)));
  }

  @Test
  void collectsAllThreadScreenshots() {
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);
    screenshots.finishContext();
    screenshots.startContext("ui/YourTest/test_another_method/");
    screenshots.takeScreenshot(driver, true, true);
    screenshots.finishContext();
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);

    List<File> allThreadScreenshots = screenshots.getThreadScreenshots();
    assertThat(allThreadScreenshots).hasSize(5);
    assertThat(allThreadScreenshots.get(0))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.0.png", ts)));
    assertThat(allThreadScreenshots.get(1))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/MyTest/test_some_method/%s.1.png", ts)));
    assertThat(allThreadScreenshots.get(2))
      .hasToString(dir + normalize(String.format("/build/reports/tests/ui/YourTest/test_another_method/%s.2.png", ts)));
    assertThat(allThreadScreenshots.get(3))
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.3.png", ts)));
    assertThat(allThreadScreenshots.get(4))
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.4.png", ts)));
  }

  @Test
  void collectsContextScreenshots() {
    screenshots.startContext("build/reports/tests/ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);
    screenshots.takeScreenshot(driver, true, true);

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

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.0.png", ts)));

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.1.png", ts)));

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastScreenshot())
      .hasToString(dir + normalize(String.format("/build/reports/tests/%s.2.png", ts)));
  }

  @Test
  void canGetLastThreadScreenshot() {
    assertThat(screenshots.getLastThreadScreenshot())
      .isEmpty();

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File(String.format("build/reports/tests/%s.0.png", ts)).getAbsoluteFile());

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File(String.format("build/reports/tests/%s.1.png", ts)).getAbsoluteFile());

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastThreadScreenshot())
      .hasValue(new File(String.format("build/reports/tests/%s.2.png", ts)).getAbsoluteFile());
  }

  @Test
  void canGetLastThreadFullScreenshot() {
    assertThat(screenshots.lastThreadScreenshot()).isEmpty();

    Screenshot screenshot1 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastThreadScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot1.summary());

    Screenshot screenshot2 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastThreadScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot2.summary());

    Screenshot screenshot3 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastThreadScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot3.summary());
  }

  @Test
  void canGetLastContextFullScreenshot() {
    assertThat(screenshots.lastContextScreenshot()).isEmpty();

    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.lastContextScreenshot()).isEmpty();

    Screenshot screenshot1 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastContextScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot1.summary());

    Screenshot screenshot2 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastContextScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot2.summary());

    Screenshot screenshot3 = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.lastContextScreenshot())
      .map(Screenshot::summary)
      .hasValue(screenshot3.summary());
  }

  @Test
  void canGetLastContextScreenshot() {
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();

    screenshots.startContext("ui/MyTest/test_some_method/");
    assertThat(screenshots.getLastContextScreenshot())
      .isEmpty();
    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File(String.format("build/reports/tests/ui/MyTest/test_some_method/%s.0.png", ts)).getAbsoluteFile());

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File(String.format("build/reports/tests/ui/MyTest/test_some_method/%s.1.png", ts)).getAbsoluteFile());

    screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshots.getLastContextScreenshot())
      .hasValue(new File(String.format("build/reports/tests/ui/MyTest/test_some_method/%s.2.png", ts)).getAbsoluteFile());
  }

  @Test
  void canFormatScreenShotPathWithSpaces() {
    config.reportsUrl("http://ci.org/job/123/artifact");
    config.reportsFolder("build/reports/path with spaces/");

    String screenShot = screenshots.takeScreenshot(driver, true, false).getImage();

    assertThat(screenShot)
      .contains("http://ci.org/job/123/artifact/build/reports/path%20with%20spaces/");
  }

  @Test
  void doNotEncodeReportsURL() {
    config.reportsUrl("http://ci.org/path%20with%spaces/");

    String screenShotPath = screenshots.takeScreenshot(driver, true, false).getImage();

    assertThat(screenShotPath)
      .contains("http://ci.org/path%20with%spaces/");
  }

  @Test
  void encodePath() {
    assertThat(screenshots.encodePath("/foo bar/boom room/pdf")).isEqualTo("/foo%20bar/boom%20room/pdf");
  }

  @Test
  void convertsScreenshotFileNameToCIUrl() {
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");

    String screenshot = screenshots.takeScreenshot(driver, true, false).summary();
    assertThat(screenshot)
      .startsWith("Screenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/")
      .endsWith(".png");
  }

  @Test
  void convertsReportUrlForOutsideSavedScreenshot() throws IOException {
    String reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    config.reportsUrl(reportsUrl);

    // directory, that not in 'user.dir'
    config.reportsFolder(Files.createTempDirectory("artifacts-storage").toFile().getAbsolutePath());

    String screenshot = screenshots.takeScreenshot(driver, true, false).summary();
    assertThat(screenshot)
      .as("Concatenate reportUrl with screenshot file name if it saved outside of build/project home dir")
      .startsWith(String.format("Screenshot: %s", reportsUrl + new File(screenshot).getName()));
  }

  @Test
  void returnsScreenshotFileName() {
    config.reportsUrl(null);
    String currentDir = System.getProperty("user.dir");
    if (separatorChar == '\\') {
      currentDir = '/' + currentDir.replace('\\', '/');
    }

    currentDir = currentDir.replace(" ", "%20"); //the screenshot path uses %20 instead of the space character

    String screenshot = screenshots.takeScreenshot(driver, true, false).summary();
    assertThat(screenshot)
      .startsWith(String.format("Screenshot: file:%s/build/reports/tests/", currentDir))
      .endsWith(".png");
  }

  @Test
  void doesNotAddScreenshot_if_screenshotsAreDisabled() {
    config.screenshots(true);

    String screenshot = screenshots.takeScreenshot(driver, false, false).summary();
    assertThat(screenshot).isNullOrEmpty();
    verify(webDriver, never()).getScreenshotAs(any());
  }

  @Test
  void screenshotAddedToThreadScreenshots() {
    config.screenshots(true);
    screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.threadScreenshots()).size().isEqualTo(1);
  }

  @Test
  void imageIsAccesableFromThreadScreenshots() {
    config.screenshots(true);
    Screenshot screenshot = screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.threadScreenshots().get(0).getImage()).isEqualTo(screenshot.getImage());
  }

  @Test
  void inThreadScreenshotsImageValueIsNullIfFlagWasDisabledF() {
    config.screenshots(true);
    screenshots.takeScreenshot(driver, false, true);

    assertThat(screenshots.threadScreenshots().get(0).getImage()).isNull();
  }

  @Test
  void inThreadScreenshotsSourceValueIsNullIfFlagWasDisabledF() {
    config.screenshots(true);
    screenshots.takeScreenshot(driver, true, false);

    assertThat(screenshots.threadScreenshots().get(0).getSource()).isNull();
  }

  @Test
  void sourceIsAccesableFromThreadScreenshots() {
    config.screenshots(true);
    Screenshot screenshot = screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.threadScreenshots().get(0).getSource()).isEqualTo(screenshot.getSource());
  }

  @Test
  void imageIsAccesableFromCurrentContextScreenshots() {
    config.screenshots(true);
    screenshots.startContext("ui/MyTest/test_some_method/");
    Screenshot screenshot = screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.contextScreenshots().get(0).getImage()).isEqualTo(screenshot.getImage());
  }

  @Test
  void inCurrentContextScreenshotsImageValueIsNullIfFlagWasDisabledF() {
    config.screenshots(true);
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, false, true);

    assertThat(screenshots.contextScreenshots().get(0).getImage()).isNull();
  }

  @Test
  void inCurrentContextScreenshotsSourceValueIsNullIfFlagWasDisabledF() {
    config.screenshots(true);
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, true, false);

    assertThat(screenshots.contextScreenshots().get(0).getSource()).isNull();
  }

  @Test
  void sourceIsAccesableFromCurrentContextScreenshots() {
    config.screenshots(true);
    screenshots.startContext("ui/MyTest/test_some_method/");
    Screenshot screenshot = screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.contextScreenshots().get(0).getSource()).isEqualTo(screenshot.getSource());
  }

  @Test
  void screenshotAddedToCurrentContextScreenshots() {
    config.screenshots(true);
    screenshots.startContext("ui/MyTest/test_some_method/");
    screenshots.takeScreenshot(driver, true, true);

    assertThat(screenshots.contextScreenshots()).size().isEqualTo(1);
  }

  @Test

  void printHtmlPath_if_savePageSourceIsEnabled() {
    config.savePageSource(false);
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");
    doReturn(new File("build/reports/page123.html")).when(extractor).extract(eq(config), eq(webDriver), any());

    Screenshot screenshot = screenshots.takeScreenshot(driver, true, true);
    assertThat(screenshot.summary()).isEqualTo(String.format(
      "Screenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/%s.0.png" +
        "%nPage source: http://ci.mycompany.com/job/666/artifact/build/reports/page123.html", ts
    ));
  }

  private String normalize(String path) {
    return separatorChar == '\\' ? path.replace('/', separatorChar) : path;
  }
}
