package com.codeborne.selenide.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class HttpHelperTest implements WithAssertions {
  private HttpHelper helper = new HttpHelper();

  @Test
  void extractsFileNameFromHttpHeader() {
    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename=statement.xls"))
      .get()
      .isEqualTo("statement.xls");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\""))
      .get()
      .isEqualTo("statement-40817810048000102279.pdf");

    assertThat(helper.getFileNameFromContentDisposition(
      "content-disposition", "attachement; filename=selenide-2.6.1.jar"))
      .get()
      .isEqualTo("selenide-2.6.1.jar");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=selenide-4.11.5.md?sessioncookie=12345%2323"))
      .get()
      .isEqualTo("selenide-4.11.5.md");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=random_file.txt?auth=5v1kij42xXSsc;charset=CP1251"))
      .get()
      .isEqualTo("random_file.txt");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=Prices.csv;charset=UTF-8"))
      .get()
      .isEqualTo("Prices.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%D1%86%D0%B5%D0%BD%D1%8B%27.csv;charset=UTF-8"))
      .get()
      .isEqualTo("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%F6%E5%ED%FB%27.csv;charset=CP1251"))
      .get()
      .isEqualTo("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''%D1%86%D0%B5%D0%BD%D1%8B%27.csv"))
      .get()
      .isEqualTo("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=cp1251''%F6%E5%ED%FB%27.csv"))
      .get()
      .isEqualTo("цены'.csv");

    assertThat(helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''Na%C3%AFve%20file.txt"))
      .get()
      .isEqualTo("Naïve file.txt");

    assertThat(helper.getFileNameFromContentDisposition(
      "content-disposition",
      "attachement; filename=UTF-8''%D1%84%D0%B0%D0%B9%D0%BB-%D1%81-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%BC.txt"))
      .get()
      .isEqualTo("файл-с-русским.txt");
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsEmpty() {
    assertThat(helper.getFileNameFromContentDisposition("Content-Disposition", null).isPresent())
      .isFalse();
    assertThat(helper.getFileNameFromContentDisposition("Content-Disposition", "").isPresent())
      .isFalse();
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsNotFound() {
    assertThat(helper.getFileNameFromContentDisposition("another-header", "some.png").isPresent())
      .isFalse();
  }
}
