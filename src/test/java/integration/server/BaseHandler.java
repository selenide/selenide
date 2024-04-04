package integration.server;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static integration.server.Delayer.writeSlowly;
import static integration.server.Delayer.pause;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

public abstract class BaseHandler extends HttpServlet {
  static final String CONTENT_TYPE_PLAIN_TEXT = "text/plain";
  static final String CONTENT_TYPE_HTML_TEXT = "text/html";
  private static final Pattern RE_FILE_EXTENSION = Pattern.compile(".*\\.(.+)");
  private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);

  Result get(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return new Result(SC_METHOD_NOT_ALLOWED, CONTENT_TYPE_HTML_TEXT, "Method not allowed: GET");
  }

  @Override
  public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long start = System.nanoTime();
    result(request, response, start, get(request, response));
  }

  Result post(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return new Result(SC_METHOD_NOT_ALLOWED, CONTENT_TYPE_HTML_TEXT, "Method not allowed: POST");
  }

  @Override
  public final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long start = System.nanoTime();
    result(request, response, start, post(request, response));
  }

  private void result(HttpServletRequest request, HttpServletResponse response, long start, Result result) throws IOException {
    if (result.pause > 0) {
      log.debug("sleep before [{}] for {} ms", request.getPathInfo(), result.pause);
      pause(result.pause);
    }
    response.setStatus(result.httpStatus);
    response.setContentLength(result.contentLength);
    response.setContentType(result.contentType);
    for (Map.Entry<String, String> httpHeader : result.httpHeaders.entrySet()) {
      response.setHeader(httpHeader.getKey(), httpHeader.getValue());
    }
    if (!result.httpHeaders.containsKey("Cache-Control")) {
      response.setHeader("Cache-Control", "no-cache");
    }
    writeContent(response, result);
    logRequest(request, result.httpStatus, start);
    if (result.httpStatus >= SC_BAD_REQUEST) {
      log.error("Http response {}: '{}'", result.httpStatus, result.content == null ? null : IOUtils.toString(result.content, UTF_8));
    }
  }

  protected String contentType(String fileName) {
    String fileExtension = RE_FILE_EXTENSION.matcher(fileName).replaceFirst("$1");

    return switch (fileExtension) {
      case "json" -> "application/json";
      case "txt" -> CONTENT_TYPE_PLAIN_TEXT;
      case "html" -> CONTENT_TYPE_HTML_TEXT;
      case "pdf" -> "application/pdf";
      case "png" -> "image/png";
      case "ico" -> "image/x-icon";
      default -> "application/octet-stream";
    };
  }

  String getFilenameFromRequest(HttpServletRequest request) {
    return request.getPathInfo().replaceFirst("/(.*)", "$1");
  }

  byte[] readFileContent(String fileName) throws IOException {
    try (InputStream in = currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
      return in == null ? null : IOUtils.toByteArray(in);
    }
  }

  @SuppressWarnings("SuspiciousDateFormat")
  private void logRequest(HttpServletRequest request, int httpStatus, long startTime) {
    String time = new SimpleDateFormat("hh:MM:ss:SSS").format(new Date());
    log.info("{} {} {} -> {} {} ms",
      time,
      request.getMethod(),
      Stream.of(request.getRequestURL(), request.getQueryString())
        .filter(Objects::nonNull)
        .collect(joining("?")),
      httpStatus,
      (System.nanoTime() - startTime) / 1_000_000
    );
  }

  private void writeContent(HttpServletResponse response, Result result) throws IOException {
    if (result.content != null) {
      try (OutputStream os = response.getOutputStream()) {
        if (result.duration == 0) {
          writeQuickly(os, result.content);
        }
        else {
          writeSlowly(os, result.contentLength, result.content, result.duration);
        }
      }
    }
  }

  private static void writeQuickly(OutputStream os, InputStream content) throws IOException {
    byte[] buffer = new byte[2048];
    for (int count = content.read(buffer); count > -1; count = content.read(buffer)) {
      os.write(buffer, 0, count);
      os.flush();
    }
  }
}
