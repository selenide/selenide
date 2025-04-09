package com.codeborne.selenide.impl;

import java.io.File;

public class AttachmentConsolePrinter {

  /**
   * Prints an attachment line to the console. This is recognised by at least
   * <a href="https://docs.gitlab.com/ci/testing/unit_test_reports/">GitLab Unit test reports</a>
   * and <a href="https://plugins.jenkins.io/junit-attachments/">Jenkins JUnit Attachments plugin</a>.
   *
   * @param file the file to be attached
   */
  public static void printAttachmentLine(File file) {
    System.out.printf("[[ATTACHMENT|%s]]%n", file.getAbsolutePath());
  }
}
