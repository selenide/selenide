---
layout: page
title : С чего начать?
header : С чего начать?
group: navigation
---
{% include JB/setup %}

Начать использовать Selenide очень просто. Не нужно читать тонны документации. Не нужно покупать тренинг.

Просто добавь в проект <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/2.1/selenide-2.1.jar">selenide.jar</a> и начинай писать тест.

### Для пользователей Maven:

Добавь в файл pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>2.1</version>
</dependency>
```

### Для пользователей Ivy:

Добавь в файл ivy.xml:

```xml
<ivy-module>
  <dependencies>
    <dependency org="com.codeborne" name="selenide" rev="2.1"/>
  </dependencies>
</ivy-module>
```

### Для пользователей Gradle:

Добавь в файл build.gradle:

```groovy
dependencies {
  testCompile 'com.codeborne:selenide:2.1'
}
```

## Начинай писать тест

Вот так просто! Больше никакой волокиты, начинай писать тест.

Импортируй нужный класс:

```java
include static com.codeborne.selenide.Selenide.*
```

и пиши тест:

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

И готово!

Можно использовать любой фреймворк по вкусу: JUnit, TestNG - что душа пожелает.

Запускать как обычные юнит-тесты. Можно из IDE, можно ANT скриптом, можно "mvn test".
