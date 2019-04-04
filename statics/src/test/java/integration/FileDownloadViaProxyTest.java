package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.apache.commons.io.FileUtils.readFileToString;

class FileDownloadViaProxyTest extends IntegrationTest {
  private File folder = new File(Configuration.reportsFolder);

  @BeforeEach
  void setUp() {
    useProxy(true);
    openFile("page_with_uploads.html");
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
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
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
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
  void downloadExternalFile() throws FileNotFoundException {
    open("http://the-internet.herokuapp.com/download");
    File video = $(By.linkText("some-file.txt")).download();
    assertThat(video.getName())
      .isEqualTo("some-file.txt");
  }

  @Test
  void downloadMissingFile() {
    assertThatThrownBy(() -> $(byText("Download missing file")).download())
      .isInstanceOf(FileNotFoundException.class);
  }

  @Test
  @DisabledIfSystemProperty(named = "selenide.browser", matches = "chrome")
  public void download_withCustomTimeout() throws IOException {
    File downloadedFile = $(byText("Download me slowly (2000 ms)")).download(3000);

    assertThat(downloadedFile.getName())
      .isEqualTo("hello_world.txt");
  }
}
