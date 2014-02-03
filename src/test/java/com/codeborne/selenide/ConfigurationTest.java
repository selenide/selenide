package com.codeborne.selenide;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {
  @Before
  public void setUp() {
    System.setProperty("selenide.reportsUrl", "");
    System.setProperty("BUILD_URL", "");
  }

  @Test
  public void getsReportsUrlFromSystemProperty() {
    System.setProperty("selenide.reportsUrl", "http://ci.org/job/123/artifact/");
    assertEquals("http://ci.org/job/123/artifact/", Configuration.getReportsUrl());
  }

  @Test
  public void canConstructReportsUrlFromJenkinsProperty() {
    System.setProperty("BUILD_URL", "http://ci.org/job/123/");
    assertEquals("http://ci.org/job/123/artifact/", Configuration.getReportsUrl());
  }

  @After
  public void resetBuildUrl() {
    setUp();
  }
}
