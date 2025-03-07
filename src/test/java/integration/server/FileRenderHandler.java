package integration.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@NullMarked
class FileRenderHandler extends BaseHandler {
  private static final Logger log = LoggerFactory.getLogger(FileRenderHandler.class);
  private final AtomicLong sessionIdCounter = new AtomicLong();
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
    sleepIfNeeded(request.getParameter("duration"));
    String fileName = getFilenameFromRequest(request);
    byte[] fileContent = readFileContent(fileName);
    if (fileContent == null) {
      return new Result(SC_NOT_FOUND, CONTENT_TYPE_HTML_TEXT, "NOT_FOUND");
    }

    if (!"/favicon.ico".equals(request.getPathInfo())) {
      generateSessionId(request, response);
    }

    return new Result(SC_OK, contentType(fileName), fileContent);
  }

  private void sleepIfNeeded(@Nullable String duration) {
    if (duration != null) {
      try {
        Thread.sleep(Long.parseLong(duration));
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }

  private void generateSessionId(HttpServletRequest request, HttpServletResponse response) {
    String sessionId = String.format("%s.%s", System.currentTimeMillis(), sessionIdCounter.getAndIncrement());
    Cookie cookie = new Cookie("session_id", sessionId);
    cookie.setMaxAge(-1);
    cookie.setPath("/");
    response.addCookie(cookie);

    log.info("Generated session ID {} for request {}", sessionId, request.getPathInfo());
    sessions.add(sessionId);
  }
}
