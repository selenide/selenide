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
  public Result post(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return get(request, response);
  }

  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fileName = getFilenameFromRequest(request);
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      return new Result(SC_NOT_FOUND, CONTENT_TYPE_HTML_TEXT, "NOT_FOUND");
    }

    generateSessionId(response);
    return new Result(SC_OK, getContentType(fileName), fileContent);
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
