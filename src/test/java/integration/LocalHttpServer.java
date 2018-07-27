package integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.WithAssertions;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import static com.codeborne.selenide.Selenide.sleep;
import static com.google.common.base.Joiner.on;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

public class LocalHttpServer implements WithAssertions {
  private static final Logger log = Logger.getLogger(LocalHttpServer.class.getName());
  private static final String CONTENT_TYPE_HTML_TEXT = "text/html";
  private static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
  public final List<FileItem> uploadedFiles = new ArrayList<>(2);
  private final Server server;
  private Set<String> sessions = new ConcurrentSkipListSet<>();

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
    context.setSecurityHandler(basicAuth("/basic-auth/*", "scott", "tiger", "Private!"));
    server.setHandler(context);

    context.addServlet(new ServletHolder(new FileDownloadHandler()), "/files/*");
    context.addServlet(new ServletHolder(new FileUploadHandler()), "/upload");
    context.addServlet(new ServletHolder(new BasicAuthHandler()), "/basic-auth/*");
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

    server.setConnectors(new Connector[]{sslConnector});
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

  private static SecurityHandler basicAuth(String context, String username, String password, String realm) {
    HashLoginService loginService = new HashLoginService();
    UserStore userStore = new UserStore();
    userStore.addUser(username, Credential.getCredential(password), new String[] {"user"});
    loginService.setUserStore(userStore);
    loginService.setName(realm);

    Constraint constraint = new Constraint();
    constraint.setName(Constraint.__BASIC_AUTH);
    constraint.setRoles(new String[]{"user"});
    constraint.setAuthenticate(true);

    ConstraintMapping constraintMapping = new ConstraintMapping();
    constraintMapping.setConstraint(constraint);
    constraintMapping.setPathSpec(context);

    ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
    constraintSecurityHandler.setAuthenticator(new BasicAuthenticator());
    constraintSecurityHandler.setRealmName("myrealm");
    constraintSecurityHandler.addConstraintMapping(constraintMapping);
    constraintSecurityHandler.setLoginService(loginService);

    return constraintSecurityHandler;
  }

  /**
   * Method may be used to locally run test server used by Selenide own tests
   *
   * @param args not used
   */
  public static void main(String[] args) throws Exception {
    new LocalHttpServer(8080, false).start();
    Thread.currentThread().join();
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

  private class FileHandler extends HttpServlet {
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

  private class BasicAuthHandler extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String basicAuth = new String(Base64.getDecoder().decode(request.getHeader("Authorization").replace("Basic ", "")), UTF_8);
      String html = "<html><body>Hello, " + basicAuth + "!</body></html>";

      response.setStatus(SC_OK);
      response.setContentLength(html.length());
      response.setContentType("text/html");
      printResponse(response, html.getBytes(UTF_8));
    }
  }

  private class FileDownloadHandler extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      long start = System.nanoTime();
      String sessionId = getSessionId(request);
      assertThat(sessions)
        .contains(sessionId);

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
  }

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
}
