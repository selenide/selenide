package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.using;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.nio.file.Files.createTempDirectory;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class FileDownloadViaProxyTest extends ProxyIntegrationTest {
  private final File folder = new File(Configuration.downloadsFolder).getAbsoluteFile();

  @BeforeEach
  void setUp() {
    openFile("page_with_uploads.html");
    timeout = 1000;
    Configuration.fileDownload = PROXY;
  }

  @Test
  void downloadsFiles() {
    File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithAlert() {
    File downloadedFile = $(byText("Download me with alert")).download(
      file().withExtension("txt").withAction((driver, link) -> {
        link.click();
        Alert alert = driver.switchTo().alert();
        assertThat(alert.getText()).isEqualTo("Are you sure to download it?");
        alert.dismiss();
      }));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithCyrillicName() {
    File downloadedFile = $(byText("Download file with cyrillic name")).download(file().withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("файл-с-русским-названием.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Превед медвед!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadsFileWithNorwayCharactersInName() {
    File downloadedFile = $(byText("Download file with \"ø\" in name")).download(file().withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("ø-report.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, Nørway!");
  }

  @Test
  void downloadsFileWithForbiddenCharactersInName() {
    File downloadedFile = $(byText("Download file with \"forbidden\" characters in name"))
      .download(withExtension("txt"));

    assertThat(downloadedFile).hasName("имя+с+_pound,_percent,_ampersand,_left,_right,_backslash," +
      "_left,_right,_asterisk,_question,_dollar,_exclamation,_quote,_quotes," +
      "_colon,_at,_plus,_backtick,_pipe,_equal.txt");

    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Превед \"короед\"! Амперсанды &everywhere&&;$#`");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void downloadMissingFile() {
    timeout = 10;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension("pdf")))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download file with extension \"pdf\" in 10 ms");
  }

  @Test
  public void download_withCustomTimeout() {
    File downloadedFile = $(byText("Download me slowly")).download(file().withExtension("txt").withTimeout(2000));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_byName() {
    File downloadedFile = $(byText("Download me slowly")).download(file().withName("hello_world.txt").withTimeout(2000));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_byNameRegex() {
    File downloadedFile = $(byText("Download me slowly")).download(file().withNameMatching("hello_.\\w+\\.txt").withTimeout(2000));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  public void download_byExtension() {
    File downloadedFile = $(byText("Download me slowly")).download(file().withExtension("txt").withTimeout(2000));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadsFilesToCustomFolder() throws IOException {
    closeWebDriver();

    try {
      String downloadsFolder = createTempDirectory("selenide-tests-custom-folder-proxy").toString();
      Configuration.downloadsFolder = downloadsFolder;
      openFile("page_with_uploads.html");

      File downloadedFile = $(byText("Download me")).download(withExtension("txt"));

      assertThat(downloadedFile.getAbsolutePath()).startsWith(new File(downloadsFolder).getAbsolutePath());
      assertThat(downloadedFile).hasName("hello_world.txt");
      assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
    }
    finally {
      closeWebDriver();
    }
  }

  @Test
  void downloadsPdfFile() {
    File downloadedFile = $(byText("Download a PDF")).download(file().withExtension("pdf").withTimeout(timeout));

    assertThat(downloadedFile).hasName("minimal.pdf");
    assertThat(downloadedFile).content().startsWith("%PDF-1.1");
  }

  @Test
  void downloadsPotentiallyHarmfulWindowsFiles() {
    File downloadedFile = $(byText("Download EXE file")).download(withExtension("exe"));

    assertThat(downloadedFile).hasName("tiny.exe");
    assertThat(downloadedFile).binaryContent().hasSize(43);
  }

  @Test
  void downloadsPotentiallyHarmfulMacFiles() {
    File downloadedFile = $(byText("Download DMG file")).download(withExtension("dmg"));

    assertThat(downloadedFile.getName()).isEqualTo("tiny.dmg");
    assertThat(downloadedFile).binaryContent().hasSize(43);
  }

  @Test
  void downloadWithOptions() {
    Configuration.fileDownload = FOLDER;
    Configuration.timeout = 1;

    File downloadedFile = $(byText("Download me")).download(using(PROXY)
      .withExtension("txt")
      .withTimeout(4000));

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
    File downloadedFile = $(byText("Download large file")).download(withExtension("txt"));

    assertThat(downloadedFile).hasName("large_file.txt");
    assertThat(downloadedFile).hasSize(5 * 1024 * 1024);
  }

  @Test
  void canDownloadFilesAfterUsingAnotherBrowser() {
    assumeThat(isChrome() || isFirefox()).isTrue();

    openFile("page_with_uploads.html");
    useAnotherBrowser();

    File downloadedFile = $(byText("Download me")).download(
      using(PROXY).withTimeout(ofSeconds(2)).withExtension("txt")
    );

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadWithRedirect() {
    File downloadedFile = $(byText("Download with redirect")).download(withExtension("txt"));
    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  private void useAnotherBrowser() {
    withProxy(proxy -> {
      WebDriver anotherBrowser = isFirefox() ? openFirefox(proxy) : openChrome(proxy);
      try {
        using(anotherBrowser, proxy, () -> {
          openFile("page_with_selects_without_jquery.html");
        });
      }
      finally {
        anotherBrowser.quit();
      }
    });
  }

  private void withProxy(Consumer<SelenideProxyServer> block) {
    SelenideProxyServer proxy = new SelenideProxyServer(new SelenideConfig(), null);
    proxy.start();
    try {
      block.accept(proxy);
    }
    finally {
      proxy.shutdown();
    }
  }

}

