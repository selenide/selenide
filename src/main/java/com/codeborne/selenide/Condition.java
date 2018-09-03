package com.codeborne.selenide;

import com.codeborne.selenide.conditions.Text;
import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * Conditions to match web elements: checks for visibility, text etc.
 */
public abstract class Condition {
  /**
   * Checks if element is visible
   *
   * <p>Sample: {@code $("input").shouldBe(visible);}</p>
   */
  public static final Condition visible = new Condition("visible") {
    @Override
    public boolean apply(Context context, WebElement element) {
      return element.isDisplayed();
    }
  };

  /**
   * Check if element exist. It can be visible or hidden.
   *
   * <p>Sample: {@code $("input").should(exist);}</p>
   */
  public static final Condition exist = new Condition("exist") {
    @Override
    public boolean apply(Context context, WebElement element) {
      try {
        element.isDisplayed();
        return true;
      }
      catch (StaleElementReferenceException e) {
        return false;
      }
    }
  };

  /**
   * @deprecated please use {@link #exist} instead, "present" is ambiguous
   * <p>
   * Synonym for {@link #exist}.
   *
   * <p>Sample: {@code $("input").shouldBe(present);}</p>
   */
  @Deprecated
  public static final Condition present = exist;

  /**
   * Checks that element is not visible or does not exists.
   * <p>
   * Opposite to {@link #appear}
   *
   * <p>Sample: {@code $("input").shouldBe(hidden);}</p>
   */
  public static final Condition hidden = new Condition("hidden", true) {
    @Override
    public boolean apply(Context context, WebElement element) {
      try {
        return !element.isDisplayed();
      }
      catch (StaleElementReferenceException elementHasDisappeared) {
        return true;
      }
    }
  };

  /**
   * Synonym for {@link #visible} - may be used for better readability
   *
   * <p>Sample: {@code $("#logoutLink").should(appear);}</p>
   */
  public static final Condition appear = visible;

  /**
   * Synonym for {@link #visible} - may be used for better readability
   * <p><code>$("#logoutLink").waitUntil(appears, 10000);</code></p>
   * <p>
   * Though the same can be done in a shorter way:
   * <p><code>waitFor(By.id("logoutLink");</code></p>
   */
  public static final Condition appears = visible;

  /**
   * Synonym for {@link #hidden} - may be used for better readability:
   *
   * <p>Sample: <code>$("#loginLink").waitUntil(disappears, 9000);</code></p>
   */
  public static final Condition disappears = hidden;

  /**
   * Synonym for {@link #hidden} - may be used for better readability:
   *
   * <p><code>$("#loginLink").should(disappear);</code></p>
   */
  public static final Condition disappear = hidden;

  /**
   * @param attributeName  name of attribute
   * @param attributeValue expected value of attribute
   * @deprecated please use {@link #attribute(String, String)} instead
   * <p>
   * Sample:
   * <code>$("#mydiv").waitUntil(hasAttribute("fileId", "12345"), 7000);</code>
   * </p>
   */
  @Deprecated
  public static Condition hasAttribute(String attributeName, String attributeValue) {
    return attribute(attributeName, attributeValue);
  }

  /**
   * Check if element has "readonly" attribute (with any value)
   *
   * <p>Sample: <code>$("input").shouldBe(readonly);</code></p>
   */
  public static final Condition readonly = attribute("readonly");

  /**
   * Check if element has given attribute (with any value)
   *
   * <p>Sample: <code>$("#mydiv").shouldHave(attribute("fileId"));</code></p>
   *
   * @param attributeName name of attribute, not null
   * @return true iff attribute exists
   */
  public static Condition attribute(final String attributeName) {
    return new Condition("attribute") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return element.getAttribute(attributeName) != null;
      }

