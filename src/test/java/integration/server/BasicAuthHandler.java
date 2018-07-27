package integration.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;

class BasicAuthHandler extends BaseHandler {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userPasswBase64 = request.getHeader("Authorization").replace("Basic ", "");
    byte[] userPasswBytes = Base64.getDecoder().decode(userPasswBase64);
    String userPassw = new String(userPasswBytes, UTF_8);
    String html = "<html><body>Hello, " + userPassw + "!</body></html>";

    response.setStatus(SC_OK);
    response.setContentLength(html.length());
    response.setContentType("text/html");
    printResponse(response, html.getBytes(UTF_8));
  }
}
