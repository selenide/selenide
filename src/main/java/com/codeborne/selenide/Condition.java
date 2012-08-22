package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

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
    return new Condition("hasAttribute", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && attributeValue.equals(element.getAttribute(attributeName));
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

  protected Condition focused = new Condition("focused") {
    @Override public boolean apply(WebElement webElement) {
      return webElement.getAttribute("name").equals(getWebDriver().findElement(By.cssSelector(":focus")).getAttribute("name"));
    }

    @Override public String actualValue(WebElement webElement) {
      return getWebDriver().findElement(By.cssSelector(":focus")).getAttribute("name");
    }
  };

  public static final Condition enabled = new Condition("enabled") {
    @Override public boolean apply(WebElement element) {
      return element != null && element.isEnabled();
    }

    @Override public String actualValue(WebElement element) {
      return element.isEnabled() ? "enabled" : "disabled";
    }
  };

  protected Condition empty = hasValue("");

  private final String name;
  private final boolean nullIsAllowed;

  public Condition(String name, boolean nullIsAllowed) {
    this.name = name;
    this.nullIsAllowed = nullIsAllowed;
  }

  public Condition(String name) {
    this(name, false);
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
