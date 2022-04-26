package integration.server;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.hc.core5.http.HttpStatus.SC_UNAUTHORIZED;

class BearerTokenHandler extends BaseHandler {
  private final Set<String> allowedTokens = new HashSet<>(asList("token-123", "token-456"));

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

    String bearerToken = request.getHeader("Authorization").replace("Bearer ", "");
    if (!allowedTokens.contains(bearerToken)) {
      return new Result(SC_UNAUTHORIZED, CONTENT_TYPE_HTML_TEXT, "UNAUTHORIZED: Invalid bearer token " + bearerToken);
    }
    String path = request.getPathInfo().replace("/", "");
    String html = String.format("<html><body>" +
      "<div id=\"greeting\">%s, %s!</div>" +
      "<br>" +
      "<br>" +
      "<a id=\"bye\" href=\"/bearer-token-auth/bye\">bye!</a></body></html>", path, bearerToken);

    return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, html);
  }
}
