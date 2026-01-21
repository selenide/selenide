package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadedFile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class DownloadsFolderTest {
  @Test
  void filesExcept() {
    DownloadedFile f1 = fileWithName("hello.txt");
    DownloadedFile f2 = fileWithName("goodbye.txt");
    DownloadedFile f3 = fileWithName("report.pdf");

    DummyDownloadsFolder folder = new DummyDownloadsFolder(List.of(f1, f2, f3));

    assertThat(folder.filesExcept(emptyList())).containsExactly(f1, f2, f3);
    assertThat(folder.filesExcept(List.of(f1))).containsExactly(f2, f3);
    assertThat(folder.filesExcept(List.of(f2))).containsExactly(f1, f3);
    assertThat(folder.filesExcept(List.of(f3))).containsExactly(f1, f2);
    assertThat(folder.filesExcept(List.of(f2, f3))).containsExactly(f1);
    assertThat(folder.filesExcept(List.of(f1, f2, f3))).isEmpty();
  }

  @Test
  void filesExcept_emptyFolder() {
    DummyDownloadsFolder folder = new DummyDownloadsFolder(emptyList());

    assertThat(folder.filesExcept(emptyList())).isEmpty();
    assertThat(folder.filesExcept(List.of())).isEmpty();
    assertThat(folder.filesExcept(List.of(fileWithName("hello.txt")))).isEmpty();
  }

  private record DummyDownloadsFolder(
    List<DownloadedFile> files
  ) implements DownloadsFolder {

    @Override
    public void cleanupBeforeDownload() {
    }

    @Override
    public void deleteIfEmpty() {
    }

    @Override
    public String getPath() {
      return "/tmp";
    }
  }
}
