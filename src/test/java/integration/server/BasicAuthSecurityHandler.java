package integration.server;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;

import static org.eclipse.jetty.util.security.Credential.getCredential;

public class BasicAuthSecurityHandler extends ConstraintSecurityHandler {
  public BasicAuthSecurityHandler(String context, String username, String password, String realm) {
    setAuthenticator(new BasicAuthenticator());
    setRealmName("myrealm");
    addConstraintMapping(constraintMapping(context));
    setLoginService(loginService(username, password, realm));
  }

  private ConstraintMapping constraintMapping(String context) {
    ConstraintMapping constraintMapping = new ConstraintMapping();
    constraintMapping.setConstraint(constraint());
    constraintMapping.setPathSpec(context);
    return constraintMapping;
  }

  private Constraint constraint() {
    Constraint constraint = new Constraint();
    constraint.setName(Constraint.__BASIC_AUTH);
    constraint.setRoles(new String[]{"user" });
    constraint.setAuthenticate(true);
    return constraint;
  }

  private HashLoginService loginService(String username, String password, String realm) {
    HashLoginService loginService = new HashLoginService();
    loginService.setUserStore(userStore(username, password));
    loginService.setName(realm);
    return loginService;
  }

  private UserStore userStore(String username, String password) {
    UserStore userStore = new UserStore();
    userStore.addUser(username, getCredential(password), new String[]{"user" });
    return userStore;
  }
}
