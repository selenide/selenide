package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.$;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.assertj.core.api.Assertions.assertThat;

public class ScreenshotsTest extends IntegrationTest {
  private String reportsFolder = new File(Configuration.reportsFolder).getAbsolutePath();

  @BeforeEach
  void openTestPage() {
    openFile("page_with_big_divs.html");
  }

  @Test
  void withClassAndMethodName() {
    String method = UUID.randomUUID().toString();

    String screenshot = Screenshots.takeScreenShot("org.blah.SomeTest", method);

    assertThat(screenshot)
      .startsWith(reportsFolder + "/org/blah/SomeTest/" + method)
      .matches(".*" + method + "\\.\\d+\\.png");
    assertThat(files(method)).hasSize(2);
    assertThat(Screenshots.getLastScreenshot().getAbsolutePath()).isEqualTo(screenshot);
  }

  @Test
  void withFileName() {
    String fileName = UUID.randomUUID().toString();

    String screenshot = Screenshots.takeScreenShot(fileName);

    assertThat(screenshot)
      .startsWith(reportsFolder + "/" + fileName)
      .matches(".*" + fileName + "\\.png");
    assertThat(files(fileName)).hasSize(2);
    assertThat(Screenshots.getLastScreenshot().getAbsolutePath()).isEqualTo(screenshot);
  }

  @Test
  void takeScreenShotAsFile() {
    File screenshot = Screenshots.takeScreenShotAsFile();

    assertThat(screenshot.getAbsolutePath()).endsWith(".png");
    assertThat(files(screenshot.getParentFile(), screenshot.getName())).hasSize(1);
    assertThat(Screenshots.getLastScreenshot()).isEqualTo(screenshot);
  }

  @Test
  void takeScreenShot_ofWebElement() throws IOException {
    File dir = new File(reportsFolder, "/ScreenshotsTest/takeScreenShot_ofWebElement/");
    FileUtils.deleteDirectory(dir);
    Screenshots.startContext("ScreenshotsTest", "takeScreenShot_ofWebElement");

    try {
      File screenshot = Screenshots.takeScreenShot($("#small_div"));

      assertThat(screenshot.getAbsolutePath()).endsWith(".png");
      assertThat(screenshot.getAbsolutePath())
        .startsWith(reportsFolder + "/ScreenshotsTest/takeScreenShot_ofWebElement/")
        .matches(".*\\.png");
      assertThat(files(dir, "")).hasSize(1);
      assertThat(Screenshots.getLastScreenshot()).isNull();
    }
    finally {
      Screenshots.finishContext();
    }
  }

  @Test
  void takeScreenShot_ofWebElement_insideFrame() throws IOException {
    openFile("page_with_iframe.html");
    File dir = new File(reportsFolder, "/ScreenshotsTest/takeScreenShot_ofWebElement_insideFrame/");
    FileUtils.deleteDirectory(dir);
    Screenshots.startContext("ScreenshotsTest", "takeScreenShot_ofWebElement_insideFrame");

    try {
      File screenshot = Screenshots.takeScreenShot($("iframe#iframe_page"), $("#small_div"));

      assertThat(screenshot.getAbsolutePath()).endsWith(".png");
      assertThat(screenshot.getAbsolutePath())
        .startsWith(reportsFolder + "/ScreenshotsTest/takeScreenShot_ofWebElement_insideFrame/")
        .matches(".*\\.png");
      assertThat(files(dir, "")).hasSize(1);
      assertThat(Screenshots.getLastScreenshot()).isNull();
    }
    finally {
      Screenshots.finishContext();
    }
  }

  @Test
  void takeScreenShotAsImage_ofWebElement_insideFrame() throws IOException {
    openFile("page_with_iframe.html");
    File dir = new File(reportsFolder, "/ScreenshotsTest/takeScreenShotAsImage_ofWebElement_insideFrame/");
    FileUtils.deleteDirectory(dir);
    Screenshots.startContext("ScreenshotsTest", "takeScreenShotAsImage_ofWebElement_insideFrame");

    try {
      BufferedImage screenshot = Screenshots.takeScreenShotAsImage($("iframe#iframe_page"), $("#small_div"));

      assertThat(screenshot.getWidth()).isGreaterThan(10);
      assertThat(screenshot.getHeight()).isGreaterThan(10);
      assertThat(dir).doesNotExist();
      assertThat(Screenshots.getLastScreenshot()).isNull();
    }
    finally {
      Screenshots.finishContext();
    }
  }

  @Test
  void takeScreenShotAsImage_ofWebElement() throws IOException {
    File dir = new File(reportsFolder, "/ScreenshotsTest/takeScreenShotAsImage_ofWebElement/");
    FileUtils.deleteDirectory(dir);
    Screenshots.startContext("ScreenshotsTest", "takeScreenShotAsImage_ofWebElement");

    try {
      BufferedImage screenshot = Screenshots.takeScreenShotAsImage($("#small_div"));

      assertThat(screenshot.getWidth()).isGreaterThan(10);
      assertThat(screenshot.getHeight()).isGreaterThan(10);
      assertThat(dir).doesNotExist();
      assertThat(Screenshots.getLastScreenshot()).isNull();
    }
    finally {
      Screenshots.finishContext();
    }
  }

  private Collection<File> files(String nameContaining) {
    return files(new File(reportsFolder), nameContaining);
  }

  private Collection<File> files(File dir, String nameContaining) {
    return listFiles(dir, new IOFileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().contains(nameContaining);
      }

      @Override
      public boolean accept(File dir, String name) {
        return name.contains(nameContaining);
      }
    }, TrueFileFilter.INSTANCE);
  }
}
