package integration;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MultiFileDownloadToFolderTest extends IntegrationTest {

  @BeforeEach
  void openPage() {
    openFile("downloadMultipleFiles.html");
  }

  @Test
  void downloadsAllThreeFiles() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(FOLDER).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).hasSize(3);
    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
    assertThat(downloaded.stream().map(File::getParentFile).distinct().toList())
      .as("all files archived into one folder")
      .hasSize(1);
  }

  @Test
  void filterNarrowsCount() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(2).withMethod(FOLDER).withExtension("html").withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html");
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(FOLDER).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }

  @Test
  void failsWhenFewerFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(5).withMethod(FOLDER).withTimeout(ofSeconds(2))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Failed to download 5 files");
  }
}
