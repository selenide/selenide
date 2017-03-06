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
**2.**  $(element).doAction()<br>
**3.**  $(element).check(condition)<br>

```java
  open("/login");
  $("#submit").click();
  $(".message").shouldHave(text("Hello"));
```

## Use the power of IDE

Selenide API consists of few classes. We suggest you to stop reading, open your IDE and start typing.

Just type: `$(selector).` - and IDE will suggest you all available options.

<img src="{{ BASE_PATH }}/images/ide-just-start-typing.png" alt="Selenide API: Just start typing"/>

Use the power of todays development environments instead of bothering with documentation!

<br/>

## Selenide API

Here is a full <a href="/javadoc/current" target="_blank">Selenide javadoc</a>.

Just for reference, these are Selenide classes you will probably need for work:

<h3>com.codeborne.selenide.Selenide
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selenide.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selenide.html">[javadoc]</a>
</h3>

The core of the library. Main methods are `open`, `$` and `$$` (import static com.codeborne.selenide.Selenide.* for readability):
<ul>
  <li><a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#open(java.lang.String)">open(String URL)</a> opens the browser (if yet not opened) and loads the URL</li>
  <li><a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#$(java.lang.String)">$(String cssSelector)</a>   – returns object of the SelenideElement class that represents first element found by CSS selector on the page.</li>
  <li><a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#$(org.openqa.selenium.By)">$(By)</a> – returns "first SelenideElement" by the locator of the By class.</li>
  <li><a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#$$(java.lang.String)">$$(String cssSelector)</a>   – returns object of type ElementsCollection that represents collection of all elements found by a CSS selector. </li>
  <li><a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#$$(org.openqa.selenium.By)">$$(By)</a>   – returns "collection of elements" by the locator of By type.</li>
</ul>

Usually, when you get a SelenideElement object by the Dollar `$` command, you can perform some action on it:

* `$(byText("Sign in")).click();`

or even several actions at once:

* `$(byName("password")).setValue("qwerty").pressEnter();`

or you can check some condition: 

* `$(".welcome-message").shouldHave(text("Welcome, user!")).`

The "Double Dollar" command (`$$`) can be useful when a needed element is a one of a same type. For example, instead of:

`$(byXpath("//*[@id='search-results']//a[contains(text(),'selenide.org')]")).click();`

you can use more readable and verbose alternative:

`$$("#search-results a").findBy(text("selenide.org")).click();`

The majority of operations on elements, acquired by the `$` and `$$` commands, have built-in implicit waits depending on a context. This allows in most cases to be not distracted by handling explicitly the waiting for loading of elements while automating testing of dynamic web applications.

Don't be shy to search for more methods inside the Selenide class that can suit your needs. Just type in your IDE Selenide. and choose the needed option among available IDE proposals.

Here are just a few examples:

<a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#sleep(long)">sleep()</a>,
<a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#refresh()">refresh()</a> and
<a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#title()">title()</a>, <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/Selenide.html#executeJavaScript()">executeJavaScript(String jsCode, Object... arguments)</a>.

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/selenide.html).

<h3>com.codeborne.selenide.SelenideElement
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/SelenideElement.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/SelenideElement.html">[javadoc]</a>
</h3>

The `SelenideElement` class describes an element found on the page. The object of this class can be acquired e.g. by the `$` command. The following useful methods are defined in the class.

<h4>
Inner elements search methods
</h4>

* `find(String cssSelector)` / `$(String cssSelector)`
* `find(By)` / `$(By)`
* `findAll(String cssSelector)` / `$$(String cssSelector)`
* `findAll(By)` / `$$(By)`

Here `$` and `$$` are just more concise "aliases" of `find` and `findAll` methods correspondingly.

Thus, you can specify the search path step by step, building the "locators chain":

```java
$("#header").find("#menu").findAll(".item")
```

<h4>
Methods to check element state - assertions
</h4>

*  `should(Condition)` / `shouldBe(Condition)` / `shouldHave(Condition)`
*  `shouldNot(Condition)` / `shouldNotBe(Condition)` / `shouldNotHave(Condition)`

We recommend to choose the convenient alias so the line of code can be easily read like a common english phrase, for example:

```java
$("input").should(exist);  
$("input").shouldBe(visible);
$("input").shouldHave(exactText("Some text"));
```

Assertions play role of explicit waits in Selenide. They **wait** for condition (`visible`, `enabled`, `text("some text")`) to be satisfied until timeout reached (the value of `Configuration.timeout` that is set to 4000 ms by default).
You can use "should-methods" explicitly in order to wait the needed state of element before corresponding action, for example: `$("#submit").shouldBe(enabled).click();`

