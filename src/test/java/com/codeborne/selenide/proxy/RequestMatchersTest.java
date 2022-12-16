package com.codeborne.selenide.proxy;

import com.browserup.bup.util.HttpMessageInfo;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestMatchersTest {
  @Test
  void urlEqualsTo() {
    RequestMatcher matcher = RequestMatchers.urlEquals(RequestMatcher.HttpMethod.GET, "https://codeborne.com");
    assertThat(matcher.match(request(GET), null, info("https://codeborne.com"))).isTrue();
    assertThat(matcher.match(request(OPTIONS), null, info("https://codeborne.com"))).isTrue();
    assertThat(matcher.match(request(POST), null, info("https://codeborne.com"))).isFalse();
    assertThat(matcher.match(request(GET), null, info("https://codeborne.com/login"))).isFalse();
  }

  @Test
  void urlStartsWith() {
    RequestMatcher matcher = RequestMatchers.urlStartsWith(RequestMatcher.HttpMethod.POST, "https://selenide.org");
    assertThat(matcher.match(request(POST), null, info("https://selenide.org"))).isTrue();
    assertThat(matcher.match(request(POST), null, info("https://selenide.org/login"))).isTrue();
    assertThat(matcher.match(request(OPTIONS), null, info("https://selenide.org"))).isTrue();
    assertThat(matcher.match(request(GET), null, info("https://selenide.org"))).isFalse();
    assertThat(matcher.match(request(POST), null, info("https://selenide.com"))).isFalse();
    assertThat(matcher.match(request(POST), null, info("ftp://https://selenide.org"))).isFalse();
  }

  @Test
  void urlEndsWith() {
    RequestMatcher matcher = RequestMatchers.urlEndsWith(RequestMatcher.HttpMethod.PUT, "/auth/login");
    assertThat(matcher.match(request(PUT), null, info("https://selenide.org/auth/login"))).isTrue();
    assertThat(matcher.match(request(PUT), null, info("https://info.ee/auth/login"))).isTrue();
    assertThat(matcher.match(request(OPTIONS), null, info("https://info.ee/auth/login"))).isTrue();
    assertThat(matcher.match(request(GET), null, info("https://selenide.org/auth/login"))).isFalse();
    assertThat(matcher.match(request(POST), null, info("https://selenide.org/auth/login"))).isFalse();
    assertThat(matcher.match(request(PUT), null, info("https://selenide.org/auth/login/123"))).isFalse();
  }

  @Test
  void urlContains() {
    RequestMatcher matcher = RequestMatchers.urlContains(RequestMatcher.HttpMethod.DELETE, "/user/");
    assertThat(matcher.match(request(DELETE), null, info("https://auth.ee/user/123"))).isTrue();
    assertThat(matcher.match(request(OPTIONS), null, info("https://auth.ee/user/123"))).isTrue();
    assertThat(matcher.match(request(GET), null, info("https://auth.ee/user/123"))).isFalse();
    assertThat(matcher.match(request(DELETE), null, info("https://auth.ee/uzer/123"))).isFalse();
  }

  @Test
  void urlMatches() {
    RequestMatcher matcher = RequestMatchers.urlMatches(RequestMatcher.HttpMethod.PATCH, ".*/(\\w+)/(\\d+).*");
    assertThat(matcher.match(request(PATCH), null, info("https://auth.ee/user/123"))).isTrue();
    assertThat(matcher.match(request(PATCH), null, info("/user/123"))).isTrue();
    assertThat(matcher.match(request(PATCH), null, info("/user/123/"))).isTrue();
    assertThat(matcher.match(request(PATCH), null, info("/user/"))).isFalse();
    assertThat(matcher.match(request(PATCH), null, info("https://auth.ee/user/john"))).isFalse();
    assertThat(matcher.match(request(PATCH), null, info("https://auth.ee//456"))).isFalse();
  }

  private HttpRequest request(HttpMethod method) {
    HttpRequest request = mock();
    when(request.method()).thenReturn(method);
    return request;
  }

  private HttpMessageInfo info(String url) {
    HttpMessageInfo info = mock();
    when(info.getUrl()).thenReturn(url);
    return info;
  }
}
