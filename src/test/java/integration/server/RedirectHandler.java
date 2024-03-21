package integration.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;

class RedirectHandler extends BaseHandler {
  private static final Pattern RE = Pattern.compile("/redirect/to(/.+)/\\w+");

  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) {
    String path = request.getRequestURI();
    String redirectTo = RE.matcher(path).replaceFirst("$1");
    String location = request.getQueryString() == null ? redirectTo : redirectTo + '?' + request.getQueryString();
    return new Result(SC_MOVED_TEMPORARILY, Map.of("Location", location));
  }
}
