package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Selenide proxy filter allowing to mock any response
 * @since 6.9.0
 */
@ParametersAreNonnullByDefault
public class MockResponseFilter implements RequestFilter {
  private final Map<String, ResponseMock> mocks = new LinkedHashMap<>();

  /**
   * See {@link #mock(String, RequestMatcher, byte[])}
   */
  public void mock(String name, RequestMatcher requestMatcher, String mockedResponse) {
    mock(name, requestMatcher, mockedResponse.getBytes(UTF_8));
  }

  /**
   * Mock server response
   * @param name any unique string. Can be used to reset the mock.
   * @param requestMatcher criteria which requests to mock
   * @param mockedResponse the mocked response body (e.g. html or image)
   */
  public void mock(String name, RequestMatcher requestMatcher, byte[] mockedResponse) {
    if (mocks.containsKey(name)) {
      throw new IllegalArgumentException("Response filter already registered: " + name);
    }
    mocks.put(name, new ResponseMock(requestMatcher, mockedResponse));
  }

  /**
   * Remove the mock when it's not needed anymore.
   * @param name the unique name under which the mock was registered.
   */
  public void reset(String name) {
    if (mocks.remove(name) == null) {
      throw new IllegalArgumentException("Response filter was not registered: " + name);
    }
  }

  /**
   * Remove all mocks
   */
  public void reset() {
    mocks.clear();
  }

  @Override
  @Nullable
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    for (ResponseMock mock : mocks.values()) {
      if (mock.requestMatcher.match(request, contents, messageInfo)) {
        var response = new DefaultFullHttpResponse(request.protocolVersion(), OK, wrappedBuffer(mock.mockedResponse));
        response.headers().set("Content-Length", response.content().readableBytes());
        return response;
      }
    }

    return null;
  }

  private static final class ResponseMock {
    private final RequestMatcher requestMatcher;
    private final byte[] mockedResponse;

    private ResponseMock(RequestMatcher requestMatcher, byte[] mockedResponse) {
      this.requestMatcher = requestMatcher;
      this.mockedResponse = mockedResponse;
    }
  }
}
