package com.github.selenide;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public abstract class Condition {
  public static final Condition visible = new Condition("visible", false) {
    @Override
    public boolean apply(WebElement element) {
      return element != null && element.isDisplayed();
    }
    @Override
    public Object actualValue(WebElement element) {
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
    public Object actualValue(WebElement element) {
      return element == null? "does not exists" :
          element.isDisplayed() ? "visible" : "hidden";
    }
  };

  public static Condition hasAttribute(final String attributeName, final String attributeValue) {
    return new Condition("hasAttribute", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && attributeValue.equals(element.getAttribute(attributeName));
      }
      @Override
      public Object actualValue(WebElement element) {
        return element == null? "element does not even exist" : element.getAttribute(attributeName);
      }
      @Override
      public String toString() {
        return "got attribute " + attributeName + "=" + attributeValue;
      }
    };
  }

  public static Condition hasText(final String text) {
    return new Condition("hasText", false) {
      @Override
      public boolean apply(WebElement element) {
        return element != null && element.getText().contains(text);
      }
      @Override
      public Object actualValue(WebElement element) {
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
      public Object actualValue(WebElement element) {
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
    for (int i=0; i<objects.length; i++) {
      if (object.equals(objects[i])) {
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
      public Object actualValue(WebElement element) {
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
      public Object actualValue(WebElement element) {
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
  private final boolean nullIsAllowed;

  public Condition(String name, boolean nullIsAllowed) {
    this.name = name;
    this.nullIsAllowed = nullIsAllowed;
  }

  public abstract boolean apply(WebElement element);
  public final boolean applyNull() {
    return nullIsAllowed;
  }
  public abstract Object actualValue(WebElement element);

  @Override
  public String toString() {
    return "become " + name;
  }
}
