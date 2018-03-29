package integration;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;

import static com.google.common.base.Joiner.on;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.SEVERE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertTrue;

public class LocalHttpServer {
  private static final Logger log = Logger.getLogger(LocalHttpServer.class.getName());
  private static final String CONTENT_TYPE_HTML_TEXT  = "text/html";
  private static final String CONTENT_TYPE_IMAGE_PNG  = "image/png";

  private final Server server;

  /**
   * @param port
   * @param ssl
   * @throws IOException
   */
  public LocalHttpServer(int port, boolean ssl) {
    server = new Server();

    if (ssl) {
      configureHttps(port);
    } else {
      ServerConnector connector = new ServerConnector(server);
      connector.setPort(port);
      server.setConnectors(new Connector[]{connector});
    }

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(new FileDownloadHandler()), "/files/*");
    context.addServlet(new ServletHolder(new FileUploadHandler()), "/upload");
    context.addServlet(new ServletHolder(new FileHandler()), "/*");
  }

  // Configure Jetty to listen both http and https port
  // as described here: http://java.dzone.com/articles/adding-ssl-support-embedded
  private void configureHttps(int port) {
    HttpConfiguration https = new HttpConfiguration();
    https.addCustomizer(new SecureRequestCustomizer());

    SslContextFactory sslContextFactory = new SslContextFactory();

    // created with "keytool -genkey -alias test.selenide.org -keyalg RSA -keystore test-selenide.jks -keysize 2048"
    sslContextFactory.setKeyStorePath(getClass().getResource("/test-selenide.jks").toExternalForm());
    sslContextFactory.setKeyStorePassword("selenide.rulez");
    sslContextFactory.setKeyManagerPassword("selenide.rulez");

    ServerConnector sslConnector = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, "http/1.1"),
        new HttpConnectionFactory(https));
    sslConnector.setPort(port);

    server.setConnectors(new Connector[] {sslConnector});
  }

  public LocalHttpServer start() throws Exception {
    server.start();
    return this;
  }

  private void logRequest(HttpServletRequest request, Object response, long startTime) {
    String time = new SimpleDateFormat("hh:MM:ss:SSS").format(new Date());
    log.info(time + " " +
        on('?').skipNulls().join(request.getRequestURL(), request.getQueryString()) +
        " -> " + response +
        " " + (System.nanoTime() - startTime) / 1000000 + " ms");
  }

  private Set<String> sessions = new ConcurrentSkipListSet<>();

  private class FileHandler extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
//      cookie.setDomain("localhost");
      cookie.setPath("/");
      http.addCookie(cookie);
      sessions.add(sessionId);
    }
  }

  private class FileDownloadHandler extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      long start = System.nanoTime();
      String sessionId = getSessionId(request);
      assertTrue(sessions.contains(sessionId));

      String fileName = getFilenameFromRequest(request);
      byte[] fileContent = readFileContent(fileName);
      if (fileContent == null) {
        response.setStatus(SC_NOT_FOUND);
        logRequest(request, "NOT_FOUND", start);
        return;
      }

      response.setStatus(SC_OK);
      response.setContentLength(fileContent.length);
      response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
      response.setContentType(getContentType(fileName));

      printResponse(response, fileContent);
      logRequest(request, "ok", start);
    }
  }

  public final List<FileItem> uploadedFiles = new ArrayList<>(2);

  private class FileUploadHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      long start = System.nanoTime();

      DiskFileItemFactory factory = new DiskFileItemFactory();
      factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
      ServletFileUpload upload = new ServletFileUpload(factory);
      try {
        List<FileItem> items = upload.parseRequest(request);
        for (FileItem item : items) {
          if (item.getSize() > 0) {
            uploadedFiles.add(item);
          }
        }

        String message = "<h3>Uploaded " + uploadedFiles.size() + " files</h3>" + items;
        response.setContentType(CONTENT_TYPE_HTML_TEXT);
        printResponse(response, message.getBytes("UTF-8"));
        logRequest(request, message, start);
      } catch (FileUploadException e) {
        logRequest(request, e.getMessage(), start);
        log.log(SEVERE, e.getMessage(), e);
      }
    }
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

  static byte[] readFileContent(String fileName) throws IOException {
    InputStream in = currentThread().getContextClassLoader().getResourceAsStream(fileName);
    if (in == null) return null;
    try {
      return IOUtils.toByteArray(in);
    } finally {
      in.close();
    }
  }

  private static String getFilenameFromRequest(HttpServletRequest request) {
    return request.getPathInfo().replaceFirst("/(.*)", "$1");
  }

  static String getContentType(String fileName) {
    String fileExtension = FilenameUtils.getExtension(fileName);
    return fileExtension.contains("png") ? CONTENT_TYPE_IMAGE_PNG : CONTENT_TYPE_HTML_TEXT;
  }

  static void printResponse(HttpServletResponse http, byte[] fileContent) throws IOException {
    try (OutputStream os = http.getOutputStream()) {
      os.write(fileContent);
    }
  }

  /**
   * Method may be used to locally run test server used by Selenide own tests
   *
   * @param args not used
   */
  public static void main(String[] args) throws Exception {
    LocalHttpServer server = new LocalHttpServer(8080, false).start();
    Thread.currentThread().join();
  }
}
