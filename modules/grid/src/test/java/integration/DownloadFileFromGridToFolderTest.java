package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.impl.FileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

import static com.codeborne.selenide.Configuration.downloadsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.DownloadOptions.files;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.files.DownloadActions.clickAndConfirm;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.remote.CapabilityType.ENABLE_DOWNLOADS;

final class DownloadFileFromGridToFolderTest extends AbstractGridTest {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileFromGridToFolderTest.class);
  private final File folder = new File(downloadsFolder).getAbsoluteFile();

  @BeforeEach
  void openFileUploadForm() {
    Configuration.browserCapabilities.setCapability(ENABLE_DOWNLOADS, true);
    Configuration.fileDownload = FOLDER;
    openFile("page_with_uploads.html");
  }

  @Test
  @Tag("smoke")
  void downloadFile() {
    File downloadedFile = $(byText("Download me")).download(withNameMatching("hello.*\\.txt"));

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
  void downloadsFileWithSpaceInName() {
    File downloadedFile = $(byText("Download file with space in name")).download(withExtension("txt"));

    assertThat(downloadedFile.getName()).isEqualTo("file 0 & _ ' `backticks`.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("File with space in name\n");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  void failFastOnNotMatchingFile() {
    long start = currentTimeMillis();

    DownloadOptions downloadOptions = DownloadOptions.using(FOLDER)
      .withIncrementTimeout(Duration.ofMillis(1002L))
      .withFilter(withName("foo.bar"))
      .withTimeout(ofSeconds(20));

    assertThatThrownBy(() -> $(byText("Download me")).download(downloadOptions))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download file with name \"foo.bar\" in 20s")
      .hasMessageFindingMatch("haven't been modified for 1(\\.\\d{1,3})?s")
      .hasMessageContaining("incrementTimeout: 1.002s");

    long end = currentTimeMillis();
    assertThat(end - start)
      .as("Less than timeout (but greater than increment timeout)")
      .isBetween(1002L, 10_000L);
  }

  @Test
  void downloadMissingFile() {
    timeout = 11;
    assertThatThrownBy(() -> $(byText("Download missing file")).download(withExtension("txt")))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download file with extension \"txt\" in 11ms");
  }

  @Test
  @SuppressWarnings("deprecation")
  void downloadsPdfFile() {
    File downloadedFile = $(byText("Download a PDF")).download(timeout, withExtension("pdf"));

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
    File downloadedFile = $(byText("Download DMG file")).download(
      file().withExtension("dmg").withTimeout(ofSeconds(8)));

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
    File downloadedFile = $(byText("Download file *part")).download(withExtension("part"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.part");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, part WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  @SuppressWarnings("deprecation")
  void downloadsFileWithCrdownloadExtension() {
    File downloadedFile = $(byText("Download file *crdownload")).download(timeout, withName("hello_world.crdownload"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.crdownload");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, crdownload WinRar!");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void download_slowly() {
    File downloadedFile = $(byText("Download me slowly"))
      .download(timeout * 2, withName("hello_world.txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  @SuppressWarnings("deprecation")
  public void download_super_slowly() {
    File downloadedFile = $(byText("Download me super slowly")).download(timeout * 3, withExtension("txt"));

    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  @SuppressWarnings("deprecation")
  void downloadLargeFile() {
    File downloadedFile = $(byText("Download large file")).download(timeout * 2, withExtension("txt"));

    assertThat(downloadedFile).hasName("large_file.txt");
    assertThat(downloadedFile).hasSize(5L * 1024 * 1024);
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
  void downloadMultipleFilesAtOnce() {
    openFile("downloadMultipleFiles.html");

    Collection<File> result = $("#multiple-downloads").downloadFiles(files(3));

    Map<String, File> files = result.stream().collect(toMap(File::getName, file -> file));
    assertThat(files).hasSizeGreaterThanOrEqualTo(3);
    assertThat(files.keySet()).containsExactlyInAnyOrder("empty.html", "hello_world.txt", "download.html");

    assertThat(files.get("empty.html")).content().isEqualToIgnoringNewLines(new FileContent("empty.html").content());
    assertThat(files.get("hello_world.txt")).content().isEqualToIgnoringNewLines(new FileContent("hello_world.txt").content());
    assertThat(files.get("download.html")).content().isEqualToIgnoringNewLines(new FileContent("download.html").content());
  }

  @Test
  void downloadMultipleFiles_errorMessage() {
    openFile("downloadMultipleFiles.html");

    assertThatThrownBy(() -> $("#multiple-downloads").downloadFiles(files(22).withTimeout(200)))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download at least 22 files in 200ms (found 3 files: [")
      .hasMessageContaining("hello_world.txt")
      .hasMessageContaining("empty.html")
      .hasMessageContaining("download.html")
      .hasMessageContaining("Timeout: 200ms");
  }

  @Test
  void downloadWithRedirect() {
    File downloadedFile = $(byText("Download with redirect")).download(withExtension("txt"));
    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void downloadFileWithoutContent() {
    File downloadedFile = $(byText("Download me")).download(file().withExtension("txt").withoutContent());

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Mocked file content");
    assertThat(downloadedFile.getAbsolutePath()).startsWith(folder.getAbsolutePath());
  }
}
