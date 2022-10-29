package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static com.codeborne.selenide.Configuration.downloadsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.DownloadActions.clickAndConfirm;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static java.nio.file.Files.createTempDirectory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileDownloadToFolderTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(FileDownloadToFolderTest.class);
  private final File folder = new File(downloadsFolder).getAbsoluteFile();

  @BeforeEach
  void setUp() {
    Configuration.fileDownload = FOLDER;
    openFile("page_with_uploads.html");
    timeout = 100;
  }

  @Test
  void downloadsFiles() throws IOException {
    File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithAlert() throws IOException {
    File downloadedFile = $(byText("Download me with alert")).download(
      using(FOLDER).withFilter(withExtension("txt")).withAction(
        clickAndConfirm("Are you sure to download it?")
      )
    );
    log.info("Downloaded file {}", downloadedFile.getAbsolutePath());

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCyrillicName() throws IOException {
    File downloadedFile = $(byText("Download file with cyrillic name")).download(withExtension("txt"));

    assertThat(downloadedFile.getName())
      .isEqualTo("файл-с-русским-названием.txt");
    assertThat(downloadedFile).content()
      .isEqualToIgnoringNewLines("Превед медвед!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadExternalFile() throws FileNotFoundException {
    open("https://the-internet.herokuapp.com/download");
    File video = $(By.linkText("some-file.txt")).download(withExtension("txt"));

    assertThat(video.getName()).isEqualTo("some-file.txt");
  }

  @Test
  void downloadMissingFile() {
    timeout = 100;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension("txt")))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage("Failed to download file {by text: Download missing file} in 100 ms. with extension \"txt\"");
  }

  @Test
  void downloadMissingFileWithExtension() {
    timeout = 80;
    assertThatThrownBy(() -> $(byText("Download me")).download(withExtension("pdf")))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage("Failed to download file {by text: Download me} in 80 ms. with extension \"pdf\"");
  }

  @Test
  public void download_byName() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me")).download(withName("hello_world.txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_byNameRegex() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me")).download(withNameMatching("hello_.+\\.txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_byExtension() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadsFilesToCustomFolder() throws IOException {
    closeWebDriver();
    String customDownloadsFolder = createTempDirectory("selenide-tests-to-custom-folder").toString();
    downloadsFolder = customDownloadsFolder;

    try {
      openFile("page_with_uploads.html");
      File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

      assertThat(downloadedFile.getAbsolutePath())
        .startsWith(new File(customDownloadsFolder).getAbsolutePath());
      assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    }
    finally {
      closeWebDriver();
    }
  }

  @Test
  void downloadsPdfFile() throws IOException {
    File downloadedFile = $(byText("Download a PDF")).download(timeout, withExtension("pdf"));

    assertThat(downloadedFile.getName()).matches("minimal.*.pdf");
    assertThat(downloadedFile).content().startsWith("%PDF-1.1");
  }

  @Test
  void downloadsPotentiallyHarmfulWindowsFiles() throws IOException {
    File downloadedFile = $(byText("Download EXE file")).download(withNameMatching("\\w+\\.exe"));

    assertThat(downloadedFile.getName()).startsWith("tiny.exe");
    assertThat(Files.size(downloadedFile.toPath())).isEqualTo(43);
    assertThat(downloadedFile).content()
      .isEqualToIgnoringNewLines("Here might be potentially harmful exe file");
  }

  @Test
  void downloadsPotentiallyHarmfulMacFiles() throws IOException {
    File downloadedFile = $(byText("Download DMG file")).download(withExtension("dmg"));

    assertThat(downloadedFile.getName()).isEqualTo("tiny.dmg");
    assertThat(Files.size(downloadedFile.toPath())).isEqualTo(43);
  }

  @Test
  void downloadWithOptions() throws IOException {
    Configuration.fileDownload = PROXY;
    Configuration.timeout = 1;

    File downloadedFile = $(byText("Download me")).download(using(FOLDER)
      .withFilter(withExtension("txt"))
      .withTimeout(4000)
    );

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadEmptyFile() throws IOException {
    File downloadedFile = $(byText("Download empty file")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).matches("empty-file.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithPartExtension() throws IOException {
    File downloadedFile = $(byText("Download file *part")).download(withExtension("part"));

    assertThat(downloadedFile.getName())
      .matches("hello_world.*\\.part");
    assertThat(downloadedFile).content()
      .isEqualToIgnoringNewLines("Hello, part WinRar!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCrdownloadExtension() throws IOException {
    File downloadedFile = $(byText("Download file *crdownload")).download(withName("hello_world.crdownload"));

    assertThat(downloadedFile.getName())
      .matches("hello_world.*\\.crdownload");
    assertThat(downloadedFile).content()
      .isEqualToIgnoringNewLines("Hello, crdownload WinRar!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  public void download_super_slowly() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me super slowly"))
      .download(4000, withName("hello_world.txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_super_slowly_without_filter() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me super slowly")).download(4000);

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }
}
