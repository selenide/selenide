package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.proxy.RequestMatcher.HttpMethod;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Selenide proxy filter allowing to mock any response
 *
 * @since 6.9.0
 */
@ParametersAreNonnullByDefault
public class MockResponseFilter implements RequestFilter {
  private final Map<String, ResponseMock> mocks = new LinkedHashMap<>();

  /**
   * See {@link #mockBytes(String, HttpMethod, RequestMatcher, Supplier)}
   */
  public void mockText(String name, HttpMethod method, RequestMatcher requestMatcher, Supplier<String> mockedResponse) {
    mockBytes(name, method, requestMatcher, () -> mockedResponse.get().getBytes(UTF_8));
  }

  /**
   * Mock server response
   *
   * @param name           any unique string. Can be used to reset the mock.
   * @param requestMatcher criteria which requests to mock
   * @param mockedResponse the mocked response body (e.g. html or image)
   */
  public void mockBytes(String name, HttpMethod method, RequestMatcher requestMatcher, Supplier<byte[]> mockedResponse) {
    if (mocks.containsKey(name)) {
      throw new IllegalArgumentException("Response filter already registered: " + name);
    }
    mocks.put(name, new ResponseMock(method, requestMatcher, mockedResponse));
  }

  /**
   * Remove the mock when it's not needed anymore.
   *
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
  public void resetAll() {
    mocks.clear();
  }

  @Override
  @Nullable
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    for (ResponseMock mock : mocks.values()) {
      if (mock.requestMatcher.match(request, contents, messageInfo)) {

        if (request.method().equals(OPTIONS)) {
          return mockOptionsRequest(request, mock);
        }

        if (request.method().name().equalsIgnoreCase(mock.method.name())) {
          return mockRequest(request, mock);
        }
      }
    }

    return null;
  }

  @Nonnull
  @CheckReturnValue
  private DefaultFullHttpResponse mockOptionsRequest(HttpRequest request, ResponseMock mock) {
    HttpHeaders headers = new DefaultHttpHeaders()
      .add("Content-Length", "0")
      .add("Access-Control-Allow-Methods", mock.method)
      .add("Access-Control-Allow-Headers", "*")
      .add("Access-Control-Allow-Origin", "*")
      .add("Access-Control-Max-Age", "0");

    return response(request, wrappedBuffer(new byte[0]), headers);
  }

  @Nonnull
  @CheckReturnValue
  private DefaultFullHttpResponse mockRequest(HttpRequest request, ResponseMock mock) {
    ByteBuf content = wrappedBuffer(mock.mockedResponse.get());
    HttpHeaders headers = new DefaultHttpHeaders()
      .add("Content-Length", content.readableBytes())
      .set("Access-Control-Allow-Origin", "*");

    return response(request, content, headers);
  }

  @Nonnull
  @CheckReturnValue
  private static DefaultFullHttpResponse response(HttpRequest request, ByteBuf content, HttpHeaders headers) {
    return new DefaultFullHttpResponse(request.protocolVersion(), OK, content, headers, EmptyHttpHeaders.INSTANCE);
  }

  @ParametersAreNonnullByDefault
  private static final class ResponseMock {
    private final HttpMethod method;
    private final RequestMatcher requestMatcher;
    private final Supplier<byte[]> mockedResponse;

    private ResponseMock(HttpMethod method, RequestMatcher requestMatcher, Supplier<byte[]> mockedResponse) {
      this.method = method;
      this.requestMatcher = requestMatcher;
      this.mockedResponse = mockedResponse;
    }
  }
}