There are also versions of "Selenide explicit waits" with ability to set timeout explicitly:

*  waitUntil(Condition, milliseconds)
*  waitWhile(Condition, milliseconds)


<h4>
Methods-actions on element
</h4>

*  `click()`
*  `doubleClick()`
*  `contextClick()`
*  `hover()`
*  `setValue(String) / val(String)`
*  `pressEnter()`
*  `pressEscape()`
*  `pressTab()`
*  `selectRadio(String value)`
*  `selectOption(String)`
*  `append(String)`
*  `dragAndDropTo(String)`
*  ...

The majority of actions returns the object of SelenideElement (the same proxy-element) allowing to build concise method chains: `$("#edit").setValue("text").pressEnter();`.


<h4>
Methods to get element statuses and attribute values
</h4>

*  `getValue()` / `val()`
*  `data()`
*  `attr(String)`
*  `text()`        // returns "visible text on a page"
*  `innerText()`   // returns "text of element in DOM"
*  `getSelectedOption()`
*  `getSelectedText()`
*  `getSelectedValue()`
*  `isDisplayed()` //returns false, if element is hidden (invisible) or if element does not exist in DOM; otherwise - true
*  `exists()` //returns true, if element exists in DOM, otherwise - false 

<h4>
Other useful methods
</h4>

*  uploadFromClasspath(String fileName)
*  download()
*  toWebElement()
*  uploadFile(File...)

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/selenide-element.html)

<h3>com.codeborne.selenide.Condition
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Condition.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Condition.html">[javadoc]</a>
</h3>

Conditions are used in `should` / `shouldNot` / `waitUntil` / `waitWhile` constructs. We recommend to import corresponding conditions statically to receive all the advantages of readable code:

*   visible / appear   // e.g. $("input").shouldBe(visible)
*   present / exist    // // conditions to wait for element existence in DOM (it can be still hidden) 
*   hidden / disappear // not(visible)
*   readonly           // e.g. $("input").shouldBe(readonly)
*   name               // e.g. $("input").shouldHave(name("fname"))
*   value              // e.g. $("input").shouldHave(value("John"))
*   type               // e.g. $("#input").shouldHave(type("checkbox"))
*   id                 // e.g. $("#input").shouldHave(id("myForm"))
*   empty              // e.g. $("h2").shouldBe(empty)
*   attribute(name)    // e.g. $("#input").shouldHave(attribute("required"))
*   attribute(name, value) // e.g. $("#list li").shouldHave(attribute("class", "active checked"))
*   cssClass(String)       // e.g. $("#list li").shouldHave(cssClass("checked"))
*   focused
*   enabled
*   disabled
*   selected
*   matchText(String regex)
*   text(String substring)
*   exactText(String wholeText)
*   textCaseSensitive(String substring)
*   exactTextCaseSensitive(String wholeText)

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/condition.html)

<h3>com.codeborne.selenide.Selectors
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selectors.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html">[javadoc]</a>
</h3>

The class contains some `By` selectors to locate elements by text or attributes (that may be missed in standard Selenium WebDriver API):

*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#byText(java.lang.String)">byText</a>     - search element by exact text
*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#withText(java.lang.String)">withText</a>   - search element by contained text (substring)
*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#by(java.lang.String, java.lang.String)">by(attributeName, attributeValue)</a>    - search by attribute's name and value
*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#byTitle(java.lang.String)">byTitle</a>   - search by attribute "title"
*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#byValue(java.lang.String)">byValue</a>   - search by attribute "value"
*   <a href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Selectors.html#byXpath(java.lang.String)">Xpath</a>
* etc.

```java
// Examples:
$(byText("Login")).shouldBe(visible));
$(By.xpath("//div[text()='Login']")).shouldBe(visible);  // any org.openqa.selenium.By.* selector can be used
$(byXpath("//div[text()='Login']")).shouldBe(visible);  // or any its alternative from Selectors class 
```

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/selectors.html)

 

<h3>com.codeborne.selenide.ElementsCollection
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/ElementsCollection.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/ElementsCollection.html">[javadoc]</a>
</h3>

This is the class that describes a collection of elements on the page, found by the locator. Usually the object of the ElementsCollection class can be acquired by the `$$` method. The class contains rather useful methods.

<h4>
Assertions
</h4>

