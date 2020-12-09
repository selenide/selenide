package integration.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

class FileDownloadHandler extends BaseHandler {
  private final Set<String> sessions;

  FileDownloadHandler(Set<String> sessions) {
    this.sessions = sessions;
  }

  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String sessionId = getSessionId(request);
    if (!sessions.contains(sessionId)) {
      return new Result(SC_UNAUTHORIZED, CONTENT_TYPE_HTML_TEXT, "Unknown session: " + sessionId + ", known sessions: " + sessions);
    }

    String fileName = getFilenameFromRequest(request);
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      return new Result(SC_NOT_FOUND, CONTENT_TYPE_HTML_TEXT, "NOT_FOUND: " + fileName);
    }

    if (request.getParameter("pause") != null) {
      try {
        Thread.sleep(Long.parseLong(request.getParameter("pause")));
      }
      catch (InterruptedException ignore) {
      }
    }

    String contentType = getContentType(fileName);
    if ("файл-с-запрещёнными-символами.txt".equals(fileName)) {
      fileName = "имя с #pound,%percent,&ampersand,{left,}right,\\backslash," +
        "<left,>right,*asterisk,?question,$dollar,!exclamation,'quote,\"quotes," +
        ":colon,@at,+plus,`backtick,|pipe,=equal.txt";
      contentType = CONTENT_TYPE_HTML_TEXT;
    }
    Map<String, String> map = new HashMap<>();
    map.put("content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
    return new Result(SC_OK, contentType, fileContent, map);
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
