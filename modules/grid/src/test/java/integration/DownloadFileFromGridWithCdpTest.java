package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.impl.FileContent;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.codeborne.selenide.Configuration.downloadsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.files.DownloadActions.clickAndConfirm;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class DownloadFileFromGridWithCdpTest extends AbstractGridTest {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileFromGridWithCdpTest.class);
  private final File folder = new File(downloadsFolder).getAbsoluteFile();

  @BeforeEach
  void openFileUploadForm() {
    assumeThat(isFirefox())
      .as("Firefox doesn't support CDP download method")
      .isFalse();
    Configuration.remote = gridUrl().toString();
    Configuration.browserCapabilities.setCapability("se:downloadsEnabled", true);
    Configuration.fileDownload = CDP;
    openFile("page_with_uploads.html");
  }

  @Test
  void downloadFile() {
    File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithAlert() {
    File downloadedFile = $(byText("Download me with alert")).download(file().withExtension("txt").withAction(
        clickAndConfirm("Are you sure to download it?")
      )
    );
    log.info("Downloaded file {}", downloadedFile.getAbsolutePath());

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCyrillicName() {
    File downloadedFile = $(byText("Download file with cyrillic name")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("файл-с-русским-названием.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Превед медвед!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadMissingFile() {
    timeout = 111;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension("txt")))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download file with extension \"txt\" in 111 ms");
  }

  @Test
  void downloadsPdfFile() {
    File downloadedFile = $(byText("Download a PDF")).download(file().withExtension("pdf").withTimeout(timeout));

    assertThat(downloadedFile.getName()).matches("minimal.*.pdf");
    assertThat(downloadedFile).content().startsWith("%PDF-1.1");
  }

  @Test
  void downloadsPotentiallyHarmfulWindowsFiles() throws IOException {
    assumeThat(isEdge())
      .as("Edge shows warning like '*.exe file is not downloaded'")
      .isFalse();

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
  void downloadEmptyFile() {
    File downloadedFile = $(byText("Download empty file")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).matches("empty-file.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithPartExtension() {
    File downloadedFile = $(byText("Download file *part")).download(file().withExtension("part"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.part");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, part WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCrdownloadExtension() {
    File downloadedFile = $(byText("Download file *crdownload")).download(file().withName("hello_world.crdownload").withTimeout(900));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.crdownload");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, crdownload WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  public void download_slowly() {
    File downloadedFile = $(byText("Download me slowly"))
      .download(file().withName("hello_world.txt").withTimeout(4000));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_super_slowly() {
    File downloadedFile = $(byText("Download me super slowly")).download(file().withExtension("txt").withTimeout(6000));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadLargeFile() {
    File downloadedFile = $(byText("Download large file")).download(file().withExtension("txt").withTimeout(8000));

    assertThat(downloadedFile).hasName("large_file.txt");
    assertThat(downloadedFile).hasSize(5 * 1024 * 1024);
  }

  @ParameterizedTest
  @ValueSource(strings = {"empty.html", "hello_world.txt", "download.html"})
  void downloadMultipleFiles(String fileName) {
    openFile("downloadMultipleFiles.html");

    File text = $("#multiple-downloads").download(withName(fileName));

    assertThat(text.getName()).isEqualTo(fileName);
    assertThat(text.length()).isEqualTo(new FileContent(fileName).content().length());
  }

  @Test
  void downloadWithRedirect() {
    File downloadedFile = $(byText("Download with redirect")).download();
    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadFileWithCustomBrowser() {
    closeWebDriver();
    try {
      Configuration.browser = CustomWebDriverProvider.class.getName();
      openFile("page_with_uploads.html");
      File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

      assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
      assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
      assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
    }
    finally {
      closeWebDriver();
    }
  }

  private static class CustomWebDriverProvider extends ChromeDriverFactory {
    @Override
    public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy,
                            @Nullable File browserDownloadsFolder) {
      return super.create(config, browser, proxy, browserDownloadsFolder);
    }
  }
}
