---
layout: page
title :
header : 
group: navigation
cssClass: docs
header-text: <h4>Selenide and Selenium comparison</h4>
  
  Why is not Selenium webdriver enough?
  
---
{% include JB/setup %}

{% include documentation-menu.md %}

## Motivation

Selenium WebDriver is a great tool, but it's not a tool for testing. It's a tool for browser manipulation. 

And Selenide is a tool for automated testing (build on top of Selenium WebDriver). 

### Why yet another Selenium wrapper?

There are several testing libraries around Selenium webdriver. But it seems that they do not resolve the main problems of UI tests. 
Namely, instability of tests caused by dynamic content, JavaScript, Ajax, timeouts etc. Selenide was created to resolve these problems.


## What benefits gives Selenide over plain Selenium?

First of all, Selenide makes your tests stable by resolving (almost) all Ajax/timing issues.

Selenide provides a concise API for using Selenium WebDriver in UI tests:

* Smart waiting
* Transparent WebDriver
* Convenience methods
* Ajax support
* Automated screenshots

You can find more details below.

### Transparent WebDriver
You don't need to operate with WebDriver directly.
Selenide will start and shut down the browser automatically whenever it's needed.

### Convenience methods
Selenide provides concise API for that makes your tests shorter and more readable. 
Selenide has convenient methods for operating controls like textfield, radiobutton, selectbox, searching elements by texts and so on.

```java
@Test
public void canFillComplexForm() {
  open("/client/registration");
  $(By.name("user.name")).val("johny");
  $(By.name("user.gender")).selectRadio("male");
  $("#user.preferredLayout").selectOption("plain");
  $("#user.securityQuestion").selectOptionByText("What is my first car?");
}
```

### Ajax support
When testing Ajax applications we often need to wait until some element changes its state. Selenide has built-in methods for waiting.

Any of the following methods waits until the described event happens. Default timeout is 4 seconds.

```java
 $("#topic").should(appear);
 $("#topic").shouldBe(visible);

 $("#topic").should(disappear);
 $("h1").shouldHave(text("Hello"));
 $(".message").shouldNotHave(text("Wait for loading..."));
 $(".password").shouldNotHave(cssClass("errorField"));
 
 $(".error").should(disappear);
```

### Automated screenshots

When your test fail, Selenide will automatically take screenshot. You do not need to do anything for it. 

## More benefitcs

Look at this presentation to find out more Selenide benefits:

<div class="wrapper-content center">
<iframe width="840" height="473" src="https://www.youtube.com/embed/fR8CyLcxBZ0" frameborder="0" allowfullscreen></iframe>
</div>

<div class="wrapper-content center">
<iframe src="https://docs.google.com/presentation/d/1ZksjuL2vPN_pkhMuon0HH4gm7KNmjU50pByRRGzgVko/embed?start=false&loop=false&delayms=3000" frameborder="0" width="960" height="569" allowfullscreen="true" mozallowfullscreen="true" webkitallowfullscreen="true"></iframe>
</div>

<br/>

