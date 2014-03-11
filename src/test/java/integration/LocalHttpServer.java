package integration;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;

public class LocalHttpServer {

  private final Server server;

  public LocalHttpServer(int port) throws IOException {
    server = new Server(port);

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(new FileDownloadHandler()), "/files/*");
    context.addServlet(new ServletHolder(new FileHandler()), "/*");
  }

  public LocalHttpServer start() throws Exception {
    server.start();
    return this;
  }

  private Set<String> sessions = new ConcurrentSkipListSet<String>();

  private class FileHandler extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      byte[] fileContent = readFileContent(request);
      if (fileContent == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      generateSessionId(response);
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentLength(fileContent.length);

      printResponse(response, fileContent);
    }

    private void generateSessionId(HttpServletResponse http) {
      String sessionId = "" + System.currentTimeMillis();
      Cookie cookie = new Cookie("session_id", sessionId);
      cookie.setMaxAge(-1);
//      cookie.setDomain("localhost");
      cookie.setPath("/");
      http.addCookie(cookie);
      sessions.add(sessionId);
    }
  }

  private class FileDownloadHandler extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String sessionId = getSessionId(request);
      assertTrue(sessions.contains(sessionId));

      byte[] fileContent = readFileContent(request);
      if (fileContent == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentLength(fileContent.length);
      response.setHeader("content-disposition", "attachment; filename=" + request.getPathInfo());
      printResponse(response, fileContent);
    }
  }

  private String getSessionId(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()){
        if ("session_id".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    throw new IllegalArgumentException("No cookie 'session_id' found: " + Arrays.toString(request.getCookies()));
  }

  static byte[] readFileContent(HttpServletRequest request) throws IOException {
    String fileName = request.getPathInfo().replaceFirst("\\/(.*)", "$1");
    InputStream in = currentThread().getContextClassLoader().getResourceAsStream(fileName);
    if (in == null) return null;
    try {
      return IOUtils.toByteArray(in);
    } finally {
      in.close();
    }
  }

  static void printResponse(HttpServletResponse http, byte[] fileContent) throws IOException {
    OutputStream os = http.getOutputStream();
    try {
      os.write(fileContent);
    } finally {
      os.close();
    }
  }
}
