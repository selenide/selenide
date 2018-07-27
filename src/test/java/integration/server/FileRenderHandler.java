package integration.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

class FileRenderHandler extends BaseHandler {
  private final Set<String> sessions;

  FileRenderHandler(Set<String> sessions) {
    this.sessions = sessions;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    doGet(request, response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long start = System.nanoTime();

    String fileName = getFilenameFromRequest(request);
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      response.setStatus(SC_NOT_FOUND);
      logRequest(request, "NOT_FOUND", start);
      return;
    }

    generateSessionId(response);
    response.setStatus(SC_OK);
    response.setContentLength(fileContent.length);

    response.setContentType(getContentType(fileName));
    printResponse(response, fileContent);
    logRequest(request, "ok", start);
  }

  private void generateSessionId(HttpServletResponse http) {
    String sessionId = "" + System.currentTimeMillis();
    Cookie cookie = new Cookie("session_id", sessionId);
    cookie.setMaxAge(-1);
    cookie.setPath("/");
    http.addCookie(cookie);
    sessions.add(sessionId);
  }
}
