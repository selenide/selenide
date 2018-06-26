package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpHelperTest {
  private HttpHelper helper = new HttpHelper();

  @Test
  void extractsFileNameFromHttpHeader() {
    Assertions.assertEquals("statement.xls", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=attachment; filename=statement.xls").get());

    Assertions.assertEquals("statement-40817810048000102279.pdf", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\"").get());

    Assertions.assertEquals("selenide-2.6.1.jar", helper.getFileNameFromContentDisposition(
      "content-disposition", "attachement; filename=selenide-2.6.1.jar").get());

    Assertions.assertEquals("selenide-4.11.5.md", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=selenide-4.11.5.md?sessioncookie=12345%2323").get());

    Assertions.assertEquals("random_file.txt", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachement; filename=random_file.txt?auth=5v1kij42xXSsc;charset=CP1251").get());

    Assertions.assertEquals("Prices.csv", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=Prices.csv;charset=UTF-8").get());

    Assertions.assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%D1%86%D0%B5%D0%BD%D1%8B%27.csv;charset=UTF-8").get());

    Assertions.assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename=%F6%E5%ED%FB%27.csv;charset=CP1251").get());

    Assertions.assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''%D1%86%D0%B5%D0%BD%D1%8B%27.csv").get());

    Assertions.assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=cp1251''%F6%E5%ED%FB%27.csv").get());

    Assertions.assertEquals("Naïve file.txt", helper.getFileNameFromContentDisposition(
      "Content-Disposition", "attachment; filename*=UTF-8''Na%C3%AFve%20file.txt").get());

    Assertions.assertEquals("файл-с-русским.txt", helper.getFileNameFromContentDisposition(
      "content-disposition",
      "attachement; filename=UTF-8''%D1%84%D0%B0%D0%B9%D0%BB-%D1%81-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%BC.txt")
      .get());
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsEmpty() {
    Assertions.assertFalse(helper.getFileNameFromContentDisposition("Content-Disposition", null).isPresent());
    Assertions.assertFalse(helper.getFileNameFromContentDisposition("Content-Disposition", "").isPresent());
  }

  @Test
  void fileNameIsNone_ifContentDispositionHeaderIsNotFound() {
    Assertions.assertFalse(helper.getFileNameFromContentDisposition("another-header", "some.png").isPresent());
  }
}
