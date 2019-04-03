package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CiReportUrl {
  private static final Logger log = LoggerFactory.getLogger(SelenideConfig.class);

  public String getReportsUrl(String reportsUrl) {
    if (!isEmpty(reportsUrl)) {
      log.debug("Using variable selenide.reportsUrl={}", reportsUrl);
      return resolveUrlSource(reportsUrl);
    }
    reportsUrl = getJenkinsReportsUrl();
    if (!isEmpty(reportsUrl)) {
      log.debug("Using Jenkins BUILD_URL: {}", reportsUrl);
      return reportsUrl;
    }
    reportsUrl = getTeamCityUrl();
    if (!isEmpty(reportsUrl)) {
      log.debug("Using Teamcity artifacts url: {}", reportsUrl);
      return reportsUrl;
    }
    log.debug("Variable selenide.reportsUrl not found");
    return reportsUrl;
  }

  private String getTeamCityUrl() {
    String url = System.getProperty("teamcity.serverUrl");
    String build_type = System.getProperty("teamcity.buildType.id");
    String build_number = System.getProperty("build.number");
    if (isEmpty(build_type) || isEmpty(build_number) || isEmpty(url)) {
      return null;
    }
    return resolveUrlSource("%s/repository/download/%s/%s:id/", url, build_type, build_number);
  }

  private String getJenkinsReportsUrl() {
    String build_url = System.getProperty("BUILD_URL");
    if (!isEmpty(build_url)) {
      return resolveUrlSource("%s/artifact/", build_url);
    } else {
      return null;
    }
  }

  private String resolveUrlSource(String base, Object... format) {
    if (format.length != 0) {
      base = String.format(base, format);
    }
    try {
      return new URI(base).normalize().toURL().toString();
    } catch (Exception e) {
      log.error("Variable selenide.reportsUrl is incorrect: " + base, e);
      return null;
    }
  }

  private boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }
}
