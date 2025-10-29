package integration.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

class CorsProtectedHandler extends BaseHandler {
  private static final Logger log = LoggerFactory.getLogger(CorsProtectedHandler.class);
  private final String friendlyOrigin;

  CorsProtectedHandler(String friendlyOrigin) {
    this.friendlyOrigin = friendlyOrigin;
  }

  @Override
  protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
    log.info("Option {} -> allow {}", request.getPathInfo(), friendlyOrigin);
    response.addHeader("Access-Control-Allow-Headers", "Content-type, X-Boom");
    response.addHeader("Access-Control-Allow-Origin", friendlyOrigin);
    response.addHeader("Access-Control-Max-Age", "0");
    response.addHeader("Content-Length", "0");
    response.setStatus(SC_OK);
  }

  @Override
  public Result post(HttpServletRequest request, HttpServletResponse response) {
    String name = request.getPathInfo().substring(1);
    byte[] greeting = String.format("Say CORS and enter, friend %s!", name).getBytes(UTF_8);
    return new Result(SC_OK, CONTENT_TYPE_PLAIN_TEXT, greeting, Map.of("Access-Control-Allow-Origin", friendlyOrigin));
  }
}
