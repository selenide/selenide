package integration.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Collections.list;
import static javax.servlet.http.HttpServletResponse.SC_OK;

class HeadersPrinterHandler extends BaseHandler {
  @Override
  public Result get(HttpServletRequest request, HttpServletResponse response) {
    List<String> headers = list(request.getHeaderNames());

    String path = request.getPathInfo().replace("/", "");
    String html = String.format("<html><body>%s!<br>", path);

    for (String header : headers) {
      for (String value : list(request.getHeaders(header))) {
        html += "<br>" + header + "=" + value;
      }
    }

    html += "</body></html>";

    return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, html);
  }
}
