package integration.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import org.eclipse.jetty.security.authentication.LoginAuthenticator;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.UserIdentity;

public class BearerTokenAuthenticator extends LoginAuthenticator {
  @Override
  public String getAuthMethod() {
    return "BEARER";
  }

  @Override
  public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory) throws ServerAuthException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());

    try {
      if (!mandatory)
        return new DeferredAuthentication(this);

      if (credentials != null) {
        int space = credentials.indexOf(' ');
        if (space > 0) {
          String method = credentials.substring(0, space);
          if ("bearer".equalsIgnoreCase(method)) {
            credentials = credentials.substring(space + 1);
            Charset charset = StandardCharsets.UTF_8;
            credentials = new String(Base64.getDecoder().decode(credentials), charset);
            int i = credentials.indexOf(':');
            if (i > 0) {
              String username = credentials.substring(0, i);
              String password = credentials.substring(i + 1);

              UserIdentity user = login(username, password, request);
              if (user != null)
                return new UserAuthentication(getAuthMethod(), user);
            }
          }
        }
      }

      if (DeferredAuthentication.isDeferred(response))
        return Authentication.UNAUTHENTICATED;

      String value = "basic realm=\"" + _loginService.getName() + "\"";
      Charset charset = StandardCharsets.UTF_8;
      value += ", charset=\"" + charset.name() + "\"";
      response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), value);
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return Authentication.SEND_CONTINUE;
    }
    catch (IOException e) {
      throw new ServerAuthException(e);
    }
  }

  @Override
  public boolean secureResponse(ServletRequest req, ServletResponse res, boolean mandatory, User validatedUser) {
    return true;
  }
}
