package com.codeborne.selenide;

import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.Html;
import com.google.common.base.Predicate;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.getFocusedElement;

public abstract class Condition implements Predicate<WebElement> {
  /**
   * <code>$("input").shouldBe(visible);</code>
   */
  public static final Condition visible = new Condition("visible") {
    @Override
    public boolean apply(WebElement element) {
      return element.isDisplayed();
    }
  };

  /**
   * Check if element exist. It can be visible or hidden.
   *
   * <code>$("input").should(exist);</code>
   */
  public static final Condition exist = new Condition("exist") {
    @Override
    public boolean apply(WebElement element) {
      element.isDisplayed();
      return true;
    }
  };

  /**
   * @see #exist
   *
   * <code>$("input").shouldBe(present);</code>
   */
  public static final Condition present = exist;

  /**
   * <code>$("input").should(notPresent);</code>
   *
   * @deprecated Use method $.shouldNot(exist) or $.shouldNotBe(present).
   */
  @Deprecated
  public static final Condition notPresent = new Condition("notPresent", true) {
    @Override
    public boolean apply(WebElement element) {
      return false;
    }
  };

  /**
   * Checks that element is not visible or does not exists.
   *
   * Opposite to com.codeborne.selenide.Condition#appear
   *
   * <code>$("input").shouldBe(hidden);</code>
   */
  public static final Condition hidden = new Condition("hidden", true) {
    @Override
    public boolean apply(WebElement element) {
      try {
        return !element.isDisplayed();
      } catch (StaleElementReferenceException elementHasDisappeared) {
        return true;
      }
    }
  };

  /**
   * Synonym for com.codeborne.selenide.Condition#appear - may be used for better readability
   *
   * <code>$("#logoutLink").should(appear);</code>
   */
  public static final Condition appear = visible;

  /**
   * Synonym for #visible - may be used for better readability
   * $("#logoutLink").waitUntil(appears, 10000);

   * Thought the same can be done in a shorter way:
   * <code>waitFor(By.id("logoutLink");</code>
   */
  public static final Condition appears = visible;

  /**
   * Synonym for com.codeborne.selenide.Condition#hidden - may be used for better readability:
   *
   * <code>$("#loginLink").waitUntil(disappears, 9000);</code>
   */
  public static final Condition disappears = hidden;

  /**
     * Synonym for com.codeborne.selenide.Condition#hidden - may be used for better readability:
   *
     * $("#loginLink").should(disappear);
     */
  public static final Condition disappear = hidden;

  /**
   * <code>$("#mydiv").waitUntil(hasAttribute("fileId", "12345"), 7000);</code>
   * @param attributeName name of attribute
   * @param attributeValue expected value of attribute
   */
  public static Condition hasAttribute(String attributeName, String attributeValue) {
    return attribute(attributeName, attributeValue);
  }

  /**
   * Check if element has "readonly" attribute (with any value)
   *
   * <code>$("input").shouldBe(readonly);</code>
   */
  public static final Condition readonly = attribute("readonly");

  /**
   * Check if element has given attribute (with any value)
   *
   * <code>$("#mydiv").shouldHave(attribute("fileId"));</code>
   *
   * @param attributeName name of attribute, not null
   * @return true iff attribute exists
   */
  public static Condition attribute(final String attributeName) {
    return new Condition("hasAttribute") {
      @Override
      public boolean apply(WebElement element) {
        return element.getAttribute(attributeName) != null;
      }
      @Override
      public String toString() {
        return "attribute " + attributeName;
      }
    };
  }

  /**
   * <code>$("#mydiv").shouldHave(attribute("fileId", "12345"));</code>
   *
   * @param attributeName name of attribute
   * @param expectedAttributeValue expected value of attribute
   */
  public static Condition attribute(final String attributeName, final String expectedAttributeValue) {
    return new Condition("hasAttribute") {
      @Override
      public boolean apply(WebElement element) {
        return expectedAttributeValue.equals(getAttributeValue(element, attributeName));
      }
      @Override
      public String toString() {
        return "attribute " + attributeName + '=' + expectedAttributeValue;
      }
    };
  }

