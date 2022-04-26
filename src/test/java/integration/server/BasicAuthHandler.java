package integration.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.hc.core5.http.HttpStatus.SC_UNAUTHORIZED;

class BasicAuthHandler extends BaseHandler {
  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) {
    List<String> authorizationHeaders = Collections.list(request.getHeaders("Authorization"));
    if (authorizationHeaders.isEmpty()) {
      return new Result(SC_UNAUTHORIZED, CONTENT_TYPE_HTML_TEXT, "UNAUTHORIZED: no Authorization header");
    }
    else if (authorizationHeaders.size() > 1) {
      return new Result(SC_UNAUTHORIZED, CONTENT_TYPE_HTML_TEXT,
        "UNAUTHORIZED: too many Authorization headers: " + authorizationHeaders);
    }

    String userPasswordBase64 = request.getHeader("Authorization").replace("Basic ", "");
    byte[] userPasswordBytes = Base64.getDecoder().decode(userPasswordBase64);
    String userPassword = new String(userPasswordBytes, UTF_8);
    String path = request.getPathInfo().replace("/", "");
    String html = String.format("<html><body>" +
      "<div id=\"greeting\">%s, %s!</div>" +
      "<br>" +
      "<br>" +
      "<a id=\"bye\" href=\"/basic-auth/bye\">bye!</a></body></html>", path, userPassword);

    return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, html);
  }
}
