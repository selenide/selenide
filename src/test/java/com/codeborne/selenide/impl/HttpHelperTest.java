package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HttpHelperTest {
  HttpHelper helper = new HttpHelper();

  @Test
  public void extractsFileNameFromHttpHeader() {
    assertEquals("statement.xls", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "Content-Disposition=attachment; filename=statement.xls").get());

    assertEquals("statement-40817810048000102279.pdf", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\"").get());

    assertEquals("selenide-2.6.1.jar", helper.getFileNameFromContentDisposition(
        "content-disposition", "attachement; filename=selenide-2.6.1.jar").get());

    assertEquals("selenide-4.11.5.md", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachement; filename=selenide-4.11.5.md?sessioncookie=12345%2323").get());

    assertEquals("random_file.txt", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachement; filename=random_file.txt?auth=5v1kij42xXSsc;charset=CP1251").get());

    assertEquals("Prices.csv", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename=Prices.csv;charset=UTF-8").get());

    assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename=%D1%86%D0%B5%D0%BD%D1%8B%27.csv;charset=UTF-8").get());

    assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename=%F6%E5%ED%FB%27.csv;charset=CP1251").get());

    assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename*=UTF-8''%D1%86%D0%B5%D0%BD%D1%8B%27.csv").get());

    assertEquals("цены'.csv", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename*=cp1251''%F6%E5%ED%FB%27.csv").get());

    assertEquals("Naïve file.txt", helper.getFileNameFromContentDisposition(
        "Content-Disposition", "attachment; filename*=UTF-8''Na%C3%AFve%20file.txt").get());

    assertEquals("файл-с-русским.txt", helper.getFileNameFromContentDisposition(
        "content-disposition",
        "attachement; filename=UTF-8''%D1%84%D0%B0%D0%B9%D0%BB-%D1%81-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%BC.txt")
        .get());
  }

  @Test
  public void fileNameIsNone_ifContentDispositionHeaderIsEmpty() {
    assertFalse(helper.getFileNameFromContentDisposition("Content-Disposition", null).isPresent());
    assertFalse(helper.getFileNameFromContentDisposition("Content-Disposition", "").isPresent());
  }

  @Test
  public void fileNameIsNone_ifContentDispositionHeaderIsNotFound() {
    assertFalse(helper.getFileNameFromContentDisposition("another-header", "some.png").isPresent());
  }
}
