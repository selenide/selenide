package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.codeborne.selenide.Selenide.$;

/**
 * Check the issue of multiple screen shots (task::firefox)
 * 1. b1-4 all nonexistent, then take single screenshot instead of four
 * 2. b1 nonexistent, take single screenshot
 * check the number of screenshots in build\reports\tests\firefox\integration\MultipleScreenshotTest\emptyMethod
 */
public class MultipleScreenshotTest extends IntegrationTest {
  static String DIR_PATH = "build/reports/tests/firefox/integration/MultipleScreenshotTest/emptyMethod";

  @BeforeEach
  void openTestPage() {
    openFile("multipleScreenshot.html");
  }

  @Test
  public void testMultiMissElement() {
    int init = getTxtFilesCount(new File(DIR_PATH));
    try {
      $("#b1").$("#b2").$("#b3").$("#b4").click();
    } catch (Error e) {
      assertEquals(1, getTxtFilesCount(new File(DIR_PATH)) - init);
    }
  }

  @Test
  public void testSingleMissElement() {
    int init = getTxtFilesCount(new File(DIR_PATH));
    try {
      $("#b1").click();
    } catch (Error e) {
      assertEquals(1, getTxtFilesCount(new File(DIR_PATH)) - init);
    }
  }

  private static int getTxtFilesCount(File srcFile) {
    int count = 0;
    if (srcFile == null) {
      throw new NullPointerException();
    }
    File[] files = srcFile.listFiles();
    for (File f : files) {
      if (f.isDirectory()) {
        getTxtFilesCount(f);
      } else {
        if (f.getName().endsWith(".png")) {
          count++;
        }
      }
    }
    return count;
  }

}