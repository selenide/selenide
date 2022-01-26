package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class BasicAuthUrl {
  @Nonnull
  String appendBasicAuthToURL(String url, @Nullable BasicAuthCredentials credentials) {
    String login = "";
    String password = "";
    String domain = "";

    if (credentials != null) {
      if (!credentials.domain.isEmpty()) domain = credentials.domain + "%5C";
      if (!credentials.login.isEmpty()) login = credentials.login + ":";
      if (!credentials.password.isEmpty()) password = credentials.password + "@";
    }
    int index = url.indexOf("://") + 3;
    if (index < 3) {
      return domain + login + password + url;
    }

    return url.substring(0, index - 3) + "://"
      + domain
      + login
      + password
      + url.substring(index);
  }
}
