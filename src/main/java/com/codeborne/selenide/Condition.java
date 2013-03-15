package com.codeborne.selenide;

import com.codeborne.selenide.impl.Describe;
import com.google.common.base.Predicate;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.getFocusedElement;
import static java.util.regex.Pattern.DOTALL;

public abstract class Condition implements Predicate<WebElement> {
  public static Condition not(final Condition condition) {
    return new Condition("not(" + condition.name + ')', false) {
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

  public static final Condition visible = new Condition("visible", false) {
    @Override
    public boolean apply(WebElement element) {
      return element.isDisplayed();
    }
    @Override
    public String actualValue(WebElement element) {
      return element.isDisplayed() ? "visible" : "hidden";
    }
  };

  public static final Condition present = new Condition("present", false) {
    @Override
    public boolean apply(WebElement element) {
      return true;
    }

    @Override
    public String actualValue(WebElement element) {
      return "exists";
    }
  };

  public static final Condition exist = present;

  public static final Condition notPresent = new Condition("notPresent", true) {
    @Override
    public boolean apply(WebElement element) {
      return false;
    }

    @Override
    public String actualValue(WebElement element) {
      return "exists";
    }
  };

  /**
   * Checks that element is not visible or does not exists.
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
    @Override
    public String actualValue(WebElement element) {
      return element.isDisplayed() ? "visible" : "hidden";
    }
  };

  /**
   * Synonym for #visible - may be used for better readability
   * $("#logoutLink").should(appear);
   */
  public static final Condition appear = visible;

  /**
   * Synonym for #visible - may be used for better readability
   * $("#logoutLink").waitUntil(appears, 10000);

   * Thought the same can be done in a shorter way:
   * waitFor(By.id("logoutLink");
   */
  public static final Condition appears = visible;

  /**
   * Synonym for #hidden - may be used for better readability:
   * $("#loginLink").waitUntil(disappears, 9000);
   */
  public static final Condition disappears = hidden;

  /**
     * Synonym for #hidden - may be used for better readability:
     * $("#loginLink").should(disappear);
     */
  public static final Condition disappear = hidden;

  /**
   * $("#mydiv").waitUntil(hasAttribute("fileId", "12345"), 7000);
   * @param attributeName name of attribute
   * @param attributeValue expected value of attribute
   */
  public static Condition hasAttribute(String attributeName, String attributeValue) {
    return attribute(attributeName, attributeValue);
  }

  /**
   * Check if element has "readonly" attribute (with any value)
   */
  public static final Condition readonly = attribute("readonly");

  /**
   * Check if element has given attribute (with any value)
   *
   * @param attributeName name of attribute, not null
   * @return true iff attribute exists
   */
  public static Condition attribute(final String attributeName) {
    return new Condition("hasAttribute", false) {
      @Override
      public boolean apply(WebElement element) {
        return element.getAttribute(attributeName) != null;
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getAttribute(attributeName);
      }
      @Override
      public String toString() {
        return "got attribute " + attributeName;
      }
    };
  }

  /**
   * $("#mydiv").shouldHave(attribute("fileId", "12345"));
   * @param attributeName name of attribute
   * @param expectedAttributeValue expected value of attribute
   */
  public static Condition attribute(final String attributeName, final String expectedAttributeValue) {
    return new Condition("hasAttribute", false) {
      @Override
      public boolean apply(WebElement element) {
        return expectedAttributeValue.equals(getAttributeValue(element, attributeName));
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getAttribute(attributeName);
      }
      @Override
      public String toString() {
        return "got attribute " + attributeName + '=' + expectedAttributeValue;
      }
    };
  }

  private static String getAttributeValue(WebElement element, String attributeName) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr.trim();
  }

  /**
   * $("#input").shouldHave(value("John"))
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
   * $("#input").shouldHave(name("username"))
   * @param name expected name of input field
   */
  public static Condition name(String name) {
    return hasAttribute("name", name);
  }

  /**
   * $("#input").shouldHave(type("checkbox"))
   * @param type expected type of input field
   */
  public static Condition type(String type) {
    return hasAttribute("type", type);
  }

  /**
   * $("#input").shouldHave(id("myForm"))
   * @param id expected id of input field
   */
  public static Condition id(String id) {
    return hasAttribute("id", id);
  }

  /**
   * 1) For input element, check that value is missing or empty
   * $("#input").shouldBe(empty)
   *
   * 2) For other elements, check that text is empty
   * $("h2").shouldBe(empty)
   */
  public static final Condition empty = new Condition("empty", false) {
    private final Condition emptyValue = value("");
    private final Condition emptyText = exactText("");

    @Override
    public boolean apply(WebElement element) {
      return emptyValue.apply(element) && emptyText.apply(element);
    }

    @Override
    public String actualValue(WebElement element) {
      return "value=" + getAttributeValue(element, "value") + ", text='" + element.getText() + '\'';
    }
  };

  /**
   * $(".error_message").waitWhile(matchesText("Exception"), 12000)
   *
   * @see #matchText(String)
   */
  public static Condition matchesText(String text) {
    return matchText(text);
  }

  /**
   * Assert that given element's text matches given regular expression
   *
   * $("h1").should(matchText("Hello\s*John"))
   *
   * @param regex e.g. Kicked.*Chuck Norris   -   in this case ".*" can contain any characters including spaces, tabs, CR etc.
   */
  public static Condition matchText(final String regex) {
    return new Condition("match", false) {
      @Override
      public boolean apply(WebElement element) {
        return matches(element.getText(), regex);
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getText();
      }
      @Override
      public String toString() {
        return "matched text '" + regex + '\'';
      }
    };
  }

  private static boolean matches(String text, String regex) {
    return Pattern.compile(".*" + regex + ".*", DOTALL).matcher(text).matches();
  }

  /**
   * $("h1").waitUntil(hasText("Hello"), 10000)
   * <p>Case insensitive</p>
   * @param text expected text of HTML element
   */
  public static Condition hasText(String text) {
    return text(text);
  }

  /**
   * $("h1").shouldHave(text("Hello\s*John"))
   * <p>Case insensitive</p>
   * @param text expected text of HTML element
   */
  public static Condition text(final String text) {
    return new Condition("text", false) {
      @Override
      public boolean apply(WebElement element) {
        return element.getText().toLowerCase().contains(text.toLowerCase());
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getText();
      }
      @Override
      public String toString() {
        return "got text '" + text + '\'';
      }
    };
  }

  /**
   * $("h1").shouldHave(textCaseSensitive("Hello\s*John"))
   * @param text expected text of HTML element
   */
  public static Condition textCaseSensitive(final String text) {
    return new Condition("textCaseSensitive", false) {
      @Override
      public boolean apply(WebElement element) {
        return element.getText().contains(text);
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getText();
      }
      @Override
      public String toString() {
        return "got text '" + text + '\'';
      }
    };
  }

  /**
   * $("h1").shouldHave(exactText("Hello"))
   * <p>Case insensitive</p>
   * @param text expected text of HTML element
   */
  public static Condition exactText(final String text) {
    return new Condition("exactText", false) {
      @Override
      public boolean apply(WebElement element) {
        return text.equalsIgnoreCase(element.getText());
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getText();
      }
      @Override
      public String toString() {
        return "got exactly the text '" + text + '\'';
      }
    };
  }

  /**
   * $("h1").shouldHave(exactText("Hello"))
   * @param text expected text of HTML element
   */
  public static Condition exactTextCaseSensitive(final String text) {
    return new Condition("exactTextCaseSensitive", false) {
      @Override
      public boolean apply(WebElement element) {
        return text.equals(element.getText());
      }
      @Override
      public String actualValue(WebElement element) {
        return element.getText();
      }
      @Override
      public String toString() {
        return "got exactly the text '" + text + '\'';
      }
    };
  }

  /**
   * $("#my-select-box").waitUntil(hasOptions(), 7000);
   */
  public static Condition hasOptions() {
    return options;
  }

  /**
   * $("input").shouldHave(options);
   */
  public static final Condition options = new Condition("hasOptions", false) {
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
      return "got any options";
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

  public static Condition cssClass(String cssClass) {
    return hasClass(cssClass);
  }

  public static Condition hasClass(final String cssClass) {
    return new Condition("hasClass", false) {
      @Override
      public boolean apply(WebElement element) {
        return hasClass(element, cssClass);
      }
      @Override
      public String actualValue(WebElement element) {
        return "class=" + element.getAttribute("class");
      }
      @Override
      public String toString() {
        return "got class '" + cssClass + '\'';
      }
    };
  }

  public static Condition hasNotClass(final String cssClass) {
    return new Condition("hasNotClass", false) {
      @Override
      public boolean apply(WebElement element) {
        return !hasClass(element, cssClass);
      }
      @Override
      public String actualValue(WebElement element) {
        return "class=" + element.getAttribute("class");
      }
      @Override
      public String toString() {
        return "lose class '" + cssClass + '\'';
      }
    };
  }

  public static final Condition focused = new Condition("focused", false) {
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

  public static final Condition enabled = new Condition("enabled", false) {
    @Override public boolean apply(WebElement element) {
      return element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  public static final Condition disabled = new Condition("disabled", false) {
    @Override public boolean apply(WebElement element) {
      return !element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  public static final Condition selected = new Condition("selected", false) {
    @Override public boolean apply(WebElement element) {
      return element.isSelected();
    }

    @Override public String actualValue(WebElement element) {
      return String.valueOf(element.isSelected());
    }
  };

  private final String name;
  private final boolean nullIsAllowed;

  protected Condition(String name, boolean nullIsAllowed) {
    this.name = name;
    this.nullIsAllowed = nullIsAllowed;
  }

  @Override
  public abstract boolean apply(WebElement element);

  public final boolean applyNull() {
    return nullIsAllowed;
  }

  public abstract String actualValue(WebElement element);

  @Override
  public String toString() {
    return "become " + name;
  }
}
