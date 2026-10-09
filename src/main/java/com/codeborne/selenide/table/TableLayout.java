package com.codeborne.selenide.table;

import org.openqa.selenium.By;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Locators describing the structure of a table-like element.
 *
 * @param rows locator of data rows, relative to the table root
 * @param cells locator of cells, relative to a row
 * @param headers locator of header cells, relative to the table root
 * @since 7.19.0
 */
public record TableLayout(By rows, By cells, By headers) {
  private static final String XPATH_PREFIX = "By.xpath: ";

  /**
   * @since 7.19.0
   */
  public TableLayout {
    requireNonNull(rows, "rows");
    requireNonNull(cells, "cells");
    requireNonNull(headers, "headers");
  }

  /**
   * Layout of a classic HTML {@code <table>} with headers in {@code <thead>} and data rows in {@code <tbody>}.
   * Rows of nested tables are ignored.
   *
   * @since 7.19.0
   */
  public static TableLayout html() {
    return new TableLayout(
      By.xpath("./tbody/tr[td]"),
      By.xpath("./*[self::td or self::th]"),
      By.xpath("./thead/tr/th"));
  }

  /**
   * Layout of an ARIA grid or table using roles {@code row}, {@code cell}/{@code gridcell}/{@code rowheader}
   * and {@code columnheader}.
   *
   * @since 7.19.0
   */
  public static TableLayout aria() {
    return new TableLayout(
      By.xpath(".//*[@role='row'][*[@role='cell' or @role='gridcell']]"),
      By.xpath("./*[@role='cell' or @role='gridcell' or @role='rowheader']"),
      By.xpath(".//*[@role='columnheader']"));
  }

  /**
   * Custom layout.
   *
   * @param rows locator of data rows, relative to the table root
   * @param cells locator of cells, relative to a row
   * @param headers locator of header cells, relative to the table root
   * @since 7.19.0
   */
  public static TableLayout of(By rows, By cells, By headers) {
    return new TableLayout(rows, cells, headers);
  }

  static Optional<String> xpath(By by) {
    String description = by.toString();
    if (description.startsWith(XPATH_PREFIX)) {
      return Optional.of(description.substring(XPATH_PREFIX.length()));
    }
    return Optional.empty();
  }
}
