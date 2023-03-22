package integration.server;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.fileupload.FileItem;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.openqa.selenium.net.PortProber.findFreePort;

public class LocalHttpServer {
  private static final Logger log = LoggerFactory.getLogger(LocalHttpServer.class);
  private final List<FileItem> uploadedFiles = new CopyOnWriteArrayList<>();
  private final Set<String> sessions = new ConcurrentSkipListSet<>();
  private final Server server;
  private final int port;

  LocalHttpServer(int port, boolean ssl, String friendlyOrigin, Map<String, String> basicAuthUsers) {
    this.port = port;
    server = new Server();

    if (ssl) {
      server.setConnectors(new Connector[]{new SSLConnector(server, port)});
    } else {
      ServerConnector connector = new ServerConnector(server);
      connector.setPort(port);
      server.setConnectors(new Connector[]{connector});
    }

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    BasicAuthSecurityHandler basicAuthSecurityHandler = new BasicAuthSecurityHandler("/basic-auth/*", "Private!");
    for (Map.Entry<String, String> user : basicAuthUsers.entrySet()) {
      basicAuthSecurityHandler.addUser(user.getKey(), user.getValue());
    }
    context.setSecurityHandler(basicAuthSecurityHandler);
    server.setHandler(context);

    context.addServlet(new ServletHolder(new FileDownloadHandler(sessions)), "/files/*");
    context.addServlet(new ServletHolder(new FileUploadHandler(uploadedFiles)), "/upload");
    context.addServlet(new ServletHolder(new BasicAuthHandler()), "/basic-auth/*");
    context.addServlet(new ServletHolder(new BearerTokenHandler()), "/bearer-token-auth/*");
    context.addServlet(new ServletHolder(new HeadersPrinterHandler()), "/headers/*");
    context.addServlet(new ServletHolder(new CorsProtectedHandler(friendlyOrigin)), "/try-cors/*");
    context.addServlet(new ServletHolder(new FileRenderHandler(sessions)), "/*");
  }

  public int getPort() {
    return port;
  }

  public List<FileItem> getUploadedFiles() {
    return uploadedFiles;
  }

  public void reset() {
    uploadedFiles.clear();
    sessions.clear();
    log.info("Reset sessions & uploaded files");
  }

  public LocalHttpServer start() throws Exception {
    server.start();
    return this;
  }

  public void stop() throws Exception {
    server.stop();
  }

  public static LocalHttpServer startWithRetry(boolean ssl, String friendlyOrigin,
                                               Map<String, String> basicAuthUsers) throws Exception {
    IOException lastError = null;
    for (int i = 0; i < 5; i++) {
      try {
        return new LocalHttpServer(findFreePort(), ssl, friendlyOrigin, basicAuthUsers).start();
      }
      catch (IOException failedToStartServer) {
        if (failedToStartServer.getCause() instanceof BindException) {
          lastError = failedToStartServer;
        }
        else {
          throw failedToStartServer;
        }
      }
    }
    throw lastError;
  }

  /**
   * Method may be used to locally run test server used by Selenide own tests
   *
   * @param args not used
   */
  public static void main(String[] args) throws Exception {
    new LocalHttpServer(8080, false, "no-cors-allowed", ImmutableMap.of("scott", "tiger")).start();
    Thread.currentThread().join();
  }
}
