---
layout: page
title: Selenide.org
tagline: Concise UI Tests in Java
---
{% include JB/setup %}

![right]({{ BASE_PATH }}/images/selenide-logo-100x100.png)

Selenide is a library for writing concise and expressive UI Tests in Java using Selenium WebDriver.

## Code sample

```java
@Test
public void userCanLoginByUsername() {
  open("/login");
  $(By.name("user.name")).setValue("johny");
  $("#submit").click();
  $(".loading_progress").should(disappear); // Waits until element disappears
  $("#username").shouldHave(text("Hello, Johny!")); // Waits until element gets text
}
```

## What are Selenide advantages?
Selenide is a wrapper for <a href="http://seleniumhq.org/projects/webdriver/">Selenium WebDriver</a> that brings the following advantages:

+  Concise API for tests
+  Automatic handling browser lifecycle (open/close/restart)
+  jQuery-style selectors
+  Ajax support

That way Selenide provides a Concise API for writing tests.
You don't need to think how to shutdown browser, handle timeouts or write
monstrous code for waiting events - just **concentrate on your business logic**!

## How to start?
Just add <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/2.1/selenide-2.1.jar">selenide.jar</a> to your project.
For Maven, Ivy, Gradle etc. users:

```xml
<dependency org="com.codeborne" name="selenide" revision="2.1"/>
```

See [Quick Start guide](https://github.com/codeborne/selenide/wiki/Quick-Start) for more details.

## Contacts

<ul>
  <li><a href="https://github.com/codeborne/selenide">View on <strong>GitHub</strong></a></li>
  <li><a href="http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.codeborne%22%20AND%20a%3A%22selenide%22">Search In <strong>Maven</strong></a></li>
  <li><a href="http://twitter.com/jselenide">Follow at <strong>Twitter</strong></a></li>
</ul>
