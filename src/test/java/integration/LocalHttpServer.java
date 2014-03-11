package integration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.lang.Thread.currentThread;

public class LocalHttpServer {

  private final HttpServer server;

  public LocalHttpServer(int port) throws IOException {
    server = HttpServer.create(new InetSocketAddress(port), 0);
//    server.createContext("/test", new MyHandler());
    server.createContext("/", new FileHandler());
    server.setExecutor(null);
  }

  public LocalHttpServer start() {
    server.start();
    return this;
  }

  private static class FileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange http) throws IOException {
      String fileName = http.getRequestURI().getPath().replaceFirst("\\/(.*)", "$1");
      byte[] fileContent = readFileContent(fileName);

      http.sendResponseHeaders(200, fileContent.length);
      OutputStream os = http.getResponseBody();
      try {
        os.write(fileContent);
      } finally {
        os.close();
      }

    }

    private byte[] readFileContent(String file) throws IOException {
      InputStream in = currentThread().getContextClassLoader().getResourceAsStream(file);
      try {
        return IOUtils.toByteArray(in);
      } finally {
        in.close();
      }
    }
  }

//  private static class MyHandler implements HttpHandler {
//    @Override
//    public void handle(HttpExchange t) throws IOException {
//      String response = "This is the response";
//      t.sendResponseHeaders(200, response.length());
//      OutputStream os = t.getResponseBody();
//      os.write(response.getBytes());
//      os.close();
//    }
//  }
}
