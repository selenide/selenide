package integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.apache.commons.io.FileUtils.readFileToString;

class FileDownloadViaHttpGetTest extends IntegrationTest {
  private File folder = new File(Configuration.reportsFolder);

  @BeforeEach
  void setUp() {
    close();
    Configuration.fileDownload = HTTPGET;
    openFile("page_with_uploads.html");
  }

  @Test
  void downloadsFiles() throws IOException {
    Assumptions.assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...

    File downloadedFile = $(byText("Download me")).download();

    Assertions.assertEquals("hello_world.txt", downloadedFile.getName());
    Assertions.assertEquals("Hello, WinRar!", readFileToString(downloadedFile, "UTF-8"));
    Assertions.assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }

  @Test
  void downloadsFileWithCyrillicName() throws IOException {
    Assumptions.assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...

    File downloadedFile = $(byText("Download file with cyrillic name")).download();

    Assertions.assertEquals("файл-с-русским-названием.txt", downloadedFile.getName());
    Assertions.assertEquals("Превед медвед!", readFileToString(downloadedFile, "UTF-8"));
    Assertions.assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }

  @Test
  void downloadMissingFile() {
    Assertions.assertThrows(FileNotFoundException.class,
      () -> $(byText("Download missing file")).download());
  }
}
