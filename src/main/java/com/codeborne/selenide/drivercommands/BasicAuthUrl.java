package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
class BasicAuthUrl {
  @Nonnull
  String appendBasicAuthToURL(String url, @Nullable BasicAuthCredentials credentials) {
    if (credentials == null) {
      return url;
    }

    String domain = credentials.domain.isEmpty() ? "" : encode(credentials.domain) + "%5C";
    String login = credentials.login.isEmpty() ? "" : encode(credentials.login) + ':';
    String password = credentials.password.isEmpty() ? "" : encode(credentials.password) + "@";

    int index = url.indexOf("://");
    if (index < 0) {
      return domain + login + password + url;
    }

    return url.substring(0, index) + "://"
      + domain
      + login
      + password
      + url.substring(index + 3);
  }

  private static String encode(String value) {
    try {
      return URLEncoder.encode(value, UTF_8.name());
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
