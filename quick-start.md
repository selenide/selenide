---
layout: page
title : Quick Start
header : Quick Start
group: navigation
cssClass: howto
header-text: >
  <h4>It's extremely easy to start using Selenide. Definitely not a rocket science.</h4>
  Just add <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/2.14/selenide-2.14.jar">selenide.jar</a> to your project and you are done.<br/>
  Here is the quick start guide to get you started.

---
{% include JB/setup %}

<a class="video right" href="http://vimeo.com/107647158">
  How to write UI test in 10 minutes
</a>

### For Maven users:

Add these lines to file pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>{{site.SELENIDE_VERSION}}</version>
    <scope>test</scope>
</dependency>
```

### For Ivy users:

Add these lines to file ivy.xml:

```xml
<ivy-module>
  <dependencies>
    <dependency org="com.codeborne" name="selenide" rev="{{site.SELENIDE_VERSION}}"/>
  </dependencies>
</ivy-module>
```

### For Gradle users:

Add these lines to file build.gradle:

```groovy
dependencies {
  testCompile 'com.codeborne:selenide:{{site.SELENIDE_VERSION}}'
}
```

## Start writing test

So easy! No more boring routines, we can start.

Import required classes:

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

### Video tutorial
<iframe src="//player.vimeo.com/video/107647158" width="800" height="450" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe> <p><a href="http://vimeo.com/107647158">Как написать UI тест за 10 минут</a> from <a href="http://vimeo.com/user20427140">Selenide</a> on <a href="https://vimeo.com">Vimeo</a>.</p> <p>Туториал по Selenide</p>