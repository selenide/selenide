package integration.server;

import org.apache.commons.fileupload.FileItem;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalHttpServer {
  private final List<FileItem> uploadedFiles = new CopyOnWriteArrayList<>();
  private final Set<String> sessions = new ConcurrentSkipListSet<>();
  private final Server server;

  public LocalHttpServer(int port, boolean ssl) {
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
    basicAuthSecurityHandler.addUser("scott", "tiger");
    basicAuthSecurityHandler.addUser("scott2", "tiger2");
    context.setSecurityHandler(basicAuthSecurityHandler);
    server.setHandler(context);

    context.addServlet(new ServletHolder(new FileDownloadHandler(sessions)), "/files/*");
    context.addServlet(new ServletHolder(new FileUploadHandler(uploadedFiles)), "/upload");
    context.addServlet(new ServletHolder(new BasicAuthHandler()), "/basic-auth/*");
    context.addServlet(new ServletHolder(new HeadersPrinterHandler()), "/headers/*");
    context.addServlet(new ServletHolder(new FileRenderHandler(sessions)), "/*");
  }

  public List<FileItem> getUploadedFiles() {
    return uploadedFiles;
  }

  public void reset() {
    uploadedFiles.clear();
    sessions.clear();
  }

  public LocalHttpServer start() throws Exception {
    server.start();
    return this;
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
}
