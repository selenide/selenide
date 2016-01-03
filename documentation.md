---
layout: page
title : Documentation
header : Documentation
group: navigation
cssClass: docs
header-text: >
  <h4>Poor software <span class="bold">doesn't have</span> documentation.
  Brilliant software <span class="bold">doesn't need</span> documentation.</h4>

  We are proud to claim that Selenide is so simple that you don't need to read tons of documentation.<br/>
  The whole work with Selenide consists of three simple things!
---
{% include JB/setup %}

{% include documentation-menu.md %}

## Three simple things:
**1.**  Open the page   <br>
**2.**  $(find element).doAction()<br>
**3.**  $(find element).checkCondition()<br>

```java
  open("/login");
  $("#submit").click();
  $(".message").shouldHave(text("Hello"));
```

## Use the power of IDE

Selenide API consists of few classes. We suggest you to stop reading, open your IDE and start typing.

Just type: `$(selector).` - and IDE suggest you all the options.

<img src="{{ BASE_PATH }}/images/ide-just-start-typing.png" alt="Selenide API: Just start typing"/>

Use the power of nowdays development environments instead of bothering with documentation!

<br/>

## Selenide API

Here is a full <a href="/javadoc/{{site.SELENIDE_VERSION}}" target="_blank">Selenide javadoc</a>.

Just for reference, these are Selenide classes you will probably need for work:

<h3>com.codeborne.selenide.Selenide
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selenide.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html">[javadoc]</a>
</h3>

The main class for using Selenide library. The basic methods are `open`, `$` and `$$` :
<ul>
  <li><a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#open(java.lang.String)">open(String URL)</a> opens the browser</li>
  <li><a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#$(java.lang.String)">$(String cssSelector)</a>   - defines CSS locator, returns SelenideElement</li>
  <li><a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#$(org.openqa.selenium.By)">$(By)</a> defines any other locator (id, name, text, xpath etc.)  - returns SelenideElement</li>
  <li><a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#$$(java.lang.String)">$$(String cssSelector)</a>   - defines CSS locator, returns collection of elements</li>
  <li><a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#$$(org.openqa.selenium.By)">$$(By)</a>   - returns collection of elements</li>
</ul>

Defining SelenideElement doesn't trigger the search in DOM yet, so you can save the locators in variables for later use at any place.
With a SelenideElement instance, you can either do action with it (`click`, `setValue` etc.) or
check a condition: `shouldHave(text("abc"))`. Both will trigger the search of the elements in DOM.

There is also a number of other less frequently used methods in Selenide class:
<a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#sleep(long)">sleep()</a>,
<a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#refresh()">refresh()</a> and
<a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selenide.html#title()">title()</a>.

Look for more documentation in Wiki (soon).

<h3>com.codeborne.selenide.SelenideElement
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/SelenideElement.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/SelenideElement.html">[javadoc]</a>
</h3>

SelenideElement is a wrapper around Selenium WebElement, giving it some additional convenience methods to do Selenide magic.

You can chain SelenideElements with `$` eg.  `$("#page").$("#table").$("#header")` which doesn't trigger DOM search.

Assertions trigger the DOM search and returning SelenideElement allowing fluent interface.

*  should(Condition)
*  shouldBe(Condition)
*  shouldHave(Condition)
*  shouldNot(Condition)
*  shouldNotBe(Condition)
*  shouldNotHave(Condition)

Actions on the elements:

*  click()
*  doubleClick()
*  pressEnter(String)
*  selectOption(String text)
*  selectOptionByValue(String value)
*  setValue(String)
*  val(String)
*  append(String)


Getting status and values of elements:

*  val()
*  data()
*  text()
*  isDisplayed()
*  exists()
*  getSelectedOption()
*  getSelectedText()
*  getSelectedValue()<br/>

Other useful commands:

*  waitUntil(Condition, milliseconds)
*  waitWhile(Condition, milliseconds)
*  uploadFromClasspath(String fileName)
*  download()
*  toWebElement()

<h3>com.codeborne.selenide.Condition
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Condition.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Condition.html">[javadoc]</a>
</h3>


*   visible | appear   // e.g. $("input").shouldBe(visible)
*   present | exist
*   hidden | disappear | not(visible)
*   readonly           // e.g. $("input").shouldBe(readonly)
*   attribute(String)
*   name               // e.g. $("input").shouldHave(name("fname"))
*   value              // e.g. $("input").shouldHave(value("John"))
*   type               // $("#input").shouldHave(type("checkbox"))
*   id                 // $("#input").shouldHave(id("myForm"))
*   empty              // $("h2").shouldBe(empty)
*   options
*   cssClass(String)
*   focused
*   enabled
*   disabled
*   selected
*   matchText(String regex)
*   text(String substring)
*   exactText(String wholeText)
*   textCaseSensitive(String substring)
*   exactTextCaseSensitive(String wholeText)

<br/>
You can easily add your own conditions by extending class `com.codeborne.selenide.Condition`.

For example:

```java
public static Condition css(final String propName, final String propValue) {
    @Override
    public boolean apply(WebElement element) {
      return propValue.equalsIgnoreCase(element.getCssValue(propName));
    }

    @Override
    public String actualValue(WebElement element) {
        return element.getCssValue(propName);
    }
};

// Example usage:
$("h1").shouldHave(css("font-size", "16px"));
```


<h3>com.codeborne.selenide.Selectors
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selectors.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html">[javadoc]</a>
</h3>

This class contains some `By` selectors for searcing elements by text or attribute (that are missing in standard Selenium WebDriver API):

*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html#byText(java.lang.String)">byText</a>     - search by exact text
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html#withText(java.lang.String)">withText</a>   - search by text (substring)
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html#by(java.lang.String, java.lang.String)">by</a>    - search by attribute
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html#byTitle(java.lang.String)">byTitle</a>   - search by "title" attribute
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Selectors.html#byValue(java.lang.String)">byValue</a>   - search by "value" attribute

<h3>com.codeborne.selenide.ElementsCollection
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/ElementsCollection.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html">[javadoc]</a>
</h3>

This is the class returned by `$$` method. It contains a list of web elements with additionals methods:

*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html#shouldBe(com.codeborne.selenide.CollectionCondition)">shouldBe</a>     - e.g. `$$(".errors").shouldBe(empty)`
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html#shouldHave(com.codeborne.selenide.CollectionCondition)">shouldHave</a>     - e.g. `$$("#mytable tbody tr").shouldHave(size(2))`
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html#find(com.codeborne.selenide.Condition)">find</a>     - e.g. `$$("#multirowTable tr").findBy(text("Norris"))`
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html#filter(com.codeborne.selenide.Condition)">filter</a>     - e.g. `$$("#multirowTable tr").filterBy(text("Norris"))`
*   <a href="{{BASE_PATH}}/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/ElementsCollection.html#exclude(com.codeborne.selenide.Condition)">exclude</a>     - e.g. `$$("#multirowTable tr").excludeWith(text("Chack"))`

See more details on these and other classes in [javadoc]({{ BASE_PATH }}/javadoc.html)

Stay tuned!
