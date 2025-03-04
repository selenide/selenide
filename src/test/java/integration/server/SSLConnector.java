package integration.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import static java.util.Objects.requireNonNull;

// Configure Jetty to listen both http and https port
// as described here: http://java.dzone.com/articles/adding-ssl-support-embedded
public class SSLConnector extends ServerConnector {
  public SSLConnector(Server server, int port) {
    super(server,
      new SslConnectionFactory(createSslContextFactory(), "http/1.1"),
      new HttpConnectionFactory(https()));
    setPort(port);
  }

  @Override
  public final void setPort(int port) {
    super.setPort(port);
  }

  private static HttpConfiguration https() {
    HttpConfiguration https = new HttpConfiguration();
    https.addCustomizer(new SecureRequestCustomizer(false));
    return https;
  }

  /**
   * File "test-selenide.jks" was created with the command
   * <pre>
   *   {@code keytool -genkey -alias test.selenide.org -keyalg RSA -keystore src/test/resources/test-selenide.jks}
   * </pre>
   */
  @SuppressWarnings("SpellCheckingInspection")
  private static SslContextFactory.Server createSslContextFactory() {
    SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath(requireNonNull(SSLConnector.class.getResource("/test-selenide.jks")).toExternalForm());
    sslContextFactory.setKeyStorePassword("selenide.rulez");
    sslContextFactory.setKeyManagerPassword("selenide.rulez");
    return sslContextFactory;
  }
}
