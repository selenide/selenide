package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

final class FileNamerTest {
  private final FileNamer fileNamer = new FileNamer();

  @Test
  void fileNameContainsTimestampPidThreadIdAndCounter() {
    assertThat(fileNamer.generateFileName()).matches("\\d+_\\d+_\\d+_\\d+");
  }

  @Test
  void generatesUniqueNamesEvenWithinTheSameMillisecond() {
    Set<String> names = ConcurrentHashMap.newKeySet();
    IntStream.range(0, 1000).parallel().forEach(i -> names.add(fileNamer.generateFileName()));
    assertThat(names).hasSize(1000);
  }
}
