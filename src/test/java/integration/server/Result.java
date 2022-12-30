package integration.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;

class Result {
  final int httpStatus;
  final String contentType;
  final int contentLength;
  final InputStream content;
  final Map<String, String> httpHeaders;
  final long pause;
  final long duration;

  Result(int httpStatus, String contentType, String content) {
    this(httpStatus, contentType, content.getBytes(UTF_8));
  }

  Result(int httpStatus, String contentType, byte[] content) {
    this(httpStatus, contentType, content, emptyMap());
  }

  Result(int httpStatus, String contentType, byte[] content, Map<String, String> httpHeaders) {
    this(httpStatus, contentType, content, httpHeaders, 0, 0);
  }

  Result(int httpStatus, String contentType, byte[] content, Map<String, String> httpHeaders, long pause, long duration) {
    this(httpStatus, contentType, content.length, new ByteArrayInputStream(content), httpHeaders, pause, duration);
  }

  Result(int httpStatus, String contentType, int contentLength, InputStream content,
         Map<String, String> httpHeaders, long pause, long duration) {
    this.httpStatus = httpStatus;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.content = content;
    this.httpHeaders = httpHeaders;
    this.pause = pause;
    this.duration = duration;
  }
}
