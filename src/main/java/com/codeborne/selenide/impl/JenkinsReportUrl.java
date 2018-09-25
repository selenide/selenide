package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;

import java.util.logging.Logger;

public class JenkinsReportUrl {
  private static final Logger LOG = Logger.getLogger(SelenideConfig.class.getName());

  public String getReportsUrl(String reportsUrl) {
    if (isEmpty(reportsUrl)) {
      reportsUrl = getJenkinsReportsUrl();
      if (isEmpty(reportsUrl)) {
        LOG.config("Variable selenide.reportsUrl not found");
      }
    }
    else {
      LOG.config("Using variable selenide.reportsUrl=" + reportsUrl);
    }
    return reportsUrl;
  }

  private boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  private String getJenkinsReportsUrl() {
    String build_url = System.getProperty("BUILD_URL");
    if (!isEmpty(build_url)) {
      LOG.config("Using Jenkins BUILD_URL: " + build_url);
      return build_url + "artifact/";
    }
    else {
      LOG.config("No BUILD_URL variable found. It's not Jenkins.");
      return null;
    }
  }
}
