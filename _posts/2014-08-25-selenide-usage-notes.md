---
layout: post
title: "Selenide usage simple notes: IE, TestNG, Bootstrap dropdown"
description: ""
category:
header-text: "IE, One way to add Selenide screenshot into the TestNG HTML reporter, One way to work with customized Bootstrap HTML elements"
tags: []
---
{% include JB/setup %}

Hello!

My name is Sergey Shimkiv. Below you can find some notes about Selenide/Selenium usage. <br/> <br/>

### IE notes

IE 11 x32/x64. In some cases after the actions with HTML elements (`click()` for example) you can receive an exception. <br />
The cause of it could be synthetic events usage. The workaround is to use native events for IE:
 
 ```java
   capabilities.setCapability("nativeEvents", "true");
 ```
 
 https://code.google.com/p/selenium/wiki/InternetExplorerDriver
<br/>

### One way to add Selenide screenshot into the TestNG HTML reporter

In some cases you might want to add Selenide's screenshots of failed tests into your TestNG HTML reporter.

For example:
You write two tests (Test1 and Test2) which must be linked (Test2 depends on Test1 results).
But you know that Test1 will fail because of application bug(s).
So you will write something like this:

 ```java
   @Test(...)
   public void Test1() {
    ...
    try {
      // Test block with known bug
    } catch(...) {
      // Some actions
    } finally {
      // You want to perform some actions here to ensure correct preconditions for the Test2
    }
   }
 ```

Let's assume that you're using own test listener that extends `TestListenerAdapter`.
And you have overridden `onTestFailure(ITestResult result)` method to collect some additional information into the HTML report - your own screenshots for example.
In such case it is possible that your screenshot gathering implementation will collect incorrect data because `finally` block can be performed before screenshot will be taken.

Just keep in mind that Selenide's screenshot file name (that will be taken directly after some exception) could be taken from `result.getThrowable().getMessage();`
<br/> 

### One way to work with customized Bootstrap HTML elements

Tests often get complicated when you need to test customized HTML controls with complex logic.

For example, Bootstrap's dropdown elements are represented via set of HTML elements. In common:

 ```html
  <div class="dropdown">
    <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
      Dropdown
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
      <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Action</a></li>
      <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Another action</a></li>
      <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Something else here</a></li>
      <li role="presentation" class="divider"></li>
      <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Separated link</a></li>
    </ul>
  </div>
 ```

The problem is that the `<ul>` element initially is not visible for the Selenium (and for Selenide too).
To be able to select some dropdown element value you can:

 ```java
   SelenideElement parentDiv = $(".dropdown");
   
   // Find `<button>` element and `click()` on it
   parentDiv.find("button").scrollTo().click();
   
   // Now you can find needed dropdown element value by text
   parentDiv.find(".dropdown-menu").find(withText("Action")).parent().click();
 ```

<br/>
In the next post I would like to share experience in the automated testing environment deployment (based on Grid2).
<br/><br />
Thanks to the Selenide's authors for their great tool ;)
<br />
<br />

Sergey Shimkiv
<br/>