      @Override
      public String toString() {
        return name + " " + attributeName;
      }
    };
  }

  /**
   * <p>Sample: <code>$("#mydiv").shouldHave(attribute("fileId", "12345"));</code></p>
   *
   * @param attributeName          name of attribute
   * @param expectedAttributeValue expected value of attribute
   */
  public static Condition attribute(final String attributeName, final String expectedAttributeValue) {
    return new Condition("attribute") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return expectedAttributeValue.equals(getAttributeValue(element, attributeName));
      }

      @Override
      public String toString() {
        return name + " " + attributeName + '=' + expectedAttributeValue;
      }
    };
  }

  private static String getAttributeValue(WebElement element, String attributeName) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }

  /**
   * Assert that element has given "value" attribute as substring
   * NB! Ignores difference in non-visible characters like spaces, non-breakable spaces, tabs, newlines  etc.
   *
   * <p>Sample: <code>$("input").shouldHave(value("12345 666 77"));</code></p>
   *
   * @param expectedValue expected value of "value" attribute
   */
  public static Condition value(final String expectedValue) {
    return new Condition("value") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return Html.text.contains(getAttributeValue(element, "value"), expectedValue);
      }

      @Override
      public String toString() {
        return name + " '" + expectedValue + "'";
      }
    };
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(exactValue("John"));</code></p>
   *
   * @param value expected value of input field
   */
  public static Condition exactValue(String value) {
    return attribute("value", value);
  }

  /**
   * @param value expected value of input field
   * @deprecated please use {@link #value(String)} instead
   * <p>Sample: <code>$("#myInput").waitUntil(hasValue("John"), 5000)</p>
   */
  @Deprecated
  public static Condition hasValue(String value) {
    return value(value);
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(name("username"))</code></p>
   *
   * @param name expected name of input field
   */
  public static Condition name(String name) {
    return attribute("name", name);
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(type("checkbox"))</code></p>
   *
   * @param type expected type of input field
   */
  public static Condition type(String type) {
    return attribute("type", type);
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(id("myForm"))</code></p>
   *
   * @param id expected id of input field
   */
  public static Condition id(String id) {
    return attribute("id", id);
  }

  /**
   * 1) For input element, check that value is missing or empty
   * <p>Sample: <code>$("#input").shouldBe(empty)</code></p>
   * <p>
   * 2) For other elements, check that text is empty
   * <p>Sample: <code>$("h2").shouldBe(empty)</code></p>
   */
  public static final Condition empty = and("empty", exactValue(""), exactText(""));

  /**
   * <p>Sample: <code>$(".error_message").waitWhile(matchesText("Exception"), 12000)</code></p>
   *
   * @see #matchText(String)
   */
  public static Condition matchesText(String text) {
    return matchText(text);
  }

  /**
   * Assert that given element's text matches given regular expression
   *
   * <p>Sample: <code>$("h1").should(matchText("Hello\s*John"))</code></p>
   *
   * @param regex e.g. Kicked.*Chuck Norris - in this case ".*" can contain any characters including spaces, tabs, CR etc.
   */
  public static Condition matchText(final String regex) {
    return new Condition("match text") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return Html.text.matches(element.getText(), regex);
      }

      @Override
      public String toString() {
        return name + " '" + regex + '\'';
      }
    };
  }

  /**
   * @param text expected text of HTML element
   * @deprecated please use {@link #text(String)} instead
   * <p>Sample: <code>$("h1").waitUntil(hasText("Hello"), 10000)</code></p>
   *
   * <p>Case insensitive</p>
   * <p>
   * NB! Ignores multiple whitespaces between words
   */
  @Deprecated
  public static Condition hasText(String text) {
    return text(text);
  }

  /**
   * <p>Sample: <code>$("h1").shouldHave(text("Hello\s*John"))</code></p>
   *
   * <p>NB! Case insensitive</p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition text(final String text) {
    return new Text(text);
  }

  /**
   * <p>Sample: <code>$("h1").shouldHave(textCaseSensitive("Hello\s*John"))</code></p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition textCaseSensitive(final String text) {
    return new Condition("textCaseSensitive") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return Html.text.containsCaseSensitive(element.getText(), text);
      }

      @Override
      public String toString() {
        return name + " '" + text + '\'';
      }
    };
  }

  /**
   * <p>Sample: <code>$("h1").shouldHave(exactText("Hello"))</code></p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition exactText(final String text) {
    return new Condition("exact text") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return Html.text.equals(element.getText(), text);
      }

      @Override
      public String toString() {
        return name + " '" + text + '\'';
      }
    };
  }

  /**
   * <p>Sample: <code>$("h1").shouldHave(exactTextCaseSensitive("Hello"))</code></p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition exactTextCaseSensitive(final String text) {
    return new Condition("exact text case sensitive") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return Html.text.equalsCaseSensitive(element.getText(), text);
      }

      @Override
      public String toString() {
        return name + " '" + text + '\'';
      }
    };
  }

  /**
   * @see {@link #cssClass(String)} instead of using it
   * @deprecated don't use this method, it is public by accident, and will be turned to private soon
   */
  @Deprecated
  public static boolean hasClass(WebElement element, String cssClass) {
    String classes = element.getAttribute("class");
    return classes != null && contains(classes.split(" "), cssClass);
  }

  private static <T> boolean contains(T[] objects, T object) {
    for (T object1 : objects) {
      if (object.equals(object1)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see #cssClass(String)
   *
   * <p>Sample: <code>$("input").waitUntil(hasClass("blocked"), 7000);</code></p>
   * @deprecated please use {@link #cssClass(String)} instead of this method, which is exactly the same
   */
  @Deprecated
  public static Condition hasClass(String cssClass) {
    return cssClass(cssClass);
  }

  /**
   * <p>Sample: <code>$("input").shouldHave(cssClass("active"));</code></p>
   */
  public static Condition cssClass(final String cssClass) {
    return new Condition("css class") {
      @Override
      public boolean apply(Context context, WebElement element) {
        return hasClass(element, cssClass);
      }

      @Override
      public String toString() {
        return name + " '" + cssClass + '\'';
      }
    };
  }

  /**
   * Checks if css property (style) applies for the element.
   * Both explicit and computed properties are supported.
   * <p>
   * Note that if css property is missing {@link WebElement#getCssValue} return empty string.
   * In this case you should assert against empty string.
   * <p>
   * Sample:
   * <p>
   * {@code <input style="font-size: 12">}
   * <p>
   * {@code $("input").shouldHave(cssValue("font-size", "12"));}
   * <p>
   * {@code $("input").shouldHave(cssValue("display", "block"));}
   *
   * @param propertyName  the css property (style) name  of the element
   * @param expectedValue expected value of css property
   * @see WebElement#getCssValue
   */
  public static Condition cssValue(final String propertyName, final String expectedValue) {
    return new Condition("cssValue") {
      @Override
      public boolean apply(Context context, WebElement element) {
        String actualValue = element.getCssValue(propertyName);
        return defaultString(expectedValue).equalsIgnoreCase(defaultString(actualValue));
      }

      @Override
      public String actualValue(Context context, WebElement element) {
        return element.getCssValue(propertyName);
      }

      @Override
      public String toString() {
        return name + " " + propertyName + '=' + expectedValue;
      }
    };
  }

  /**
   * Check if browser focus is currently in given element.
   */
  public static final Condition focused = new Condition("focused") {
    private WebElement getFocusedElement(Context context) {
      return (WebElement) context.executeJavaScript("return document.activeElement");
    }

    @Override
    public boolean apply(Context context, WebElement webElement) {
      WebElement focusedElement = getFocusedElement(context);
      return focusedElement != null && focusedElement.equals(webElement);
    }

    @Override
    public String actualValue(Context context, WebElement webElement) {
      WebElement focusedElement = getFocusedElement(context);
      return focusedElement == null ? "No focused focusedElement found " :
        "Focused focusedElement: " + Describe.describe(context, focusedElement) +
          ", current focusedElement: " + Describe.describe(context, webElement);
    }
  };

  /**
   * Checks that element is not disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final Condition enabled = new Condition("enabled") {
    @Override
    public boolean apply(Context context, WebElement element) {
      return element.isEnabled();
    }

    @Override
    public String actualValue(Context context, WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  /**
   * Checks that element is disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final Condition disabled = new Condition("disabled") {
    @Override
    public boolean apply(Context context, WebElement element) {
      return !element.isEnabled();
    }

    @Override
    public String actualValue(Context context, WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  /**
   * Checks that element is selected
   *
   * @see WebElement#isSelected()
   */
  public static final Condition selected = new Condition("selected") {
    @Override
    public boolean apply(Context context, WebElement element) {
      return element.isSelected();
    }

    @Override
    public String actualValue(Context context, WebElement element) {
      return String.valueOf(element.isSelected());
    }
  };

  /**
   * Checks that checkbox is checked
   *
   * @see WebElement#isSelected()
   */
  public static final Condition checked = new Condition("checked") {
    @Override
    public boolean apply(Context context, WebElement element) {
      return element.isSelected();
    }

    @Override
    public String actualValue(Context context, WebElement element) {
      return String.valueOf(element.isSelected());
    }
  };

  /**
   * Negate given condition.
   * <p>
   * Used for methods like $.shouldNot(exist), $.shouldNotBe(visible)
   * <p>
   * Typically you don't need to use it.
   */
  public static Condition not(final Condition condition) {
    return new Condition("not " + condition.name, !condition.nullIsAllowed) {
      @Override
      public boolean apply(Context context, WebElement element) {
        return !condition.apply(context, element);
      }

      @Override
      public String actualValue(Context context, WebElement element) {
        return condition.actualValue(context, element);
      }
    };
  }

  /**
   * Check if element matches ALL given conditions.
   *
   * @param name      Name of this condition, like "empty" (meaning e.g. empty text AND empty value).
   * @param condition Conditions to match.
   * @return logical AND for given conditions.
   */
  public static Condition and(String name, final Condition... condition) {
    return new Condition(name) {
      private Condition lastFailedCondition;

      @Override
      public boolean apply(Context context, WebElement element) {
        for (Condition c : condition) {
          if (!c.apply(context, element)) {
            lastFailedCondition = c;
            return false;
          }
        }
        return true;
      }

      @Override
      public String actualValue(Context context, WebElement element) {
        return lastFailedCondition == null ? null : lastFailedCondition.actualValue(context, element);
      }

      @Override
      public String toString() {
        return lastFailedCondition == null ? super.toString() : lastFailedCondition.toString();
      }
    };
  }

  /**
   * Check if element matches ANY of given conditions.
   *
   * @param name      Name of this condition, like "error" (meaning e.g. "error" OR "failed").
   * @param condition Conditions to match.
   * @return logical OR for given conditions.
   */
  public static Condition or(String name, final Condition... condition) {
    return new Condition(name) {
      private Condition firstFailedCondition;

      @Override
      public boolean apply(Context context, WebElement element) {
        for (Condition c : condition) {
          if (c.apply(context, element)) {
            return true;
          }
          else if (firstFailedCondition == null) {
            firstFailedCondition = c;
          }
        }
        return false;
      }

      @Override
      public String actualValue(Context context, WebElement element) {
        return firstFailedCondition == null ? null : firstFailedCondition.actualValue(context, element);
      }

      @Override
      public String toString() {
        return firstFailedCondition == null ? super.toString() : firstFailedCondition.toString();
      }
    };
  }

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return Condition
   */
  public static Condition be(Condition delegate) {
    return wrap("be", delegate);
  }

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return Condition
   */
  public static Condition have(Condition delegate) {
    return wrap("have", delegate);
  }

  private static Condition wrap(final String prefix, final Condition delegate) {
    return new Condition(delegate.name, delegate.applyNull()) {
      @Override
      public boolean apply(Context context, WebElement element) {
        return delegate.apply(context, element);
      }

      @Override
      public String actualValue(Context context, WebElement element) {
        return delegate.actualValue(context, element);
      }

      @Override
      public String toString() {
        return prefix + ' ' + delegate.toString();
      }
    };
  }

  private static class ExplainedCondition extends Condition {
    private final Condition delegate;
    private final String message;

    private ExplainedCondition(Condition delegate, String message) {
      super(delegate.name, delegate.nullIsAllowed);
      this.delegate = delegate;
      this.message = message;
    }

    @Override
    public boolean apply(Context context, WebElement element) {
      return delegate.apply(context, element);
    }

    @Override
    public String actualValue(Context context, WebElement element) {
      return delegate.actualValue(context, element);
    }

    @Override
    public String toString() {
      return delegate.toString() + " (because " + message + ")";
    }
  }

  protected final String name;
  protected final boolean nullIsAllowed;

  public Condition(String name) {
    this(name, false);
  }

  public Condition(String name, boolean absentElementMatchesCondition) {
    this.name = name;
    this.nullIsAllowed = absentElementMatchesCondition;
  }

  /**
   * Check if given element matches this condition.
   *
   * @param element given WebElement
   * @return true if element matches condition
   */
  public abstract boolean apply(Context context, WebElement element);

  public final boolean applyNull() {
    return nullIsAllowed;
  }

  /**
   * If element didn't match the condition, returns the actual value of element.
   * Used in error reporting.
   * Optional. Makes sense only if you need to add some additional important info to error message.
   *
   * @param context
   * @param element given WebElement
   * @return any string that needs to be appended to error message.
   */
  public String actualValue(Context context, WebElement element) {
    return null;
  }

  /**
   * Should be used for explaining the reason of condition
   */
  public Condition because(String message) {
    return new ExplainedCondition(this, message);
  }

  @Override
  public String toString() {
    return name;
  }

}
