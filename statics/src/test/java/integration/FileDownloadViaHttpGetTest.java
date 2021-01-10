package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.TimeoutException;
import com.codeborne.selenide.logevents.EventsCollector;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.nio.file.Files.createTempDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

final class FileDownloadViaHttpGetTest extends IntegrationTest {
  private static final String LISTENER = "SelenideLoggerTest";
  private final EventsCollector collector = new EventsCollector();
  private final File folder = new File(Configuration.downloadsFolder);

  @BeforeEach
  void setUp() {
    Configuration.fileDownload = HTTPGET;
    Configuration.timeout = 1000;
    openFile("page_with_uploads.html");
    SelenideLogger.addListener(LISTENER, collector);
  }

  @AfterEach
  void tearDown() {
    SelenideLogger.removeListener(LISTENER);
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

    assertThat(collector.events()).hasSize(1);

    LogEvent logEvent = collector.events().get(0);
    assertThat(logEvent).hasToString("$(\"by text: Download me\") download()");
    assertThat(logEvent.getElement()).isEqualTo("by text: Download me");
    assertThat(logEvent.getStatus()).isEqualTo(PASS);
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
  void downloadMissingFile() {
    assertThatThrownBy(() -> $(byText("Download missing file")).download())
      .isInstanceOf(FileNotFoundException.class)
      .hasMessageMatching("Failed to download file http.+/files/unexisting_file.png: .+");

    assertThat(collector.events()).hasSize(1);

    LogEvent logEvent = collector.events().get(0);
    assertThat(logEvent).hasToString("$(\"by text: Download missing file\") download()");
    assertThat(logEvent.getElement()).isEqualTo("by text: Download missing file");
    assertThat(logEvent.getStatus()).isEqualTo(FAIL);
  }

  @Test
  void downloadFileByName() {
    assertThatThrownBy(() -> $(byText("Download me")).download(withName("good_bye_world.txt")))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessageMatching("Failed to download file from http.+/files/hello_world.txt in 1000 ms." +
        " with file name \"good_bye_world.txt\";" + System.lineSeparator() + " actually downloaded: .+hello_world.txt");

    assertThat(collector.events()).hasSize(1);

    LogEvent logEvent = collector.events().get(0);
    assertThat(logEvent).hasToString("$(\"by text: Download me\") download(with file name \"good_bye_world.txt\")");
    assertThat(logEvent.getElement()).isEqualTo("by text: Download me");
    assertThat(logEvent.getStatus()).isEqualTo(FAIL);
  }

  @Test
  void downloadWithCustomTimeout() throws IOException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(3000);

    assertThat(downloadedFile.getName())
      .isEqualTo("hello_world.txt");
  }

  @Test
  void downloadsGetsTimeoutException() throws IOException {
    try {
      $(byText("Download me slowly (2000 ms)")).download(1000);
      fail("expected TimeoutException");
    } catch (TimeoutException expected) {
      assertThat(expected)
        .hasMessageStartingWith("Failed to download ")
        .hasMessageEndingWith("/files/hello_world.txt?pause=2000 in 1000 ms.");
    }
  }

  @Test
  void downloadWithQueryParamsWithoutHeaders() throws FileNotFoundException {
    openFile("download.html");
    final File downloadedFile = $("#link").download();
    assertThat(downloadedFile.getName())
      .isEqualTo("hello_world.txt");
  }

  @Test
  void downloadsFilesToCustomFolder() throws IOException {
    String downloadsFolder = createTempDirectory("selenide-tests-custom-folder-get").toString();
    Configuration.downloadsFolder = downloadsFolder;

    File downloadedFile = $(byText("Download me")).download();

    assertThat(downloadedFile.getAbsolutePath())
      .startsWith(new File(downloadsFolder).getAbsolutePath());
  }

  @Test
  void downloadsPdfFile() throws FileNotFoundException {
    File downloadedFile = $(byText("Download a PDF")).download(withExtension("pdf"));

    assertThat(downloadedFile.getName()).isEqualTo("minimal.pdf");
  }

  @Test
  void downloadWithOptions() throws IOException {
    Configuration.fileDownload = PROXY;
    Configuration.timeout = 1;

    File downloadedFile = $(byText("Download me")).download(using(HTTPGET)
      .withFilter(withExtension("txt"))
      .withTimeout(4000));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }

  @Test
  void downloadWithCustomMethodButStandardTimeout() throws IOException {
    Configuration.fileDownload = PROXY;
    Configuration.timeout = 4000;

    File downloadedFile = $(byText("Download me")).download(using(HTTPGET).withFilter(withExtension("txt")));

    assertThat(downloadedFile.getName()).isEqualTo("hello_world.txt");
  }
}