*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#shouldBe(com.codeborne.selenide.CollectionCondition)">shouldBe</a>     - e.g. `$$(".errors").shouldBe(empty)`
*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#shouldHave(com.codeborne.selenide.CollectionCondition)">shouldHave</a>     - e.g. `$$("#mytable tbody tr").shouldHave(size(2))`

Assertions also play role of explicit waits. They **wait** for condition (e.g. `size(2)`, `empty`, `texts("a", "b", "c"))` to be satisfied until timeout reached (the value of `Configuration.collectionsTimeout` that is set to 6000 ms by default).

<h4>
Methods to get statues and attributes of elements collection
</h4>

*   size()
*   isEmpty()
*   getTexts()  // returns the array of visible elements collection texts, e.g. for elements: `<li>a</li><li hidden>b</li><li>c</li>` will return the array `["a", "", "c"]`

<h4>
Methods-selectors of specific collection elements
</h4>

*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#filterBy(com.codeborne.selenide.Condition)">filterBy(Condition)</a> – returns collection (as ElementsCollection) with only those original collection elements that satisfies the condition, e.g. `$$("#multirowTable tr").filterBy(text("Norris"))`
*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#excludeWith(com.codeborne.selenide.Condition)">excludeWith(Condition)</a>     – e.g. `$$("#multirowTable tr").excludeWith(text("Chuck"))`
*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#get(int)">get(int)</a> - returns nth element (as `SelenideElement`); 
*   <a href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/ElementsCollection.html#findBy(com.codeborne.selenide.Condition)">findBy(Condition)</a> - returns the first collection element (as `SelenideElement`) that satisfied the condition.

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/elements-collection.html)

<h3>com.codeborne.selenide.CollectionCondition
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/CollectionCondition.java">[src]</a>
  <a target="_blank" href="{{BASE_PATH}}/javadoc/current/com/codeborne/selenide/CollectionCondition.html">[javadoc]</a>
</h3>

Collection conditions are used in the `shouldBe`/`shouldHave` constructs for the object of `ElementsCollection` class. It is recommended to import needed conditions statically in order to achive all advantages of the readable code.

*   `empty   // e.g. $$("#list li").shouldBe(empty)`
*   `size(int)    // e.g. $$("#list li").shouldHave(size(10))`
*   `sizeGreaterThan(int)`
*   `sizeGreaterThanOrEqual(int)`
*   `sizeLessThan(int)`
*   `sizeLessThanOrEqual(int)`
*   `sizeNotEqual(int)`
*   `texts(String... substrings)`
*   `exactTexts(String... wholeTexts)`

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/collection-condition.html)

<h3>com.codeborne.selenide.WebDriverRunner
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/WebDriverRunner.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/WebDriverRunner.html">[javadoc]</a>
</h3>

This class defines some browser management methods:

*  isChrome()
*  isFirefox()
*  isHeadless()
*  url() - returns current URL
*  source() - returns source HTML code of current page
*  getWebDriver() - returns the WebDriver instance (created by Selenide automatically or set up by the user), thus giving to the user the raw API to Selenium if needed
*  setWebDriver(WebDriver) - tells Selenide to use driver created by the user. From this moment the user himself is responsible for closing the driver (e.g. by calling `getWebDriver().quit()`).

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/webdriver-runner.html)

<h3>com.codeborne.selenide.Configuration
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Configuration.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/current/com/codeborne/selenide/Configuration.html">[javadoc]</a>
</h3>

This class contains different configuration options to configure execution of Selenide-based tests, e.g.:

*  `timeout` - waiting timeout in milliseconds, that is used in explicit (`should`/`shouldNot`/`waitUntil`/`waitWhile`) and implicit waiting for `SelenideElement`; set to 4000 ms by default; can be changed for specific tests, e.g. `Configuration.timeout = 6000;`
* `collectionsTimeout` - waiting timeout in milliseconds, that is used in explicit (`should`/`shouldNot`/`waitUntil`/`waitWhile`) and implicit waiting for `ElementsCollection`; set to 6000 ms by default; can be changed for specific tests, e.g. `Configuration.collectionsTimeout = 8000;`
*  `browser` (e.g. `"chrome"`, `"ie"`, `"firefox"`)
*  `baseUrl`
*  `reportsFolder`
* ...

Additionally, it is possible to pass configuration options as system properties e.g. when configuration tests execution on CI servers (continuous integration), e.g. -Dselenide.baseUrl=http://staging-server.com/start

Look for more details in [Selenide gitbook](https://selenide.gitbooks.io/user-guide/content/en/selenide-api/configuration.html)

Stay tuned!
