---
layout: post
title: "What is Selenide"
description: ""
category: 
tags: []
---
{% include JB/setup %}

Many of you have tried [Selenium WebDriver](http://code.google.com/p/selenium/) - one of the most popular tools for UI Testing.

Writing UI Tests is not simple. There are a number of typical problems, including Ajax, dynamic pages and timeouts.
The goal of Selenide is to resolve these problems.

![right]({{ BASE_PATH }}/images/selenide-logo-100x100.png)

### What is Selenide
[Selenide](http://selenide.org) is a wrapper for Selenium WebDriver that allows you easier and faster writing of UI Tests.
With Selenide you can concentrate on business logic instead of solving all these endless browser/ajax/timeouts problems.

This is an example of UI Test. As you see, the amount of code is minimal. Just type `open` - and browser comes.

```java
@Test
public void testLogin() {
  open("/login");
  $(By.name("user.name")).sendKeys("johny");
  $("#submitButton").click();
  $("#username").shouldHave(text("Hello, Johny!"));
  $(".error").shouldNotBe(visible);
}
```

When you first execute `open`, Selenide automatically runs web browser and opens page `http://localhost:8080/login` (host and port are configurable).
And closes the browser automatically when all tests are finished.

### Selenide key features
Shortly, these are the key features of Selenide

+  Concise API inspired by jQuery
+  Automatic handling of most problems with Ajax, waiting and timeouts
+  Automatic handling of browser lifecycle
+  Automatic screenshots on test failures

In short, the purpose of Selenide - is to **focus on business logic** and not to engage in perpetual annoying minor problems.

### Additional methods
Selenide provides additional methods for actions that cannot be done with a single command in Selenium WebDriver.
These are radiobutton selection, selectbox selection, taking screenshots, clearing browser cache etc.

```java
@Test
public void canFillComplexForm() {
  open("/client/registration");
  $(By.name("user.name")).setValue("johny");
  selectRadio("user.gender", "male");
  $(By.name("user.securityQuestion")).selectOption("What is my first car?");
  $(By.name("user.preferredLayout")).selectOptionByValue("plain");
  $("#submit").followLink();
  takeScreenShot("complex-form.png");
}

@Before
public void clearCache() {
  clearBrowserCache();
}
```

### How Selenide overcomes Ajax?

Ajax is PITA (pain in the ass) for UI Testers.

Nowdays most of applications use Ajax. When testing web application that uses Ajax, you need to invent code that waits for something. Wait until
button gets green, wait until div gets required text, or wait until error message disappears. You can find tons of web pages suggesting tricks how to
make Selenium wait something.

Selenide resolves this problem like no other. Unbelievably simple!
While Selenium provides you a rich API for waiting different events [see this for example](http://xpinjection.com/2013/04/04/waits-and-timeouts-in-webdriver/),
Selenide suggests you just to not bother. If you want to check that button is green, but button is not yet green, Selenide just waits until the
button gets green. (Of course, timeout is configurable. Default is 4 seconds).
It's an unique solution - as easy and stable as possible.

Enjoy code samples:

```java
@Test
public void pageUsingAjax() {
  $("#username").shouldBe(visible);   // waits until elemens appears
  $("#username").shouldHave(text("Hello, Johny!")); // waits until elements gets text "Hello, Johny!"
  $("#login-button").shouldHave(cssClass("green-button")); // waits until button gets green
  $("#login-button").shouldBe(disabled); // waits until button gets disabled
  $(".error").shouldNotBe(visible);  // waits until element disappears
  $(".error").should(disappear);     // try to the same with a standard Selenium WebDriver!
}
```

### How to make screenshots automatically?
That's easy! If you are using JUnit, just add this line to your base test class:

```java
   @Rule
   public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();
```

This line will cause Selenide to automatically take screenshot after every failed test.
For your convenience, Selenide creates even 2 files: .PNG и .HTML.

If you want to shot all tests (not only failed), you can use this line:

```java
   @Rule
   public ScreenShooter makeScreenshotOnEveryTest =
              ScreenShooter.failedTests().succeededTests();
```

TestNG users can add this annotation to their base test class:

```java
@Listeners({ ScreenShooter.class})
```

### I want to try, how to start?

Just add Selenide dependency to your project:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>2.2</version>
</dependency>
```

Import the class:

```java
import static com.codeborne.selenide.Selenide.*
```

and it's ready! Start writing tests!

### Why yet another testing library?

We have been using Selenium in different projects.
And we discovered that every time we need to write the same code in order to start browser, close browser,
take screenshots and so one.
You can find a huge amount of topics ala "How to do this and that in Selenium" with a huge
amount of code that you need to copy-pase into your project.

We asked ourself: why should UI Testing be so tedious?
We decided to extract our repeating code into a separate open-source library.
That's how [Selenide](http://selenide.org) was born.


### Does somebody use Selenide?
Yes. In <a href="http://codeborne.com/" target="_blank">Codeborne</a> we have been using Selenide for 2 years in different project:

*   Internet-banks
*   Self-service portals
*   etc.

with different languages and testing frameworks:

*   Java + ANT + JUnit
*   Java + Gradle + JUnit
*   Scala + ANT + ScalaTest
*   Groovy + ANT
*   etc.

So you can be sure that Selenide is not just another raw open-source project. It's actually used and supported.

### Show me a working example!

You can find a reference open-source project that uses Selenide: [Hangman game](https://github.com/asolntsev/hangman).

We have also created project [Selenide examples](https://github.com/codeborne/selenide_examples), where you can find examples of using Selenide
for testing different sites like [Gmail](https://github.com/codeborne/selenide_examples/tree/master/gmail/test/org/selenide/examples/gmail) and
[Github](https://github.com/codeborne/selenide_examples/tree/master/github/test/org/selenide/examples/github).

### What means the name "Selenide"?
In chemistry, Selenide is chemical compound containing Selenium + something.

So for UI Tests:

*   Selenide = Selenium + JUnit
*   Selenide = Selenium + TestNG
*   Selenide = Selenium + ScalaTest
*   Selenide = Selenium + что угодно

Enjoy the open-source chemistry!

### Share your experience with us!
We would be glad to get your feedback - tell us what you tried, how it worked. What succeeded, what failed?
Feel free to write your feedback or questions to [googlegroup](mailto:selenide@googlegroups.com) or [privately to me](mailto:andrei.solntsev@gmail.com)!
