package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.Credentials;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Locale.ROOT;

@ParametersAreNonnullByDefault
public class BasicAuthUtils {
  @Nonnull
  static String appendBasicAuthToURL(String url, @Nullable BasicAuthCredentials credentials) {
    if (credentials == null) {
      return url;
    }

    String login = credentials.login.isEmpty() ? "" : encode(credentials.login) + ':';
    String password = credentials.password.isEmpty() ? "" : encode(credentials.password) + "@";

    int index = url.indexOf("://");
    return index < 0 ?
      login + password + url :
      url.substring(0, index) + "://" + login + password + url.substring(index + 3);

  }

  @Nonnull
  @CheckReturnValue
  private static String encode(String value) {
    try {
      return URLEncoder.encode(value, UTF_8.name());
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @CheckReturnValue
  public static boolean registerBasicAuth(WebDriver webDriver, @Nullable Credentials credentials) {
    if (webDriver instanceof HasAuthentication hasAuthentication && credentials instanceof BasicAuthCredentials basicAuth) {
      hasAuthentication.register(
        uri -> uriMatchesDomain(uri, basicAuth.domain()),
        UsernameAndPassword.of(basicAuth.login, basicAuth.password)
      );
      return true;
    }
    return false;
  }

  @CheckReturnValue
  public static boolean uriMatchesDomain(String url, String domain) {
    return hostMatchesDomain(getHostname(url), domain);
  }

  @CheckReturnValue
  static boolean uriMatchesDomain(URI uri, String domain) {
    return hostMatchesDomain(uri.getHost(), domain);
  }

  private static boolean hostMatchesDomain(@Nullable String host, String domain) {
    if (domain.isEmpty()) {
      // support deprecated methods with empty domain
      // Remove this if before releasing Selenide 7.0.0
      return true;
    }
    if (host == null) return false;

    List<String> domains = asList(domain.toLowerCase(ROOT).split("[,|]"));
    return domains.contains(host.toLowerCase(ROOT));
  }

  @Nullable
  @CheckReturnValue
  static String getHostname(String url) {
    try {
      return new URI(url).getHost();
    }
    catch (URISyntaxException invalidUri) {
      return url;
    }
  }
}
