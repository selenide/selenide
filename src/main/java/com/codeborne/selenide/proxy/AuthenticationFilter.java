package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.drivercommands.BasicAuthUtils.uriMatchesDomain;
import static java.util.Objects.requireNonNull;

public class AuthenticationFilter implements RequestFilter {
  @Nullable
  private AuthenticationType authenticationType;

  @Nullable
  private Credentials credentials;

  @Nullable
  @Override
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    validate();
    if (authenticationType != null && uriMatchesDomain(messageInfo.getUrl(), requireNonNull(credentials).domain())) {
      String authorization = String.format("%s %s", authenticationType.getValue(), credentials.encode());
      HttpHeaders headers = request.headers();
      headers.add("Authorization", authorization);
      headers.add("Proxy-Authorization", authorization);
    }
    return null;
  }

  public void setAuthentication(@Nullable AuthenticationType authenticationType, @Nullable Credentials credentials) {
    this.authenticationType = authenticationType;
    this.credentials = credentials;
    validate();
  }

  public void removeAuthentication() {
    setAuthentication(null, null);
  }

  private void validate() {
    if (authenticationType != null && credentials == null) {
      throw new IllegalArgumentException("Authentication type is provided, but credentials not provided");
    }
    if (authenticationType == null && credentials != null) {
      throw new IllegalArgumentException("Credentials are provided, but authentication type not provided");
    }
  }
}
