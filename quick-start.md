---
layout: page
title :
header : Quick Start
group: navigation
---
{% include JB/setup %}

<div class="short howto">
<div class="wrapper-color-content">

<h3>Quick start</h3>
<h4>It's extremely easy to start using Selenide. Definitely not a rocket science.</h4>

Just add <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/2.3/selenide-2.3.jar">selenide.jar</a> to your project and you are done.<br>
Here is the quick start guide to get you started.</div></div>

<div class="quicklinks">
<div class="wrapper-color-content">
<ul class="gray-boxes">
  <li><a href="https://github.com/codeborne/selenide" target="_blank"><span class="ql"><h3>View on</h3> <strong><h4>GitHub</h4></strong></span></a></li>
  <li><a href="http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.codeborne%22%20AND%20a%3A%22selenide%22" target="_blank"><span class="ql"><h3>Search in</h3> <strong><h4>Maven</h4></strong></span></a></li>
  <li><a href="{{ BASE_PATH }}/archive.html"><span class="ql"><h3>Read our</h3> <strong><h4>Blog</h4></strong></span></a></li>
  <li><a href="http://twitter.com/jselenide" target="_blank"><span class="ql"><h3>Follow at</h3><strong><h4>Twitter</h4></strong></span></a></li>
  <li><a href="{{ BASE_PATH }}/rss.xml"><span class="ql"><h3>Subscribe to</h3><strong><h4>RSS</h4></strong></span></a></li>
</ul>
</div>
</div>
<div class="wrapper-content">

### For Maven users:

Add these lines to file pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>2.3</version>
</dependency>
```

### For Ivy users:

Add these lines to file ivy.xml:

```xml
<ivy-module>
  <dependencies>
    <dependency org="com.codeborne" name="selenide" rev="2.3"/>
  </dependencies>
</ivy-module>
```

### For Gradle users:

Add these lines to file build.gradle:

```groovy
dependencies {
  testCompile 'com.codeborne:selenide:2.3'
}
```

## Start writing test

So easy! No more boring routines, we can start.

Import one class:

```java
import static com.codeborne.selenide.Selenide.*
import static com.codeborne.selenide.Condition.*
```

and write test:

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

Ready!

You can choose any testing framework you prefer: JUnit, TestNG, Cucumber, ScalaTest, JBehave - whatever.

Run as a usual tests. You can run from IDE, or as an ANT script, or "mvn test". You don't need to change anything in your process.


### Do you want to see a working example?

You can find a reference open-source project that uses Selenide: [Hangman game](https://github.com/asolntsev/hangman/blob/master/test/uitest/selenide/HangmanSpec.java).
We have also created project [Selenide examples](https://github.com/codeborne/selenide_examples), where you can find examples of using Selenide
for testing different sites like [Gmail](https://github.com/codeborne/selenide_examples/tree/master/gmail/test/org/selenide/examples/gmail) and
[Github](https://github.com/codeborne/selenide_examples/tree/master/github/test/org/selenide/examples/github).


### Share you examples!

If you have any examples of Selenide usage, feel free to share them wit us!