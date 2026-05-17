package integration.proxy;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultiFileDownloadViaProxyTest extends ProxyIntegrationTest {

  @Test
  void downloadsAllThreeFiles() {
    openFile("downloadMultipleFiles.html");

    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(PROXY).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
    assertThat(downloaded.stream().map(File::getParentFile).distinct().toList())
      .as("all files archived into one folder")
      .hasSize(1);
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    openFile("downloadMultipleFiles.html");

    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(PROXY).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files, but found 3");
  }
}
