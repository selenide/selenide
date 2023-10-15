package com.codeborne.selenide.proxy;

import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.BasicAuthCredentials;
import io.netty.handler.codec.http.DefaultHttpRequest;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static com.codeborne.selenide.AuthenticationType.BEARER;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class AuthenticationFilterTest {
  private final AuthenticationFilter filter = new AuthenticationFilter();
  private final DefaultHttpRequest request = new DefaultHttpRequest(HTTP_1_1, GET, "/secured/page");
  private final HttpMessageContents contents = mock();
  private final HttpMessageInfo info = info("https://your.domain.com/secured/page");

  @Test
  void hasNoEffectByDefault() {
    filter.filterRequest(request, contents, info);

    assertThat(request.headers().entries().size()).isEqualTo(0);
  }

  @Test
  void canAddAuthentication() {
    filter.setAuthentication(BEARER, new BasicAuthCredentials("your.domain.com", "username", "password"));
    String expectedHeader = "Bearer " + Base64.getEncoder().encodeToString("username:password".getBytes(UTF_8));

    filter.filterRequest(request, contents, info);

    assertThat(request.headers().entries().size()).isEqualTo(2);
    assertThat(request.headers().entries().get(0).getKey()).isEqualTo("Authorization");
    assertThat(request.headers().entries().get(0).getValue()).isEqualTo(expectedHeader);

    assertThat(request.headers().entries().get(1).getKey()).isEqualTo("Proxy-Authorization");
    assertThat(request.headers().entries().get(1).getValue()).isEqualTo(expectedHeader);
  }

  @Test
  void canAddAuthentication_bearerToken() {
    filter.setAuthentication(BEARER, new BasicAuthCredentials("your.domain.com", "username", "password"));
    String expectedHeader = "Bearer dXNlcm5hbWU6cGFzc3dvcmQ="; // base64 of "username:password"

    filter.filterRequest(request, contents, info);

    assertThat(request.headers().entries().size()).isEqualTo(2);
    assertThat(request.headers().entries().get(0).getKey()).isEqualTo("Authorization");
    assertThat(request.headers().entries().get(0).getValue()).isEqualTo(expectedHeader);

    assertThat(request.headers().entries().get(1).getKey()).isEqualTo("Proxy-Authorization");
    assertThat(request.headers().entries().get(1).getValue()).isEqualTo(expectedHeader);
  }

  @Test
  void canRemoveAuthentication() {
    filter.setAuthentication(BEARER, new BasicAuthCredentials("domain", "username", "password"));
    filter.removeAuthentication();

    filter.filterRequest(request, contents, info);

    assertThat(request.headers().entries().size()).isEqualTo(0);
  }

  private HttpMessageInfo info(String url) {
    HttpMessageInfo messageInfo = mock();
    when(messageInfo.getUrl()).thenReturn(url);
    return messageInfo;
  }
}
