package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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
      return element == null? "does not exists" :
          element.isDisplayed() ? "visible" : "hidden";
    }
  };

  public static final Condition present = new Condition("present", false) {
    @Override
    public boolean apply(WebElement element) {
      return true;
    }

    @Override
    public String actualValue(WebElement element) {
      return "does not exists";
    }
  };

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
      return element == null || !element.isDisplayed();
    }
    @Override
    public String actualValue(WebElement element) {
      return element == null? "does not exists" :
          element.isDisplayed() ? "visible" : "hidden";
    }
  };

  /**
   * Synonym for #visible - may be used for better readability
   * waitUntil(By.id("logoutLink"), appears);

   * Thought the same can be done in a shorter way:
   * waitFor(By.id("logoutLink");
   */
  public static final Condition appears = visible;

  /**
   * Synonym for #hidden - may be used for better readability:
   * waitUntil(By.id("loginLink"), disappears);
   */
  public static final Condition disappears = hidden;

  public static Condition hasAttribute(final String attributeName, final String attributeValue) {
    return attribute(attributeName, attributeValue);
  }

  public static Condition attribute(final String attributeName, final String attributeValue) {
    return new Condition("hasAttribute", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && attributeValue.equals(element.getAttribute(attributeName).trim());
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "element does not even exist" : element.getAttribute(attributeName);
      }
      @Override
      public String toString() {
        return "got attribute " + attributeName + "=" + attributeValue;
      }
    };
  }

  public static Condition hasValue(final String value) {
    return hasAttribute("value", value);
  }

  public static final Condition empty = hasValue("");

  /**
   * @see #matchText(String)
   */
  public static Condition matchesText(final String text) {
    return matchText(text);
  }

  /**
   * Assert that given element's text matches given regular expression
   * @param regex e.g. Kicked.*Chuck Norris   -   in this case ".*" can contain any characters including spaces, tabs, CR etc.
   * @return
   */
  public static Condition matchText(final String regex) {
    return new Condition("match", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && matches(element.getText(), regex);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "element does not even exist" :
            "<" + element.getTagName() + ">" + element.getText() + "</" + element.getTagName() + ">";
      }
      @Override
      public String toString() {
        return "matched text '" + regex + "'";
      }
    };
  }

  private static boolean matches(String text, String regex) {
    return Pattern.compile(regex, DOTALL).matcher(text).matches();
  }

  public static Condition hasText(final String text) {
    return new Condition("hasText", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && element.getText().contains(text);
      }
      @Override
      public String actualValue(WebElement element) {
        return element == null? "element does not even exist" :
            "<" + element.getTagName() + ">" + element.getText() + "</" + element.getTagName() + ">";
      }
      @Override
      public String toString() {
        return "got text '" + text + "'";
      }
    };
  }

  public static Condition haveText(final String text) {
    return hasText(text);
  }

  public static Condition text(final String text) {
    return hasText(text);
  }

  public static Condition hasOptions() {
    return new Condition("hasOptions", false) {
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
        return element == null? "element does not even exist" : element.getText();
      }
      @Override
      public String toString() {
        return "got any options";
      }
    };
  }

  public static boolean hasClass(WebElement element, String cssClass) {
    String classes = element.getAttribute("class");
    String[] classNames = classes.split(" ");
    return contains(classNames, cssClass);
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
        return element == null? "element does not even exist" :
            "<" + element.getTagName() + " @class='" + element.getAttribute("class") + "'" + ">" + element.getText() + "</" + element.getTagName() + ">";
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
        return element == null? "element does not exist" :
            "<" + element.getTagName() + " @class='" + element.getAttribute("class") + "'" + ">" + element.getText() + "</" + element.getTagName() + ">";
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
      return getWebDriver().findElement(By.cssSelector(":focus")).getAttribute("name");
    }
  };

  public static final Condition enabled = new Condition("enabled", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  public static final Condition disabled = new Condition("disabled", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && !element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  public static final Condition checked = new Condition("checked", false) {
    @Override public boolean apply(WebElement element) {
      return element != null && "true".equalsIgnoreCase(element.getAttribute("checked"));
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
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
