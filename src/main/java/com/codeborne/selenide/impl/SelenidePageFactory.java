package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see <a href="https://github.com/SeleniumHQ/selenium/wiki/PageObjects">Page Objects Wiki</a>
 */
public class SelenidePageFactory {
  public <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass) {
    try {
      return createPageObject(pageObjectClass, driver);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create new instance of " + pageObjectClass, e);
    }
  }

  @SuppressWarnings("unchecked")
  private <PageObjectClass> PageObjectClass createPageObject(Class<PageObjectClass> pageObjectClass, Driver driver)
    throws Exception {

    if ((pageObjectClass.getModifiers() & Modifier.FINAL) != Modifier.FINAL
      && hasConstructor(pageObjectClass)) {

      DriverOrchestrator orchestrator = getDriverOrchestrator();

      if (orchestrator != null) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(pageObjectClass);
        proxyFactory.setFilter(m -> !m.getName().equals("finalize"));

        Class<?> proxyClass = proxyFactory.createClass();
        Constructor<?> constructor = proxyClass.getConstructor();
        constructor.setAccessible(true);

        PageObjectClass wrapper = (PageObjectClass) constructor.newInstance();
        ((Proxy) wrapper).setHandler(new PageObjectInvocationHandler(driver, orchestrator));

        orchestrator.using(driver, () -> {
          initPageObject(driver, wrapper);
          return null;
        });

        return wrapper;
      }
    }

    Constructor<PageObjectClass> constructor = pageObjectClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    PageObjectClass target = constructor.newInstance();
    initPageObject(driver, target);

    return target;
  }

  private <PageObjectClass> boolean hasConstructor(Class<PageObjectClass> pageObjectClass) {
    try {
      pageObjectClass.getConstructor();
      return true;
    } catch (NoSuchMethodException ignored) {
      return false;
    }
  }

  @Nullable
  private DriverOrchestrator getDriverOrchestrator()
    throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    // only if there is static facade in classpath
    Class<?> aClass;
    try {
      aClass = Class.forName("com.codeborne.selenide.impl.DriverOrchestratorImpl");
    } catch (ClassNotFoundException | NoClassDefFoundError e) {
      return null;
    }
    return (DriverOrchestrator) aClass.getDeclaredConstructor().newInstance();
  }

  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject) {
    return initPageObject(driver, pageObject);
  }

  private <PageObjectClass, T extends PageObjectClass> PageObjectClass initPageObject(Driver driver, T pageObject) {
    initElements(new SelenideFieldDecorator(this, driver, driver.getWebDriver()), pageObject);
    return pageObject;
  }

  /**
   * Similar to the other "initElements" methods, but takes an {@link FieldDecorator} which is used
   * for decorating each of the fields.
   *
   * @param decorator the decorator to use
   * @param page      The object to decorate the fields of
   */
  public void initElements(FieldDecorator decorator, Object page) {
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class) {
      proxyFields(decorator, page, proxyIn);
      proxyIn = proxyIn.getSuperclass();
    }
  }

  private void proxyFields(FieldDecorator decorator, Object page, Class<?> proxyIn) {
    Field[] fields = proxyIn.getDeclaredFields();
    for (Field field : fields) {
      if (isInitialized(page, field)) {
        continue;
      }
      Object value = decorator.decorate(page.getClass().getClassLoader(), field);
      if (value != null) {
        try {
          field.setAccessible(true);
          field.set(page, value);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private boolean isInitialized(Object page, Field field) {
    try {
      field.setAccessible(true);
      return field.get(page) != null;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private final static class PageObjectInvocationHandler implements MethodHandler {
    private final Driver driver;
    private final DriverOrchestrator orchestrator;

    private PageObjectInvocationHandler(Driver driver, DriverOrchestrator orchestrator) {
      this.driver = driver;
      this.orchestrator = orchestrator;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
      String methodName = thisMethod.getName();
      if (methodName.equals("toString")
        || methodName.equals("equals")
        || methodName.equals("hashCode")) {
        return proceed.invoke(self, args);
      }

      Object result;
      try {
        result = orchestrator.using(driver, () ->
          proceed.invoke(self, args)
        );
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      }

      return result;
    }
  }
}
