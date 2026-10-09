package com.codeborne.selenide.table;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ColumnResolverTest {
  @Test
  void resolvesTrimmedExactHeader() {
    assertThat(ColumnResolver.indexOf(List.of("Country", " Company \n"), "Company", "#t")).isEqualTo(1);
  }

  @Test
  void normalizesWhitespaceRuns() {
    assertThat(ColumnResolver.indexOf(List.of("Country", "Company  Name"), "Company Name", "#t")).isEqualTo(1);
    assertThat(ColumnResolver.indexOf(List.of("Country", "Company \n Name"), " Company Name", "#t")).isEqualTo(1);
  }

  @Test
  void isCaseSensitive() {
    assertThatThrownBy(() -> ColumnResolver.indexOf(List.of("company"), "Company", "#t"))
      .isInstanceOf(TableColumnException.class);
  }

  @Test
  void reportsMissingHeaderWithDisplayedHeaders() {
    assertThatThrownBy(() -> ColumnResolver.indexOf(List.of("Country", "Company"), "Region", "#t"))
      .isInstanceOf(TableColumnException.class)
      .hasMessage("Column \"Region\" not found in #t; displayed headers: [Country, Company]");
  }

  @Test
  void reportsAmbiguousHeader() {
    assertThatThrownBy(() -> ColumnResolver.indexOf(List.of("Country", "Company", "Company"), "Company", "#t"))
      .isInstanceOf(TableColumnException.class)
      .hasMessage("Column \"Company\" ambiguous in #t; displayed headers: [Country, Company, Company]");
  }
}
