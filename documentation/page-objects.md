---
layout: page
title :
header : Page Objects
group: navigation
cssClass: docs
header-text: >
  <h4>Documentation</h4>
  
  Page Objects
---
{% include JB/setup %}

{% include documentation-menu.md %}

## Page Objects - it's simple!

In the QA world, there is a very popular pattern [Page Objects](https://code.google.com/p/selenium/wiki/PageFactory).
It means that for every page you create a separate class - _Page Object_. This class should implement
logic of working with different page elements.
It's thought that Page Object helps to avoid duplication of locators in tests. 

Selenide allows you writing more concise and readable Page Objects. 

That's how Page Object looks like with Selenide: 

```java
public class GoogleSearchPage {
  public GoogleResultsPage search(String query) {
    $(By.name("q")).setValue(query).pressEnter();
    return page(GoogleResultsPage.class);
  }
}

public class GoogleResultsPage {
  public ElementsCollection results() {
    return $$("#ires li.g");
  }
}
```

As you see, there is no ```PageFactory```, no ```initElements``` and other garbage like that.
Your Page Object only contains your logic. 

How the test looks like? Here it is:

```java
  GoogleSearchPage searchPage = open("/login", GoogleSearchPage.class);
  GoogleResultsPage resultsPage = searchPage.search("selenide");
  resultsPage.results().shouldHave(size(10));
  resultsPage.results().get(0).shouldHave(text("Selenide: Concise UI Tests in Java"));
```

Isn't is simple?

## Classic Page Object

There is a very popular style of Page Object that implies creating a separate field for every page element.
This style has some disadvantages, but if you want, Selenide allows it:

```java
public class GoogleSearchPage {
  @FindBy(how = How.NAME, using = "q")
  private SelenideElement searchBox;
  
  public GoogleResultsPage search(String query) {
    searchBox.setValue(query).pressEnter();
    return page(GoogleResultsPage.class);
  }
}

public class GoogleResultsPage {
  @FindBy(how = How.CSS, using = "#ires li.g")
  public ElementsCollection results;
}
```

## Final OOP notice
Let me remind you that the initial idea behind Page Objects was to encapsulate (read: *hide*) logic of
working with page elements. Tests should not operate with page elements / locators / XPaths. Tests should use 
methods of Page Object instead. So, if you even declare fields for page page elements, please, make them *private*!

Let methods of Page Object be public and fields private.

Otherwise OOP is just nonsense.
