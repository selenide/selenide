package integration.server;

import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

public abstract class BaseHandler extends HttpServlet {
  static final String CONTENT_TYPE_HTML_TEXT = "text/html";
  private static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
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
    response.setStatus(result.httpStatus);
    response.setContentLength(result.content.length);
    response.setContentType(result.contentType);
    for (Map.Entry<String, String> httpHeader : result.httpHeaders.entrySet()) {
      response.setHeader(httpHeader.getKey(), httpHeader.getValue());
    }
    try (OutputStream os = response.getOutputStream()) {
      os.write(result.content);
    }
    logRequest(request, result.httpStatus, start);
    if (result.httpStatus >= SC_BAD_REQUEST) {
      log.error(new String(result.content, UTF_8));
    }
  }

  String getContentType(String fileName) {
    String fileExtension = FilenameUtils.getExtension(fileName);
    return fileExtension.contains("png") ? CONTENT_TYPE_IMAGE_PNG : CONTENT_TYPE_HTML_TEXT;
  }

  String getFilenameFromRequest(HttpServletRequest request) {
    return request.getPathInfo().replaceFirst("/(.*)", "$1");
  }

  byte[] readFileContent(String fileName) throws IOException {
    InputStream in = currentThread().getContextClassLoader().getResourceAsStream(fileName);
    if (in == null) return null;
    try {
      return IOUtils.toByteArray(in);
    }
    finally {
      in.close();
    }
  }

  private void logRequest(HttpServletRequest request, int httpStatus, long startTime) {
    String time = new SimpleDateFormat("hh:MM:ss:SSS").format(new Date());
    log.info("{} {} -> {} {} ms",
      time,
      Stream.of(request.getRequestURL(), request.getQueryString())
        .filter(Objects::nonNull)
        .collect(joining("?")),
      httpStatus,
      (System.nanoTime() - startTime) / 1000000
    );
  }
}
