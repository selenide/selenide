package integration.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Collections.list;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

class HeadersPrinterHandler extends BaseHandler {
  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) {
    List<String> headers = list(request.getHeaderNames());

    String path = request.getPathInfo().replace("/", "");
    StringBuilder html = new StringBuilder("<html><body>").append(path).append("!<br>");

    for (String header : headers) {
      for (String value : list(request.getHeaders(header))) {
        html.append("<br>").append(header).append("=").append(value);
      }
    }

    html.append("</body></html>");

    return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, html.toString());
  }
}
