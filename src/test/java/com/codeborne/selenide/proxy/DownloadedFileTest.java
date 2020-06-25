package com.codeborne.selenide.proxy;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;

class DownloadedFileTest {
  @Test
  void twoFilesAreEqual_ifNameAndModificationTimeEqual() throws IOException {
    File theFile = new File("build/downloads/some.txt");
    writeStringToFile(theFile, "some content", UTF_8);
    DownloadedFile file1 = new DownloadedFile(theFile, emptyMap());
    DownloadedFile file2 = new DownloadedFile(theFile, emptyMap());

    assertThat(file1).isEqualTo(file2);
  }

  @Test
  void twoFilesAreDifferent_ifOneOverridesOther() throws IOException {
    File theFile = new File("build/downloads/some.txt");
    writeStringToFile(theFile, "first content", UTF_8);
    DownloadedFile file1 = new DownloadedFile(theFile, emptyMap());

    theFile.setLastModified(System.currentTimeMillis() - 1001);
    DownloadedFile file2 = new DownloadedFile(theFile, emptyMap());

    assertThat(file1).isNotEqualTo(file2);
  }
}
