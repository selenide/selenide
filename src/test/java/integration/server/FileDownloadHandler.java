package integration.server;

import org.assertj.core.api.Assertions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Set;

import static com.codeborne.selenide.Selenide.sleep;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

class FileDownloadHandler extends BaseHandler {
  private final Set<String> sessions;

  FileDownloadHandler(Set<String> sessions) {
    this.sessions = sessions;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long start = System.nanoTime();
    String sessionId = getSessionId(request);
    Assertions.assertThat(sessions).contains(sessionId);

    String fileName = getFilenameFromRequest(request);
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      response.setStatus(SC_NOT_FOUND);
      logRequest(request, "NOT_FOUND", start);
      return;
    }

    if (request.getParameter("pause") != null) {
      sleep(Long.parseLong(request.getParameter("pause")));
    }

    response.setStatus(SC_OK);
    response.setContentLength(fileContent.length);
    response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
    response.setContentType(getContentType(fileName));

    printResponse(response, fileContent);
    logRequest(request, "ok", start);
  }

  private String getSessionId(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("session_id".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    throw new IllegalArgumentException("No cookie 'session_id' found: " + Arrays.toString(request.getCookies()));
  }
}
