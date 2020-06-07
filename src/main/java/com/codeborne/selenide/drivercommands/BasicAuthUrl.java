package com.codeborne.selenide.drivercommands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class BasicAuthUrl {
  @Nonnull
  String appendBasicAuthToURL(String url, String domain, String login, String password) {
    if (!domain.isEmpty()) domain += "%5C";
    if (!login.isEmpty()) login += ":";
    if (!password.isEmpty()) password += "@";
    int index = url.indexOf("://") + 3;
    if (index < 3) return domain + login + password + url;

    return url.substring(0, index - 3) + "://"
      + domain
      + login
      + password
      + url.substring(index);
  }
}
