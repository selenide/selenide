package integration;

import com.codeborne.selenide.Configuration;
import org.junit.Test;

import java.io.File;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileDownloadTest extends IntegrationTest {
  File folder = new File(Configuration.reportsFolder);

  @Test
  public void userCanDownloadFiles() {
    openFile("page_with_uploads.html");
    File downloadedFile = $(byText("Download jquery")).download();

    assertEquals("jquery-1.8.3.js", downloadedFile.getName());
    assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }
}
