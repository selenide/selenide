package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;

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
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileDownloadToFolderTest extends IntegrationTest {
  private final File folder = new File(downloadsFolder).getAbsoluteFile();

  @BeforeEach
  void setUp() {
    Configuration.fileDownload = FOLDER;
    openFile("page_with_uploads.html");
    timeout = 4000;
  }

  @RepeatedTest(10)
  void downloadsFiles() throws IOException {
    File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

    assertThat(downloadedFile.getName())
      .matches("hello_world.*\\.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @RepeatedTest(10)
  void downloadsFileWithAlert() throws IOException {
    File downloadedFile = $(byText("Download me with alert")).download(
      using(FOLDER).withFilter(withExtension("txt")).withAction(
        clickAndConfirm("Are you sure to download it?")
      )
    );

    assertThat(downloadedFile.getName())
      .matches("hello_world.*\\.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @RepeatedTest(10)
  void downloadsFileWithCyrillicName() throws IOException {
    File downloadedFile = $(byText("Download file with cyrillic name")).download(withExtension("txt"));

    assertThat(downloadedFile.getName())
      .isEqualTo("файл-с-русским-названием.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Превед медвед!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @RepeatedTest(10)
  void downloadExternalFile() throws FileNotFoundException {
    open("https://the-internet.herokuapp.com/download");
    File video = $(By.linkText("some-file.txt")).download(withExtension("txt"));

    assertThat(video.getName()).isEqualTo("some-file.txt");
  }

  @RepeatedTest(10)
  void downloadMissingFile() {
    timeout = 100;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension("txt")))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage("Failed to download file {by text: Download missing file} in 100 ms. with extension \"txt\"");
  }

  @RepeatedTest(10)
  void downloadMissingFileWithExtension() {
    timeout = 80;
    assertThatThrownBy(() -> $(byText("Download me")).download(withExtension("pdf")))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage("Failed to download file {by text: Download me} in 80 ms. with extension \"pdf\"");
  }

  @RepeatedTest(10)
  public void download_byName() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(withName("hello_world.txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @RepeatedTest(10)
  public void download_byNameRegex() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(withNameMatching("hello_.+\\.txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @RepeatedTest(10)
  public void download_byExtension() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(timeout, withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @RepeatedTest(10)
  void downloadsFilesToCustomFolder() throws IOException {
    closeWebDriver();
    String customDownloadsFolder = createTempDirectory("selenide-tests-to-custom-folder").toString();
    downloadsFolder = customDownloadsFolder;

    try {
      openFile("page_with_uploads.html");
      File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

      assertThat(downloadedFile.getAbsolutePath())
        .startsWith(new File(customDownloadsFolder).getAbsolutePath());
    }
    finally {
      closeWebDriver();
    }
  }

  @RepeatedTest(10)
  void downloadsPdfFile() throws IOException {
    File downloadedFile = $(byText("Download a PDF")).download(timeout, withExtension("pdf"));

    assertThat(downloadedFile.getName()).matches("minimal.*.pdf");
  }

  @RepeatedTest(10)
  void downloadsPotentiallyHarmfulWindowsFiles() throws IOException {
    File downloadedFile = $(byText("Download EXE file")).download(withNameMatching("\\w+\\.exe"));

    assertThat(downloadedFile.getName()).startsWith("tiny.exe");
    assertThat(Files.size(downloadedFile.toPath())).isEqualTo(43);
    assertThat(FileUtils.readFileToString(downloadedFile, UTF_8).trim())
      .isEqualTo("Here might be potentially harmful exe file");
  }

  @RepeatedTest(10)
  void downloadsPotentiallyHarmfulMacFiles() throws IOException {
    File downloadedFile = $(byText("Download DMG file")).download(withExtension("dmg"));

    assertThat(downloadedFile.getName()).isEqualTo("tiny.dmg");
    assertThat(Files.size(downloadedFile.toPath())).isEqualTo(43);
  }

  @RepeatedTest(10)
  void downloadWithOptions() throws IOException {
    Configuration.fileDownload = PROXY;
    Configuration.timeout = 1;

    File downloadedFile = $(byText("Download me")).download(using(FOLDER)
      .withFilter(withExtension("txt"))
      .withTimeout(4000)
    );

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8")).isEqualTo("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @RepeatedTest(10)
  void downloadEmptyFile() throws IOException {
    File downloadedFile = $(byText("Download empty file")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).matches("empty-file.*\\.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8")).isEqualTo("");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }
}
