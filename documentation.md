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
Just for reference, these are Selenide classes you will probably need for work:

### com.codeborne.selenide.Selenide <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selenide.java">[src]</a>

*  open(URL | String relativeOrAbsoluteURL)
*  $(By | String cssSelector)   - returns SelenideElement
*  $$(By | String cssSelector)  - return collection of elements
*  refresh()
*  title()
*  sleep(long milliseconds)

The main method us certainly `$` - it has a variety of parameters, but returns SelenideElement.

### com.codeborne.selenide.SelenideElement <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/SelenideElement.java">[src]</a>

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

### com.codeborne.selenide.Condition <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Condition.java">[src]</a>

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

### com.codeborne.selenide.Selectors <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Selectors.java">[src]</a>
### com.codeborne.selenide.ElementsCollection <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/ElementsCollection.java">[src]</a>
### com.codeborne.selenide.CollectionCondition <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/CollectionCondition.java">[src]</a>
### com.codeborne.selenide.WebDriverRunner <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/WebDriverRunner.java">[src]</a>
### com.codeborne.selenide.WebDriverProvider <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/WebDriverProvider.java">[src]</a>
### com.codeborne.selenide.Configuration <a target="_blank" href="https://github.com/codeborne/selenide/blob/master/src/main/java/com/codeborne/selenide/Configuration.java">[src]</a>



Stay tuned!
