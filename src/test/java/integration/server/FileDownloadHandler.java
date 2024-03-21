package integration.server;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Long.parseLong;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.net.URLEncoder.encode;

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

    boolean exposeFileName = !"false".equalsIgnoreCase(request.getParameter("exposeFileName"));

    String fileName = getFilenameFromRequest(request);
    String contentType = contentType(fileName);

    if ("large_file.txt".equals(fileName)) {
      int contentLength = 5 * 1024 * 1024; // 5 MB
      return new Result(SC_OK, contentType, contentLength, generateLargeFile(contentLength), headers(fileName, exposeFileName),
        longParam(request, "pause"), longParam(request, "duration"));
    }
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      return new Result(SC_NOT_FOUND, CONTENT_TYPE_HTML_TEXT, "NOT_FOUND: " + fileName);
    }

    if ("файл-с-запрещёнными-символами.txt".equals(fileName)) {
      fileName = "имя с #pound,%percent,&ampersand,{left,}right,\\backslash," +
        "<left,>right,*asterisk,?question,$dollar,!exclamation,'quote,\"quotes," +
        ":colon,@at,+plus,`backtick,|pipe,=equal.txt";
      contentType = CONTENT_TYPE_HTML_TEXT;
    }
    return new Result(SC_OK, contentType, fileContent, headers(fileName, exposeFileName),
      longParam(request, "pause"), longParam(request, "duration"));
  }

  @Nonnull
  @CheckReturnValue
  private Map<String, String> headers(String fileName, boolean exposeFileName) throws UnsupportedEncodingException {
    Map<String, String> map = new HashMap<>();
    map.put("content-disposition", exposeFileName ? "attachment; filename=" + encode(fileName, "UTF-8") : "attachment;");
    map.put("content-type", contentType(fileName));
    return map;
  }

  private InputStream generateLargeFile(final int contentLength) {
    return new ContentGenerator(contentLength);
  }

  private long longParam(HttpServletRequest request, String name) {
    String param = request.getParameter(name);
    return param == null ? 0 : parseLong(param);
  }

  private String getSessionId(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return "Request has no cookies";
    }
    for (Cookie cookie : request.getCookies()) {
      if ("session_id".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    return "No cookie 'session_id' found: " + Arrays.stream(request.getCookies()).map(Cookie::getName).toList();
  }
}
