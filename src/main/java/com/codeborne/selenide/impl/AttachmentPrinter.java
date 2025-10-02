package com.codeborne.selenide.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AttachmentPrinter implements AttachmentHandler {

  private static final Logger log = LoggerFactory.getLogger(AttachmentPrinter.class);

  /**
   * Prints an attachment line to the console. This is recognised by at least
   * <a href="https://docs.gitlab.com/ci/testing/unit_test_reports/">GitLab Unit test reports</a>
   * and <a href="https://plugins.jenkins.io/junit-attachments/">Jenkins JUnit Attachments plugin</a>.
   *
   * @param file the file to be attached
   */
  @Override
  public void attach(File file) {
    log.info("[[ATTACHMENT|{}]]", file.getAbsolutePath());
  }
}
