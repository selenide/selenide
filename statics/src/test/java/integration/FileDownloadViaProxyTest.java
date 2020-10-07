package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileDownloadViaProxyTest extends IntegrationTest {
  private final File folder = new File(Configuration.downloadsFolder);

  @BeforeEach
  void setUp() {
    useProxy(true);
    openFile("page_with_uploads.html");
    timeout = 1000;
  }

  @Test
  void downloadsFiles() throws IOException {
    File downloadedFile = $(byText("Download me")).download();

    assertThat(downloadedFile.getName())
      .isEqualTo("hello_world.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCyrillicName() throws IOException {
    File downloadedFile = $(byText("Download file with cyrillic name")).download();

    assertThat(downloadedFile.getName())
      .isEqualTo("файл-с-русским-названием.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Превед медвед!");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithForbiddenCharactersInName() throws IOException {
    File downloadedFile = $(byText("Download file with \"forbidden\" characters in name")).download();
    assertThat(downloadedFile.getName())
      .isEqualTo("имя+с+_pound,_percent,_ampersand,_left,_right,_backslash," +
        "_left,_right,_asterisk,_question,_dollar,_exclamation,_quote,_quotes," +
        "_colon,_at,_plus,_backtick,_pipe,_equal.txt");
    assertThat(readFileToString(downloadedFile, "UTF-8"))
      .isEqualTo("Превед \"короед\"! Амперсанды &everywhere&&;$#`\n");
    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadExternalFile() throws FileNotFoundException {
    open("http://the-internet.herokuapp.com/download");
    File video = $(By.linkText("some-file.txt")).download();
    assertThat(video.getName())
      .isEqualTo("some-file.txt");
  }

  @Test
  void downloadMissingFile() {
    timeout = 100;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension(".pdf")))
      .isInstanceOf(FileNotFoundException.class);
  }

  @Test
  public void download_withCustomTimeout() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(3000);

    assertThat(downloadedFile.getName())
      .isEqualTo("hello_world.txt");
  }

  @Test
  public void download_byName() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(withName("hello_world.txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @Test
  public void download_byNameRegex() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(withNameMatching("hello_.\\w+\\.txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @Test
  public void download_byExtension() throws FileNotFoundException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(timeout, withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @Test
  void downloadsFilesToCustomFolder() throws IOException {
    closeWebDriver();

    try {
      String downloadsFolder = "build/custom-folder";
      Configuration.downloadsFolder = downloadsFolder;
      openFile("page_with_uploads.html");

      File downloadedFile = $(byText("Download me")).download();

      assertThat(downloadedFile.getAbsolutePath())
        .startsWith(new File(downloadsFolder).getAbsolutePath());
    }
    finally {
      closeWebDriver();
    }
  }

  @Test
  void downloadsPdfFile() throws FileNotFoundException {
    File downloadedFile = $(byText("Download a PDF")).download(timeout, withExtension("pdf"));

    assertThat(downloadedFile.getName()).isEqualTo("minimal.pdf");
  }

  @Test
  void downloadWithOptions() throws IOException {
    Configuration.fileDownload = FOLDER;
    Configuration.timeout = 1;

    File downloadedFile = $(byText("Download me")).download(using(PROXY)
      .withFilter(withExtension("txt"))
      .withTimeout(4000));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }
}
