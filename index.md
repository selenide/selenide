---
layout: page
title: Selenide - Concise UI Tests in Java
tagline: Concise UI Tests in Java
---
{% include JB/setup %}

<h4>{{ site.tagline }}</h4>

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
Just add <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/2.4/selenide-2.4.jar">selenide.jar</a> to your project.
For Maven, Ivy, Gradle etc. users:

```xml
<dependency org="com.codeborne" name="selenide" revision="2.4"/>
```

See [Quick Start guide]({{ BASE_PATH }}/quick-start.html) for more details.
