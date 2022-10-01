package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.net.URISyntaxException;

@ParametersAreNonnullByDefault
public class AuthenticationFilter implements RequestFilter {
  private AuthenticationType authenticationType;
  private Credentials credentials;

  @Override
  @Nullable
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (authenticationType != null && needsHeader(messageInfo.getUrl())) {
      String authorization = String.format("%s %s", authenticationType.getValue(), credentials.encode());
      HttpHeaders headers = request.headers();
      headers.add("Authorization", authorization);
      headers.add("Proxy-Authorization", authorization);
    }
    return null;
  }

  boolean needsHeader(String url) {
    if (credentials.domain().isEmpty()) {
      // support deprecated methods with empty domain
      // Remove this if before releasing Selenide 7.0.0
      return true;
    }
    String host = getHostname(url);
    return host != null && host.equalsIgnoreCase(credentials.domain());
  }

  String getHostname(String url) {
    try {
      return new URI(url).getHost();
    }
    catch (URISyntaxException invalidUri) {
      return url;
    }
  }

  public void setAuthentication(@Nullable AuthenticationType authenticationType, @Nullable Credentials credentials) {
    this.authenticationType = authenticationType;
    this.credentials = credentials;
  }

  public void removeAuthentication() {
    setAuthentication(null, null);
  }
}
