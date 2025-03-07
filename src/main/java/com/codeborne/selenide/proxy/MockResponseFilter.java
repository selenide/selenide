package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.remote.http.Contents;
import org.openqa.selenium.remote.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpResponseStatus.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Selenide proxy filter allowing to mock any response
 */
public class MockResponseFilter implements RequestFilter {
  private static final int HTTP_STATUS_OK = 200;
  private static final byte[] EMPTY_RESPONSE = new byte[0];

  private final Map<String, ResponseMock> mocks = new LinkedHashMap<>();

  /**
   * See {@link #mockBytes(String, RequestMatcher, Supplier)}
   */
  public void mockText(String name, RequestMatcher requestMatcher, Supplier<String> mockedResponse) {
    mockBytes(name, requestMatcher, () -> mockedResponse.get().getBytes(UTF_8));
  }

  /**
   * See {@link #mockBytes(String, RequestMatcher, int, Supplier)}
   */
  public void mockText(String name, RequestMatcher requestMatcher, int status, Supplier<String> mockedResponse) {
    mockBytes(name, requestMatcher, status, () -> mockedResponse.get().getBytes(UTF_8));
  }

  /**
   * See {@link #mockBytes(String, RequestMatcher, int, Supplier)}
   */
  public void mockBytes(String name, RequestMatcher requestMatcher, Supplier<byte[]> mockedResponse) {
    mockBytes(name, requestMatcher, HTTP_STATUS_OK, mockedResponse);
  }

  /**
   * Mock server response
   *
   * @param name           Name of this "mocker". Must be a unique string (you are not allowed to add two mockers with the same name).
   *                       Can be used to reset the mock after the test.
   *                       Added to every http response as header "X-Mocked-By".
   * @param requestMatcher criteria which requests to mock
   * @param mockedResponse the mocked response body (e.g. html or image)
   */
  public void mockBytes(String name, RequestMatcher requestMatcher, int status, Supplier<byte[]> mockedResponse) {
    mockResponse(name, requestMatcher, () -> new HttpResponse()
      .setStatus(status)
      .setContent(Contents.bytes(mockedResponse.get())));
  }

  public void mockResponse(String name, RequestMatcher requestMatcher, Supplier<HttpResponse> mockedResponse) {
    if (mocks.containsKey(name)) {
      throw new IllegalArgumentException("Response filter already registered: " + name);
    }
    mocks.put(name, new ResponseMock(name, requestMatcher, mockedResponse));
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
  public io.netty.handler.codec.http.@Nullable HttpResponse filterRequest(
    HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo
  ) {
    for (ResponseMock mock : mocks.values()) {
      if (mock.requestMatcher.match(request, contents, messageInfo)) {
        return request.method().equals(OPTIONS) ?
          mockOptionsRequest(request, mock) :
          mockRequest(request, mock);
      }
    }

    return null;
  }

  private DefaultFullHttpResponse mockOptionsRequest(HttpRequest request, ResponseMock mock) {
    HttpHeaders headers = new DefaultHttpHeaders()
      .add("Content-Length", "0")
      .add("Access-Control-Allow-Methods", "*")
      .add("Access-Control-Allow-Headers", "*")
      .add("Access-Control-Allow-Origin", "*")
      .add("Access-Control-Max-Age", "0")
      .add("X-Mocked-By", mock.name);

    return response(request, HTTP_STATUS_OK, wrappedBuffer(EMPTY_RESPONSE), headers);
  }

  private DefaultFullHttpResponse mockRequest(HttpRequest request, ResponseMock mock) {
    HttpResponse httpResponse = mock.mockedResponse.get();
    HttpHeaders headers = new DefaultHttpHeaders()
      .add("Content-Length", httpResponse.getContent().length())
      .set("Access-Control-Allow-Origin", "*")
      .add("X-Mocked-By", mock.name);
    httpResponse.forEachHeader((name, value) -> {
      headers.add(name, value);
    });

    ByteBuf content = readBytes(mock.name, httpResponse.getContent());
    return response(request, httpResponse.getStatus(), content, headers);
  }

  private ByteBuf readBytes(String mockName, Supplier<InputStream> source) {
    try (InputStream in = source.get()) {
      return wrappedBuffer(in.readAllBytes());
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to read mocked response content for " + mockName, e);
    }
  }

  private static DefaultFullHttpResponse response(HttpRequest request, int status, ByteBuf content, HttpHeaders headers) {
    return new DefaultFullHttpResponse(request.protocolVersion(), valueOf(status), content, headers, EmptyHttpHeaders.INSTANCE);
  }

  private static final class ResponseMock {
    private final String name;
    private final RequestMatcher requestMatcher;
    private final Supplier<HttpResponse> mockedResponse;

    private ResponseMock(String name, RequestMatcher requestMatcher, Supplier<HttpResponse> mockedResponse) {
      this.name = name;
      this.requestMatcher = requestMatcher;
      this.mockedResponse = mockedResponse;
    }
  }
}
