package com.codeborne.selenide;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public abstract class Condition {
  public static final Condition visible = new Condition("visible") {
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

  public static final Condition hidden = new Condition("hidden") {
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

  public static Condition hasAttribute(final String attributeName, final String attributeValue) {
    return new Condition("hasAttribute") {
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
    return new Condition("hasText") {
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
    return new Condition("hasOptions") {
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
    return new Condition("hasClass") {
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
    return new Condition("hasNotClass") {
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

  private final String name;

  public Condition(String name) {
    this.name = name;
  }

  public abstract boolean apply(WebElement element);

  public abstract String actualValue(WebElement element);

  @Override
  public String toString() {
    return "become " + name;
  }
}
