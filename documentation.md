---
layout: page
title : Selenide documentation
header : Documentation
group: navigation
---
{% include JB/setup %}

> Poor software <span class="red">does not have</span> documentation. <br/>
> Brilliant software <span class="green">does not need</span> documentation.

We are proud to claim that Selenide is so simple that you don't need to read tons of documentation.

## Three simple things

The whole work with Selenide consists of three simple things:

1.  Open the page
2.  $(find element).doAction()
3.  $(find element).checkCondition()

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

Here is a full <a href="/javadoc/2.3" target="_blank">Selenide javadoc</a>.

Just for reference, these are Selenide classes you will probably need for work:

<h3>com.codeborne.selenide.Selenide
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selenide.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/Selenide.html">[javadoc]</a>
</h3>

The main class for using Selenide library. Two basic methods are `open` and `dollar`:
<ul>
  <li><a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#open(java.lang.String)">open(URL)</a>, and</li>
  <li><a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#$(java.lang.String)">$(String cssSelector)</a>   - returns SelenideElement</li>
  <li><a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#$(org.openqa.selenium.By)">$(By)</a>   - returns SelenideElement</li>
  <li><a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#$$(java.lang.String)">$$(String cssSelector)</a>   - returns collection of elements</li>
  <li><a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#$$(org.openqa.selenium.By)">$$(By)</a>   - returns collection of elements</li>
</ul>

When received a SelenideElement instance, you can either do action with it (`click`, `setValue`) or
check a condition: `shouldHave(text("abc"))`.

There is also a number of other less frequently used methods in Selenide class:
<a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#sleep(long)">sleep()</a>,
<a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#refresh()">refresh()</a> and
<a href="{{BASE_PATH}}/javadoc/2.3/com/codeborne/selenide/Selenide.html#title()">title()</a>.

<h3>com.codeborne.selenide.SelenideElement
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/SelenideElement.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/SelenideElement.html">[javadoc]</a>
</h3>

SelenideElement is a wrapper around Selenium WebElement, giving it some additional convenient method:

*  should(Condition)
*  shouldBe(Condition)
*  shouldHave(Condition)
*  shouldNot(Condition)
*  shouldNotBe(Condition)
*  shouldNotHave(Condition)<br/>
*  waitUntil(Condition, milliseconds)
*  waitWhile(Condition, milliseconds)<br/>
*  find(String | By)
*  findAll(String | By)<br/>
*  setValue(String)
*  val(String)
*  append(String)
*  pressEnter(String)<br/>
*  val()
*  data()
*  text()
*  isDisplayed()
*  exists()<br/>
*  selectOption(String text)
*  selectOptionByValue(String value)
*  getSelectedOption()
*  getSelectedText()
*  getSelectedValue()<br/>
*  uploadFromClasspath(String fileName)
*  toWebElement()

<h3>com.codeborne.selenide.Condition
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Condition.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/Condition.html">[javadoc]</a>
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

You can easily add your own conditions by implementing interface `com.codeborne.selenide.Condition`


To be continued...

<h3>com.codeborne.selenide.Selectors
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selectors.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/Selectors.html">[javadoc]</a>
</h3>

<h3>com.codeborne.selenide.ElementsCollection
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/ElementsCollection.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/ElementsCollection.html">[javadoc]</a>
</h3>

<h3>com.codeborne.selenide.CollectionCondition
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/CollectionCondition.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/CollectionCondition.html">[javadoc]</a>
</h3>

<h3>com.codeborne.selenide.WebDriverRunner
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/WebDriverRunner.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/WebDriverRunner.html">[javadoc]</a>
</h3>

<h3>com.codeborne.selenide.WebDriverProvider
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/WebDriverProvider.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/WebDriverProvider.html">[javadoc]</a>
</h3>

<h3>com.codeborne.selenide.Configuration
  <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Configuration.java">[src]</a>
  <a target="_blank" href="{{ BASE_PATH }}/javadoc/2.3/com/codeborne/selenide/Configuration.html">[javadoc]</a>
</h3>



Stay tuned!
