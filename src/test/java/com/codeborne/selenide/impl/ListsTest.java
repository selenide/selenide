package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ListsTest {
  @Test
  void composesNonEmptyListFromVarargs() {
    assertThat(Lists.list("trips")).isEqualTo(singletonList("trips"));
    assertThat(Lists.list("trips", "traps")).isEqualTo(asList("trips", "traps"));
    assertThat(Lists.list("trips", "traps", "trull")).isEqualTo(asList("trips", "traps", "trull"));
  }
}
