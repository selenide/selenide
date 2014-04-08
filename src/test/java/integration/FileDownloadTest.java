package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileDownloadTest extends IntegrationTest {
  File folder = new File(Configuration.reportsFolder);

  @Before
  public void setUp() {
    openFile("page_with_uploads.html");
  }

  @Test
  public void userCanDownloadFiles() throws IOException {
    File downloadedFile = $(byText("Download me")).download();

    assertEquals("hello_world.txt", downloadedFile.getName());
    assertEquals("Hello, WinRar!", FileUtils.readFileToString(downloadedFile));
    assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }

  @Test(expected = FileNotFoundException.class)
  public void downloadMissingFile() throws IOException {
    $(byText("Download missing file")).download();
  }

  @Test(expected = IllegalArgumentException.class)
  public void downloadUsesHrefAttribute() throws FileNotFoundException {
    $("h1").download();
  }
}
