package integration;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Locale;

public class UseLocaleExtension implements InvocationInterceptor {

  private String language;

  public UseLocaleExtension(String language) {
    this.language = language;
  }

  @Override
  public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                  ExtensionContext extensionContext) throws Throwable {
    Locale previous = Locale.getDefault();
    Locale.setDefault(new Locale(language));
    try {
      invocation.proceed();
    } finally {
      Locale.setDefault(previous);
    }
  }
}
