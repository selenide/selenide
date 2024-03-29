package integration.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

// Configure Jetty to listen both http and https port
// as described here: http://java.dzone.com/articles/adding-ssl-support-embedded
public class SSLConnector extends ServerConnector {
  public SSLConnector(Server server, int port) {
    super(server,
      new SslConnectionFactory(createSslContextFactory(), "http/1.1"),
      new HttpConnectionFactory(https()));
    setPort(port);
  }

  private static HttpConfiguration https() {
    HttpConfiguration https = new HttpConfiguration();
    https.addCustomizer(new SecureRequestCustomizer(false));
    return https;
  }

  private static SslContextFactory.Server createSslContextFactory() {
    SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

    // created with "keytool -genkey -alias test.selenide.org -keyalg RSA -keystore src/test/resources/test-selenide.jks"
    sslContextFactory.setKeyStorePath(SSLConnector.class.getResource("/test-selenide.jks").toExternalForm());
    sslContextFactory.setKeyStorePassword("selenide.rulez");
    sslContextFactory.setKeyManagerPassword("selenide.rulez");
    return sslContextFactory;
  }
}
