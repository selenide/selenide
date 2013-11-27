package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileDownloaderTest {
  FileDownloader d = new FileDownloader();

  @Test
  public void extractsFileNameFromHttpHeader() {
    assertEquals("statement.xls", d.getFileNameFromContentDisposition(
        "Content-Disposition=attachment; filename=statement.xls"));

    assertEquals("statement-40817810048000102279.pdf", d.getFileNameFromContentDisposition(
        "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\""));
  }
}
