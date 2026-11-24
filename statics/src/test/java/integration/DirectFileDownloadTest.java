package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static com.codeborne.selenide.Selenide.download;
import static org.assertj.core.api.Assertions.assertThat;

final class DirectFileDownloadTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    Configuration.timeout = 4000;
    openFile("page_with_big_divs.html");
  }

  @Test
  @Tag("smoke")
  void downloadFileByDirectLink() throws URISyntaxException {
    File file = download("/files/hello_world.txt");
    assertThat(file).hasName("hello_world.txt");
    assertThat(file).content().isEqualTo("Hello, WinRar!");
  }

  @Test
  void downloadFileWithCyrillicName() throws URISyntaxException {
    File file = download(new URI("/files/файл-с-русским-названием.txt"));
    assertThat(file).hasName("файл-с-русским-названием.txt");
    assertThat(file).content().isEqualTo("Превед медвед!");
  }

  @Test
  void downloadsFileWithSpaceInName() throws URISyntaxException {
    File file = download("/files/file%200%20%26%20_%20%27%20%60backticks%60.txt");
    assertThat(file).hasName("file 0 & _ ' `backticks`.txt");
    assertThat(file).content().isEqualToIgnoringNewLines("File with space in name\n");
  }

  @Test
  void downloadFileWithCustomTimeout() throws URISyntaxException {
    Configuration.timeout = 10;
    File file = download("/files/hello_world.txt?pause=1000", 1500);
    assertThat(file).hasName("hello_world.txt");
    assertThat(file).content().isEqualTo("Hello, WinRar!");
  }
}
