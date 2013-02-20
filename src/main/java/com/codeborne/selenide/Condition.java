package com.codeborne.selenide;

import org.openqa.selenium.*;

import java.util.regex.Pattern;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.util.regex.Pattern.DOTALL;

public abstract class Condition {
  public static final Condition visible = new Condition("visible", false) {
    @Override
    public boolean apply(WebElement element) {
      return element != null && element.isDisplayed();
    }
    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exist" : element.isDisplayed() ? "visible" : "hidden";
    }
  };

  public static final Condition present = new Condition("present", false) {
    @Override
    public boolean apply(WebElement element) {
      return true;
    }

    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exist" : "exists";
    }
  };

  public static final Condition exist = present;

  public static final Condition notPresent = new Condition("notPresent", true) {
    @Override
    public boolean apply(WebElement element) {
      return element == null;
    }

    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exist" : "exists";
    }
  };

  /**
   * Checks that element is not visible or does not exists.
   */
  public static final Condition hidden = new Condition("hidden", true) {
    @Override
    public boolean apply(WebElement element) {
      try {
        return element == null || !element.isDisplayed();
      } catch (StaleElementReferenceException elementHasDisappeared) {
        return true;
      }
    }
    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exists" : element.isDisplayed() ? "visible" : "hidden";
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
  public static Condition hasAttribute(final String attributeName, final String attributeValue) {
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
        return element != null && element.getAttribute(attributeName) != null;
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : element.getAttribute(attributeName);
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
        return element != null && expectedAttributeValue.equals(getAttributeValue(element, attributeName));
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : element.getAttribute(attributeName);
      }
      @Override
      public String toString() {
        return "got attribute " + attributeName + "=" + expectedAttributeValue;
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
  public static Condition value(final String value) {
    return hasAttribute("value", value);
  }

  /**
   * $("#myInput").waitUntil(hasValue("John"), 5000)
   * @param value expected value of input field
   */
  public static Condition hasValue(final String value) {
    return value(value);
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
      return "value=" + getAttributeValue(element, "value") + ", text='" + element.getText() + "'";
    }
  };

  /**
   * $(".error_message").waitWhile(matchesText("Exception"), 12000)
   *
   * @see #matchText(String)
   */
  public static Condition matchesText(final String text) {
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
        return element != null && matches(element.getText(), regex);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : element.getText();
      }
      @Override
      public String toString() {
        return "matched text '" + regex + "'";
      }
    };
  }

  private static boolean matches(String text, String regex) {
    return Pattern.compile(".*" + regex + ".*", DOTALL).matcher(text).matches();
  }

  /**
   * $("h1").waitUntil(hasText("Hello"), 10000)
   * @param text expected text of HTML element
   */
  public static Condition hasText(final String text) {
    return text(text);
  }

  /**
   * $("h1").should(haveText("Hello\s*John"))
   * @param text expected text of HTML element
   * @deprecated Use $.shouldHave(text("Hello"))
   */
  @Deprecated
  public static Condition haveText(final String text) {
    return hasText(text);
  }

  /**
   * $("h1").shouldHave(text("Hello\s*John"))
   * @param text expected text of HTML element
   */
  public static Condition text(final String text) {
    return new Condition("text", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && element.getText().contains(text);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : element.getText();
      }
      @Override
      public String toString() {
        return "got text '" + text + "'";
      }
    };
  }

  /**
   * $("h1").shouldHave(exactText("Hello"))
   * @param text expected text of HTML element
   */
  public static Condition exactText(final String text) {
    return new Condition("exactText", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && text.equals(element.getText());
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : element.getText();
      }
      @Override
      public String toString() {
        return "got exactly the text '" + text + "'";
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
  public static Condition options = new Condition("hasOptions", false) {
    @Override
    public boolean apply(WebElement element) {
      try {
        return element != null && element.getText().length() > 0;
      }
      catch (NoSuchElementException e) {
        return false;
      }
    }
    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exist" : element.getText();
    }
    @Override
    public String toString() {
      return "got any options";
    }
  };

  public static boolean hasClass(WebElement element, String cssClass) {
    String classes = element.getAttribute("class");
    return classes == null ? false : contains(classes.split(" "), cssClass);
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
        return element != null && hasClass(element, cssClass);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : "class=" + element.getAttribute("class");
      }
      @Override
      public String toString() {
        return "got class '" + cssClass + "'";
      }
    };
  }

  public static Condition hasNotClass(final String cssClass) {
    return new Condition("hasNotClass", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && !hasClass(element, cssClass);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "does not exist" : "class=" + element.getAttribute("class");
      }
      @Override
      public String toString() {
        return "loose class '" + cssClass + "'";
      }
    };
  }

  public static final Condition focused = new Condition("focused", false) {
    @Override public boolean apply(WebElement webElement) {
      return webElement.getAttribute("name").equals(getWebDriver().findElement(By.cssSelector(":focus")).getAttribute("name"));
    }

    @Override public String actualValue(WebElement webElement) {
      WebElement element = getWebDriver().findElement(By.cssSelector(":focus"));
      return element == null? "element does not exist" : element.getAttribute("name");
    }
  };

  public static final Condition enabled = new Condition("enabled", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element == null? "does not exist" : element.isEnabled() ? "enabled" : "disabled";
    }
  };

  public static final Condition disabled = new Condition("disabled", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && !element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element == null? "does not exist" : element.isEnabled() ? "enabled" : "disabled";
    }
  };


  /**
   * "checked" attribute seems to work incorrectly in HtmlUnit
   * @deprecated Use "selected" condition
   */
  @Deprecated
  public static final Condition checked = new Condition("checked", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && "true".equalsIgnoreCase(element.getAttribute("checked"));
    }

    @Override public String actualValue(WebElement element) {
      return element == null? "does not exist" : element.getAttribute("checked");
    }
  };

  public static final Condition selected = new Condition("selected", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && element.isSelected();
    }

    @Override public String actualValue(WebElement element) {
      return element == null? "does not exist" : String.valueOf(element.isSelected());
    }
  };

  private final String name;
  private final boolean nullIsAllowed;

  public Condition(String name, boolean nullIsAllowed) {
    this.name = name;
    this.nullIsAllowed = nullIsAllowed;
  }

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
