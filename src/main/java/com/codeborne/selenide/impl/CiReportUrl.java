package com.codeborne.selenide.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@ParametersAreNonnullByDefault
public class CiReportUrl {
  private static final Logger log = LoggerFactory.getLogger(CiReportUrl.class);

  @CheckReturnValue
  @Nullable
  public String getReportsUrl(@Nullable String reportsUrl) {
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

  @Nullable
  private String getTeamCityUrl() {
    String url = System.getProperty("teamcity.serverUrl");
    String build_type = System.getProperty("teamcity.buildType.id");
    String build_number = System.getProperty("build.number");
    if (isEmpty(build_type) || isEmpty(build_number) || isEmpty(url)) {
      return null;
    }
    return resolveUrlSource("%s/repository/download/%s/%s:id/", url, build_type, build_number);
  }

  @Nullable
  private String getJenkinsReportsUrl() {
    String build_url = System.getProperty("BUILD_URL");
    if (!isEmpty(build_url)) {
      String workspace = System.getProperty("WORKSPACE", System.getenv("WORKSPACE"));
      String reportRelativePath = "";
      if (!isEmpty(workspace)) { // we have a workspace folder. Calculate the report relative path
        Path pathAbsoluteReportsFolder = Paths.get("").normalize().toAbsolutePath();
        Path pathAbsoluteWorkSpace = Paths.get(workspace).normalize().toAbsolutePath();
        Path pathRelative = pathAbsoluteWorkSpace.relativize(pathAbsoluteReportsFolder);
        reportRelativePath = pathRelative.toString().replace('\\', '/') + '/';
      }
      return resolveUrlSource("%s/artifact/%s", build_url, reportRelativePath);
    } else {
      return null;
    }
  }

  @Nullable
  private String resolveUrlSource(String base, Object... format) {
    if (format.length != 0) {
      base = String.format(base, format);
    }
    try {
      return new URI(base).normalize().toURL().toString();
    } catch (Exception e) {
      log.error("Variable selenide.reportsUrl is incorrect: {}", base, e);
      return null;
    }
  }

  private boolean isEmpty(@Nullable String s) {
    return s == null || s.trim().isEmpty();
  }
}
