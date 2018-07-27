package integration.server;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import static com.google.common.base.Joiner.on;
import static java.lang.Thread.currentThread;

public abstract class BaseHandler extends HttpServlet {
  static final String CONTENT_TYPE_HTML_TEXT = "text/html";
  private static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
  private static final Logger log = Logger.getLogger(BaseHandler.class.getName());

  String getContentType(String fileName) {
    String fileExtension = FilenameUtils.getExtension(fileName);
    return fileExtension.contains("png") ? CONTENT_TYPE_IMAGE_PNG : CONTENT_TYPE_HTML_TEXT;
  }

  void printResponse(HttpServletResponse http, byte[] fileContent) throws IOException {
    try (OutputStream os = http.getOutputStream()) {
      os.write(fileContent);
    }
  }

  String getFilenameFromRequest(HttpServletRequest request) {
    return request.getPathInfo().replaceFirst("/(.*)", "$1");
  }

  byte[] readFileContent(String fileName) throws IOException {
    InputStream in = currentThread().getContextClassLoader().getResourceAsStream(fileName);
    if (in == null) return null;
    try {
      return IOUtils.toByteArray(in);
    } finally {
      in.close();
    }
  }

  void logRequest(HttpServletRequest request, Object response, long startTime) {
    String time = new SimpleDateFormat("hh:MM:ss:SSS").format(new Date());
    log.info(time + " " +
      on('?').skipNulls().join(request.getRequestURL(), request.getQueryString()) +
      " -> " + response +
      " " + (System.nanoTime() - startTime) / 1000000 + " ms");
  }
}
