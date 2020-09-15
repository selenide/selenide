package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sample output:
 *
 * Element should have text '666' {By.id: result}
 * Element: '<TextView class="android.widget.TextView" id="com.android.calculator2:id/result" name="6" displayed="true" checked="false" enabled="true" focused="false" bounds="[0,183][1080,584]" contentDescription="null" contentSize="{"width":1080,"height":401,"top":183,"left":0,"scrollableOffset":0,"touchPadding":24}" package="com.android.calculator2">6</TextView>'
 * Screenshot: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.png
 * Page source: file:/Users/andrei/projects/selenide-appium/build/reports/tests/1599256941895.0.html
 *
 * TODO Make all these calls "safe": if element has disappeared meanwhile, we should print at least of element details.
 */
@ParametersAreNonnullByDefault
public class AppiumElementDescriber implements ElementDescriber {
  @Nonnull
  @Override
  public String fully(Driver driver, @Nullable WebElement element) {
    String klass = element.getAttribute("class"); // android.widget.TextView
    String tagName = klass.replaceFirst(".+\\.(.+)", "$1");
    String id = element.getAttribute("resource-id");
    String checkable = element.getAttribute("checkable");
    String checked = element.getAttribute("checked");
    String clickable = element.getAttribute("clickable");
    String contentDescription = element.getAttribute("content-desc");
    String enabled = element.getAttribute("enabled");
    String focusable = element.getAttribute("focusable");
    String focused = element.getAttribute("focused");
    String packageName = element.getAttribute("package");
    String text = element.getAttribute("text");
    String name = element.getAttribute("name");
    String bounds = element.getAttribute("bounds");
    String displayed = element.getAttribute("displayed");
    String contentSize = element.getAttribute("contentSize");
    return String.format("<%s class=\"%s\" id=\"%s\" name=\"%s\" displayed=\"%s\" checked=\"%s\" enabled=\"%s\" focused=\"%s\" bounds=\"%s\" contentDescription=\"%s\" contentSize=\"%s\" package=\"%s\">%s</%s>",
      tagName, klass, id, name, displayed, checked, enabled, focused, bounds, contentDescription, contentSize, packageName, text, tagName);
  }

  @Nonnull
  @Override
  public String briefly(Driver driver, @Nonnull WebElement element) {
    String klass = element.getAttribute("class"); // android.widget.TextView
    String tagName = klass.replaceFirst(".+\\.(.+)", "$1");
    String id = element.getAttribute("resource-id");
    String text = element.getAttribute("text");
    return String.format("<%s class=\"%s\" id=\"%s\">%s</%s>", tagName, klass, id, text, tagName);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String selector(By selector) {
    if (selector instanceof By.ByCssSelector) {
      return selector.toString()
        .replace("By.selector: ", "")
        .replace("By.cssSelector: ", "");
    }
    return selector.toString();
  }
}
