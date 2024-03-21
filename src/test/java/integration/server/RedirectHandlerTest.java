package integration.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedirectHandlerTest {
  private final RedirectHandler handler = new RedirectHandler();
  private final HttpServletResponse response = mock();

  @Test
  void redirectsToUrl() {
    Result result = handler.get(request("/redirect/to/hello_world.txt/JzpBxy", null), response);
    assertThat(result).usingRecursiveComparison().isEqualTo(new Result(302, Map.of(
      "Location", "/hello_world.txt"
    )));
  }

  @Test
  void redirectsToUrlWithQueryParameters() {
    Result result = handler.get(request("/redirect/to/files/hello_world.txt/JzpBxy", "exposeFileName=false"), response);
    assertThat(result).usingRecursiveComparison().isEqualTo(new Result(302, Map.of(
      "Location", "/files/hello_world.txt?exposeFileName=false"
    )));
  }

  private HttpServletRequest request(String uri, @Nullable String query) {
    HttpServletRequest request = mock();
    when(request.getRequestURI()).thenReturn(uri);
    when(request.getQueryString()).thenReturn(query);
    return request;
  }
}
