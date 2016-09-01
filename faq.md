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

> Why Selenium webdriver is not enough?

> Why yet another Selenium wrapper?

See answer [here](/documentation/selenide-vs-selenium.html)

## Page Objects
> Can I use Page Objects with Selenide?

Yes! You can use Page Objects with Selenide.

Moreover, your page objects get **concise and readable** with Selenide. See [more details here](/documentation/page-objects.html).


## Settings
> Where can I find list of all Selenide settings?

A detailed description of all Selenide settings and default values can be found in [javadoc](http://selenide.org/javadoc/{{site.SELENIDE_VERSION}}/com/codeborne/selenide/Configuration.html).

> How can I change Selenide settings?

Selenide has very reasonable default settings. 
They should be convenient for most "normal" projects. 

If you still want to run tests with another settings, you can do it either using System property:

```
-Dselenide.timeout=6000
```

or programmatically, right from your tests:

```java
public void setUp() {
  Configuration.timeout = 6000;
}
```

## Browsers
>Can I run Selenide tests with Internet Explorer? Headless browser?

Yes.
Selenide can run tests with any browsers that has webdriver binding. The most popular browsers are supported from the box
(chrome, firefox, ie, phantomjs, htmlunit, safari, opera). Others can be also used by passing webdriver class name.

<br/>
E.g. to run tests with PhantomJS browser:
```-Dbrowser=phantomjs```

<br/>

>Can I use Selenide with Selenium Grid?

Yes, Selenide supports Selenium Grid. Just add property `-Dremote=http://localhost:5678/wd/hub` when running tests.

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

Yes. See [documentation](/documentation.html) -> [Screenshots](/documentation/screenshots.html)

> Can I tell Selenide to put screenshots to a specific folder?

Yes. See [documentation](/documentation.html) -> [Screenshots](/documentation/screenshots.html)

## Browser windows / tabs

> How can I switch between browser windows/tabs?

You can use Selenium WebDriver API for switching between browser windows.

  * `getWebDriver().getWindowHandles()` - returns set of all browser windows/tabs
  * `getWebDriver().getWindowHandle()` - returns unique identifier of active window/tab

## Source code of Selenide

> Can I access source code of Selenide?

Sure. Source code of Selenide is published [at github](https://github.com/codeborne/selenide/).

> Can I modify Selenide?

Sure! It's open-source. You can either create Pull Request or [Feature Request](https://github.com/codeborne/selenide/issues).

## License

> How much does Selenide cost?

> Does Selenide license allow me to share source code of tests with my customer?

Selenide - __free__ __open-source__ product distributed with [MIT license](https://github.com/codeborne/selenide/blob/master/LICENSE).
Shortly said, it means that you can do anything with it.