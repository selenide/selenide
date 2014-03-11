---
layout: page
title : FAQ
header : Frequently Asked Questions
group: navigation
cssClass: faq
header-text: Frequently Asked Questions
---
{% include JB/setup %}

## Motivation

>Why Selenium webdriver is not enough?

Selenium WebDriver is a great technology, but it's not a testing library. It's a browser manipulation tool.
Selenide adds a possibility for easy and stable testing.

>Why yet another Selenium wrapper?

There are several testing libraries around Selenium webdriver.
But it seems that they do not resolve the main problems of UI tests. Namely, instability of tests caused by dynamic
content, JavaScript, Ajax, timeouts etc. Selenide was created to resolve these problems.

## Browsers
>Can I run Selenide tests with Internet Explorer? Headless browser?

Yes.
Selenide can run tests with any browsers that has webdriver binding. The most popular browsers are supported from the box
(chrome, firefox, ie, phantomjs, htmlunit, safari, opera). Others can be also used by passing webdriver class name.

<br/>
E.g. to run tests with PhantomJS browser:
```-Dbrowser=phantomjs```

<br/>

>How can I tell Selenide use browser with my custom profile?

You can also provide Selenide an instance of webdriver that you configured according to your needs.
Go to [Wiki](https://github.com/codeborne/selenide/wiki/How-Selenide-creates-WebDriver) for details.

## Build scripts

>Can I run Selenide tests on CI (continuous integration) server?

Yes.
Please look at [Wiki page](https://github.com/codeborne/selenide/wiki/Build-script/) for examples of build script.

## Screenshots

> Can I take screenshot?

Typically you don't need it, because Selenide automatically takes screenshots on test failures. This is very useful for error analysis.

For JUnit and TestNG there is a special support for taking screenshots also on succeeded tests.

For JUnit:

```java
@Rule // automatically takes screenshot of every failed (and even succeeded) test
public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests().succeededTests();
```

For TestNG:

```java
@Listeners({ ScreenShooter.class})
```

Additionally, you can take screenshot at any moment with a single line of code:

```java
takeScreenshot("my_file_name");
```

Selenide will create two files: `my_file_name.png` и `my_file_name.html`