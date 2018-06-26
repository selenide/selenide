package com.codeborne.selenide;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
  @Test
  void getsReportsUrlFromSystemProperty() {
    System.setProperty("selenide.reportsUrl", "http://ci.org/job/123/artifact/");
    Assertions.assertEquals("http://ci.org/job/123/artifact/", Configuration.getReportsUrl());
  }

  @Test
  void canConstructReportsUrlFromJenkinsProperty() {
    System.setProperty("BUILD_URL", "http://ci.org/job/123/");
    Assertions.assertEquals("http://ci.org/job/123/artifact/", Configuration.getReportsUrl());
  }

  @AfterEach
  void resetBuildUrl() {
    setUp();
  }

  @BeforeEach
  void setUp() {
    System.setProperty("selenide.reportsUrl", "");
    System.setProperty("BUILD_URL", "");
  }
}
