package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DownloadFileWithHttpRequestTest {
  DownloadFileWithHttpRequest d = new DownloadFileWithHttpRequest();

  @Test
  public void extractsFileNameFromHttpHeader() {
    assertEquals("statement.xls", d.getFileNameFromContentDisposition(
        "Content-Disposition", "Content-Disposition=attachment; filename=statement.xls"));

    assertEquals("statement-40817810048000102279.pdf", d.getFileNameFromContentDisposition(
        "Content-Disposition", "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\""));

    assertEquals("selenide-2.6.1.jar", d.getFileNameFromContentDisposition(
        "content-disposition", "attachement; filename=selenide-2.6.1.jar"));
  }
}
