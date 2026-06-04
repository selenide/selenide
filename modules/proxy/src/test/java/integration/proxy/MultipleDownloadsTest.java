package integration.proxy;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.impl.FileContent;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.DownloadOptions.files;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultipleDownloadsTest extends ProxyIntegrationTest {
  @BeforeEach
  final void setUp() {
    timeout = 1;
    openFile("downloadMultipleFiles.html");
  }

  @ParameterizedTest
  @ValueSource(strings = {"empty.html", "hello_world.txt", "download.html"})
  void downloadMultipleFiles(String fileName) {
    File text = $("#multiple-downloads").download(using(PROXY).withTimeout(4000).withName(fileName));

    assertThat(text.getName()).isEqualTo(fileName);
    assertThat(text.length()).isEqualTo(new FileContent(fileName).content().length());
  }

  @ParameterizedTest
  @ValueSource(strings = {"empty.html", "hello_world.txt", "download.html"})
  void downloadMultipleFilesWithoutContent(String fileName) {
    File text = $("#multiple-downloads").download(file().withMethod(PROXY).withTimeout(4000).withName(fileName).withoutContent());

    assertThat(text.getName()).isEqualTo(fileName);
    assertThat(text).content().isEqualTo("Mocked file content");
  }

  @Test
  void downloadMultipleFilesAtOnce() {
    Collection<File> result = $("#multiple-downloads").downloadFiles(
      files(2).withMethod(PROXY).withExtension("html").withTimeout(4000)
    );

    Map<String, File> files = result.stream().collect(toMap(File::getName, file -> file));
    assertThat(files).hasSizeGreaterThanOrEqualTo(2);
    assertThat(files.keySet()).containsExactlyInAnyOrder("empty.html", "download.html");

    assertThat(files.get("empty.html")).content().isEqualToIgnoringNewLines(new FileContent("empty.html").content());
    assertThat(files.get("download.html")).content().isEqualToIgnoringNewLines(new FileContent("download.html").content());
  }

  @Test
  void downloadMultipleFiles_errorMessage() {
    assertThatThrownBy(() -> $("#multiple-downloads").downloadFiles(files(2).withMethod(PROXY).withExtension("txt").withTimeout(100)))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download at least 2 files with extension \"txt\" in 100ms (found ")
      .hasMessageContaining("empty.html")
      .hasMessageContaining("download.html")
      .hasMessageContaining("hello_world.txt")
      .hasMessageContaining("Timeout: 100ms");
  }
}
