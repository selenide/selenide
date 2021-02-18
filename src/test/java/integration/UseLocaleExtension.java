package integration;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Locale;

public class UseLocaleExtension implements InvocationInterceptor {


  @Override
  public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                  ExtensionContext extensionContext) throws Throwable {
    Locale previous = Locale.getDefault();
    UseLocale useLocale = invocationContext.getExecutable().getAnnotation(UseLocale.class);
    if (useLocale == null) {
      throw new IllegalStateException("UseLocaleExtension must be used with @UseLocale");
    }
    Locale.setDefault(new Locale(useLocale.language()));
    try {
      invocation.proceed();
    } finally {
      Locale.setDefault(previous);
    }
  }
}