  private static String getAttributeValue(WebElement element, String attributeName) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr.trim();
  }

  /**
   * <code>$("#input").shouldHave(value("John"));</code>
   * @param value expected value of input field
   */
  public static Condition value(String value) {
    return hasAttribute("value", value);
  }

  /**
   * $("#myInput").waitUntil(hasValue("John"), 5000)
   * @param value expected value of input field
   */
  public static Condition hasValue(String value) {
    return value(value);
  }

  /**
   * <code>$("#input").shouldHave(name("username"))</code>
   * @param name expected name of input field
   */
  public static Condition name(String name) {
    return hasAttribute("name", name);
  }

  /**
   * <code>$("#input").shouldHave(type("checkbox"))</code>
   * @param type expected type of input field
   */
  public static Condition type(String type) {
    return hasAttribute("type", type);
  }

  /**
   * <code>$("#input").shouldHave(id("myForm"))</code>
   * @param id expected id of input field
   */
  public static Condition id(String id) {
    return hasAttribute("id", id);
  }

  /**
   * 1) For input element, check that value is missing or empty
   * <code>$("#input").shouldBe(empty)</code>
   *
   * 2) For other elements, check that text is empty
   * <code>$("h2").shouldBe(empty)</code>
   */
  public static final Condition empty = and("empty", value(""), exactText(""));

  /**
   * <code>$(".error_message").waitWhile(matchesText("Exception"), 12000)</code>
   *
   * @see #matchText(String)
   */
  public static Condition matchesText(String text) {
    return matchText(text);
  }

  /**
   * Assert that given element's text matches given regular expression
   *
   * <code>$("h1").should(matchText("Hello\s*John"))</code>
   *
   * @param regex e.g. Kicked.*Chuck Norris   -   in this case ".*" can contain any characters including spaces, tabs, CR etc.
   */
  public static Condition matchText(final String regex) {
    return new Condition("match") {
      @Override
      public boolean apply(WebElement element) {
        return Html.text.matches(element.getText(), regex);
      }
      @Override
      public String toString() {
        return "match text '" + regex + '\'';
      }
    };
  }

  /**
   * <code>$("h1").waitUntil(hasText("Hello"), 10000)</code>
   *
   * <p>Case insensitive</p>
   *
   * NB! Ignores multiple whitespaces between words
   * @param text expected text of HTML element
   */
  public static Condition hasText(String text) {
    return text(text);
  }

  /**
   * <code>$("h1").shouldHave(text("Hello\s*John"))</code>
   *
   * <p>NB! Case insensitive</p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition text(final String text) {
    return new Condition("text") {
      @Override
      public boolean apply(WebElement element) {
        return Html.text.contains(element.getText(), text.toLowerCase());
      }

      @Override
      public String toString() {
        return "text '" + text + '\'';
      }
    };
  }

  /**
   * <code>$("h1").shouldHave(textCaseSensitive("Hello\s*John"))</code>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition textCaseSensitive(final String text) {
    return new Condition("textCaseSensitive") {
      @Override
      public boolean apply(WebElement element) {
        return Html.text.containsCaseSensitive(element.getText(), text);
      }
      @Override
      public String toString() {
        return "text '" + text + '\'';
      }
    };
  }

  /**
   * $("h1").shouldHave(exactText("Hello"))
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition exactText(final String text) {
    return new Condition("exactText") {
      @Override
      public boolean apply(WebElement element) {
        return Html.text.equals(element.getText(), text);
      }
      @Override
      public String toString() {
        return "exactly the text '" + text + '\'';
      }
    };
  }

  /**
   * <code>$("h1").shouldHave(exactTextCaseSensitive("Hello"))</code>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  public static Condition exactTextCaseSensitive(final String text) {
    return new Condition("exactTextCaseSensitive") {
      @Override
      public boolean apply(WebElement element) {
        return Html.text.equalsCaseSensitive(element.getText(), text);
      }
      @Override
      public String toString() {
        return "exactly the text '" + text + '\'';
      }
    };
  }

  /**
   * <code>$("#my-select-box").waitUntil(hasOptions(), 7000);</code>
   * @deprecated
   * @see Condition#options
   */
  @Deprecated
  public static Condition hasOptions() {
    return options;
  }

  /**
   * <code>$("input").shouldHave(options);</code>
   *
   * @deprecated Not needed anymore. Use methods $.selectOption() or $.selectOptionByValue().
   */
  @Deprecated
  public static final Condition options = new Condition("hasOptions") {
    @Override
    public boolean apply(WebElement element) {
      try {
        return element.getText().length() > 0;
      }
      catch (NoSuchElementException e) {
        return false;
      }
    }
    @Override
    public String actualValue(WebElement element) {
      return element.getText();
    }
    @Override
    public String toString() {
      return "any options";
    }
  };

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
   * Usage:
   * <code>$("input").shouldHave(cssClass("active"));</code>
   */
  public static Condition cssClass(String cssClass) {
    return hasClass(cssClass);
  }

  /**
   * Usage:
   * <code>$("input").waitUntil(hasClass("blocked"), 7000);</code>
   */
  public static Condition hasClass(final String cssClass) {
    return new Condition("hasClass") {
      @Override
      public boolean apply(WebElement element) {
        return hasClass(element, cssClass);
      }
      @Override
      public String toString() {
        return "CSS class '" + cssClass + '\'';
      }
    };
  }

  /**
   * @deprecated Use method $.shouldNotHave(cssClass("abc"))
   */
  @Deprecated
  public static Condition hasNotClass(final String cssClass) {
    return new Condition("hasNotClass") {
      @Override
      public boolean apply(WebElement element) {
        return !hasClass(element, cssClass);
      }
      @Override
      public String toString() {
        return "no CSS class '" + cssClass + '\'';
      }
    };
  }

  /**
   * Check if browser focus is currently in given element.
   */
  public static final Condition focused = new Condition("focused") {
    @Override public boolean apply(WebElement webElement) {
      WebElement focusedElement = getFocusedElement();
      return focusedElement!= null && focusedElement.equals(webElement);
    }

    @Override public String actualValue(WebElement webElement) {
      WebElement focusedElement = getFocusedElement();
      return focusedElement == null? "No focused focusedElement found " :
          "Focused focusedElement: " + Describe.describe(focusedElement) +
          ", current focusedElement: " + Describe.describe(webElement);
    }
  };

  /**
   * Checks that element is not disabled
   * @see WebElement#isEnabled()
   */
  public static final Condition enabled = new Condition("enabled") {
    @Override public boolean apply(WebElement element) {
      return element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  /**
   * Checks that element is disabled
   * @see WebElement#isEnabled()
   */
  public static final Condition disabled = new Condition("disabled") {
    @Override public boolean apply(WebElement element) {
      return !element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  /**
   * Checks that element is selected
   * @see WebElement#isSelected()
   */
  public static final Condition selected = new Condition("selected") {
    @Override public boolean apply(WebElement element) {
      return element.isSelected();
    }

    @Override public String actualValue(WebElement element) {
      return String.valueOf(element.isSelected());
    }
  };

  /**
   * Negate given condition.
   *
   * Used for methods like $.shouldNot(exist), $.shouldNotBe(visible)
   *
   * Typically you don't need to use it.
   */
  public static Condition not(final Condition condition) {
    return new Condition("not(" + condition.name + ')', !condition.nullIsAllowed) {
      @Override
      public boolean apply(WebElement element) {
        return !condition.apply(element);
      }

      @Override
      public String actualValue(WebElement element) {
        return condition.actualValue(element);
      }
    };
  }

  /**
   * Check if element matches all given conditions.
   * @param name Name of this condition, like "empty" (that means empty text AND empty value).
   * @param condition Conditions to match.
   * @return logical AND for given conditions.
   */
  public static Condition and(String name, final Condition... condition) {
    return new Condition(name) {
      protected Condition lastFailedCondition;

      @Override
      public boolean apply(WebElement element) {
        for (Condition c : condition) {
          if (!c.apply(element)) {
            lastFailedCondition = c;
            return false;
          }
        }
        return true;
      }

      @Override
      public String actualValue(WebElement element) {
        return lastFailedCondition == null ? null : lastFailedCondition.actualValue(element);
      }

      @Override
      public String toString() {
        return lastFailedCondition == null ? super.toString() : lastFailedCondition.toString();
      }
    };
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
  @Override
  public abstract boolean apply(WebElement element);

  public final boolean applyNull() {
    return nullIsAllowed;
  }

  /**
   * If element didn't match the condition, returns the actual value of element.
   * Used in error reporting.
   * Optional. Makes sense only if you need to add some additional important info to error message.
   *
   * @param element given WebElement
   * @return any string that needs to be appended to error message.
   */
  public String actualValue(WebElement element) {
    return null;
  }

  @Override
  public String toString() {
    return name;
  }
}
