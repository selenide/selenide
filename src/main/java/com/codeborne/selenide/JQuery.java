package com.codeborne.selenide;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

/**
 * Avoid using this class. It's just bad to have dependency on some concrete JS framework.
 * 
 * @deprecated There are better options for every of JQuery methods.
 */
@Deprecated
public class JQuery {
  /**
   * This instance is mutable so that you can replace it with your own custom object.
   */
  public static JQuery jQuery = new JQuery();

  /**
   * Calls onclick javascript code.
   * Useful for invisible (hovered) elements that cannot be clicked directly
   * 
   * @deprecated Do not try to click invisible elements in your test. User cannot click them, so your test should not as well.  
   */
  @Deprecated
  public void onClick(By by) {
    executeJavaScript("eval(\"" + $(by).getAttribute("onclick") + "\")");
  }

  /**
   * Trigger "onchange" event on given element
   * 
   * @deprecated Not needed anymore. Methods $.setValue() and $.append() trigger change event automatically.
   */
  @Deprecated
  public void change(By by) {
    if (isJQueryAvailable()) {
      executeJQueryMethod(by, "change()");
    }
  }

  /**
   * Trigger "onchange" event on given element
   * 
   * @deprecated Not needed anymore. Methods {@code $.setValue()} and {@code $.append()} trigger change event automatically.
   */
  @Deprecated
  public void change(By by, int index) {
    if (isJQueryAvailable()) {
      executeJQueryMethod(by, "eq(" + index + ").change()");
    }
  }

  /**
   * <p>
   * @deprecated Use method {@code $(...).scrollTo()} instead - it's jquery-agnostic.
   * </p>
   *
   * <p>This method works only if jQuery "scroll" plugin is included in page being tested</p>
   *
   * @param element HTML element to scroll to.
   */
  @Deprecated
  public void scrollTo(By element) {
    if (!isJQueryAvailable()) {
      throw new IllegalStateException("JQuery is not available on current page");
    }
    executeJavaScript("jQuery.scrollTo('" + getJQuerySelector(element) + "')");
  }

  @Deprecated
  public void executeJQueryMethod(By by, String method) {
    String selector = getJQuerySelector(by);
    if (selector != null) {
      executeJavaScript("jQuery(\"" + selector + "\")." + method);
    } else {
      System.err.println("Warning: can't convert " + by + " to JQuery selector, unable to execute " + method);
    }
  }

  /**
   * @deprecated No need to depend on JQuery in your tests
   */
  @Deprecated
  public boolean isJQueryAvailable() {
    Object result = executeJavaScript("return (typeof jQuery);");
    return !"undefined".equalsIgnoreCase(String.valueOf(result));
  }

  protected String getJQuerySelector(By seleniumSelector) {
    if (seleniumSelector instanceof By.ByName) {
      String name = seleniumSelector.toString().replaceFirst("By\\.name:\\s*(.*)", "$1");
      return "*[name='" + name + "']";
    } else if (seleniumSelector instanceof By.ById) {
      String id = seleniumSelector.toString().replaceFirst("By\\.id:\\s*(.*)", "$1");
      return "#" + id;
    } else if (seleniumSelector instanceof By.ByClassName) {
      String className = seleniumSelector.toString().replaceFirst("By\\.className:\\s*(.*)", "$1");
      return "." + className;
    } else if (seleniumSelector instanceof By.ByCssSelector) {
      return seleniumSelector.toString().replaceFirst("By\\.selector:\\s*(.*)", "$1");
    } else if (seleniumSelector instanceof By.ByXPath) {
      String seleniumXPath = seleniumSelector.toString().replaceFirst("By\\.xpath:\\s*(.*)", "$1");
      return seleniumXPath.replaceFirst("^//", "").replaceFirst("^/", "")
          .replaceAll("//", " ")
          .replaceAll("/", " > ")
          .replaceAll("\\[@", "[")
          .replaceAll("\\[(\\d+)\\]", ":nth-child($1)");
    }
    return null;
  }
}
