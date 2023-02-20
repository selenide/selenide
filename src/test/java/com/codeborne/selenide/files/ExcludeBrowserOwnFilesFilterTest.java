package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class ExcludeBrowserOwnFilesFilterTest {
  private final ExcludeBrowserOwnFilesFilter filter = new ExcludeBrowserOwnFilesFilter();

  @Test
  void ignoresChromeOwnFiles() {
    assertThat(filter.match(new File(".com.google.Chrome.byNFj9"))).isFalse();
  }

  @Test
  void ignoresEdgeOwnFiles() {
    assertThat(filter.match(new File(".com.microsoft.edgemac.RlETth"))).isFalse();
  }

  @Test
  void acceptsAllOtherFiles() {
    assertThat(filter.match(new File("hello_world.txt"))).isTrue();
    assertThat(filter.match(new File("hello_world.txt.crdownload"))).isTrue();
    assertThat(filter.match(new File(""))).isTrue();
  }

  @Test
  void negation() {
    assertThat(filter.notMatch(new File("hello_world.txt"))).isTrue();
    assertThat(filter.notMatch(new File("hello_world.txt.crdownload"))).isTrue();
    assertThat(filter.notMatch(new File(""))).isTrue();

    assertThat(filter.notMatch(new File(".com.microsoft.edgemac.RlETth"))).isTrue();
    assertThat(filter.notMatch(new File(".com.google.Chrome.gsqrna"))).isTrue();
  }
}
