package com.codeborne.selenide;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class SelenideConfigTest implements WithAssertions {
  @Test
  void getsReportsUrlFromSystemProperty() {
    System.setProperty("selenide.reportsUrl", "http://ci.org/job/123/artifact/");
    assertThat(new SelenideConfig().reportsUrl()).isEqualTo("http://ci.org/job/123/artifact/");
  }

  @Test
  void canConstructReportsUrlFromJenkinsProperty() {
    System.setProperty("BUILD_URL", "http://ci.org/job/123/");
    assertThat(new SelenideConfig().reportsUrl()).isEqualTo("http://ci.org/job/123/artifact/");
  }

  @Test
  void canConstructReportsUrlFromTeamcityProperty() {
    System.setProperty("teamcity.serverUrl", "http://ci.org/");
    System.setProperty("teamcity.buildType.id", "my-build");
    System.setProperty("build.number", "1");
    assertThat(new SelenideConfig().reportsUrl()).isEqualTo("http://ci.org/repository/download/my-build/1:id/");
  }

  @BeforeEach
  @AfterEach
  void setUp() {
    System.setProperty("selenide.reportsUrl", "");
    System.setProperty("BUILD_URL", "");
    System.setProperty("teamcity.serverUrl", "");
    System.setProperty("teamcity.buildType.id", "");
    System.setProperty("build.number", "");
  }
}
