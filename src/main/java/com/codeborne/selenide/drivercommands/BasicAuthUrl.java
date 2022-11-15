package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class BasicAuthUrl {
  @Nonnull
  String appendBasicAuthToURL(String url, @Nullable BasicAuthCredentials credentials) {
    if (credentials == null) {
      return url;
    }

    String domain = credentials.domain.isEmpty() ? "" : credentials.domain + "%5C";
    String login = credentials.login.isEmpty() ? "" : credentials.login + ':';
    String password = credentials.password.isEmpty() ? "" : credentials.password + "@";

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
