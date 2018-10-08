package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CiReportUrl {
  private static final Logger LOG = Logger.getLogger(SelenideConfig.class.getName());

  public String getReportsUrl(String reportsUrl) {
    if (!isEmpty(reportsUrl)) {
      LOG.config("Using variable selenide.reportsUrl=" + reportsUrl);
      return resolveUrlSource(reportsUrl);
    }
    reportsUrl = getJenkinsReportsUrl();
    if (!isEmpty(reportsUrl)) {
      LOG.config("Using Jenkins BUILD_URL: " + reportsUrl);
      return reportsUrl;
    }
    reportsUrl = getTeamCityUrl();
    if (!isEmpty(reportsUrl)) {
      LOG.config("Using Teamcity artifacts url: " + reportsUrl);
      return reportsUrl;
    }
    LOG.config("Variable selenide.reportsUrl not found");
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
      LOG.log(Level.ALL, "Variable selenide.reportsUrl is incorrect: " + base, e);
      return null;
    }
  }

  private boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }
}
