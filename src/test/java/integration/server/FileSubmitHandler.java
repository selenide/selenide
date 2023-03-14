package integration.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

class FileSubmitHandler extends BaseHandler {
  @Override
  public Result post(HttpServletRequest request, HttpServletResponse response) {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    Map<String, String[]> params = request.getParameterMap();

    String message = String.format("<h3>Submitted %s parameters:</h3><pre>%s</pre>", params.size(), params);
    return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, message.getBytes(UTF_8));
  }
}
