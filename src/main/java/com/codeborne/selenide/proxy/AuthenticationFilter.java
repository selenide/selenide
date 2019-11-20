package com.codeborne.selenide.proxy;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;

public class AuthenticationFilter implements RequestFilter {
  private AuthenticationType authenticationType;
  private Credentials credentials;

  @Override
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (authenticationType != null) {
      String authorization = String.format("%s %s", authenticationType.getValue(), credentials.encode());
      HttpHeaders headers = request.headers();
      headers.add("Authorization", authorization);
      headers.add("Proxy-Authorization", authorization);
    }
    return null;
  }

  public void setAuthentication(AuthenticationType authenticationType, Credentials credentials) {
    this.authenticationType = authenticationType;
    this.credentials = credentials;
  }

  public void removeAuthentication() {
    setAuthentication(null, null);
  }
}
