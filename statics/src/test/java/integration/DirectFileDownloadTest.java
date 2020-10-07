package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.codeborne.selenide.Selenide.download;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;

final class DirectFileDownloadTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_big_divs.html");
  }

  @Test
  void downloadFileByDirectLink() throws IOException, URISyntaxException {
    Configuration.timeout = 4000;
    File file = download("/files/hello_world.txt");
    assertThat(file.getName()).isEqualTo("hello_world.txt");
    assertThat(readFileToString(file, UTF_8)).isEqualTo("Hello, WinRar!");
  }

  @Test
  void downloadFileWithCyrillicName() throws IOException, URISyntaxException {
    Configuration.timeout = 4000;
    File file = download(new URI("/files/файл-с-русским-названием.txt"));
    assertThat(file.getName()).isEqualTo("файл-с-русским-названием.txt");
    assertThat(readFileToString(file, UTF_8)).isEqualTo("Превед медвед!");
  }

  @Test
  void downloadFileWithCustomTimeout() throws IOException, URISyntaxException {
    Configuration.timeout = 10;
    File file = download("/files/hello_world.txt?pause=1000", 1500);
    assertThat(file.getName()).isEqualTo("hello_world.txt");
    assertThat(readFileToString(file, UTF_8)).isEqualTo("Hello, WinRar!");
  }
}
