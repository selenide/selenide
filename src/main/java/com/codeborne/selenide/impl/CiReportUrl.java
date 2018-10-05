package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;

import java.util.logging.Logger;

public class CiReportUrl {
  private static final Logger LOG = Logger.getLogger(SelenideConfig.class.getName());

  public String getReportsUrl(String reportsUrl) {
    if (!isEmpty(reportsUrl)) {
      LOG.config("Using variable selenide.reportsUrl=" + reportsUrl);
      return reportsUrl;
    }
    reportsUrl = getJenkinsReportsUrl();
    if (!isEmpty(reportsUrl)) {
      LOG.config("Using Jenkins BUILD_URL: " + reportsUrl);
      return reportsUrl;
    }
    reportsUrl = getTeamCityUrl();
    if (!isEmpty(reportsUrl)) {
      LOG.config("Not found one of [teamcity.serverUrl,teamcity.buildType.id,build.number]. It's not Teamcity.");
      return reportsUrl;
    }
    LOG.config("Variable selenide.reportsUrl not found");
    return reportsUrl;
  }

  private String getTeamCityUrl() {
    String url = System.getProperty("teamcity.serverUrl");
    String build_type = System.getProperty("teamcity.buildType.id");
    String build_number = System.getProperty("build.number");
    String result = url + "/repository/download/" + build_type + "/" + build_number + ":id/";
    if (isEmpty(build_type) || isEmpty(build_number) || isEmpty(result)) {
      return null;
    } else {
      LOG.config("Using teamcity artifacts: " + result);
      return result;
    }
  }

  private String getJenkinsReportsUrl() {
    String build_url = System.getProperty("BUILD_URL");
    if (!isEmpty(build_url)) {
      return build_url + "artifact/";
    } else {
      return null;
    }
  }

  private boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }
}
