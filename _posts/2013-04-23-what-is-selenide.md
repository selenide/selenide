---
layout: post
title: "What is Selenide"
description: ""
category: 
tags: []
---
{% include JB/setup %}

Many of you have tried [Selenium WebDriver](http://code.google.com/p/selenium/) - one of the most popular tools for UI Testing.

We have been using Selenium in different projects.
And we discovered that every time we need to write the same code in order to start browser, close browser,
take screenshots and so one.
You can find a huge amount of topics ala "How to do this and that in Selenium" with a huge
amount of code that you need to copy-pase into your project.

We asked ourself: why should UI Testing be so tedious?
We decided to extract our repeating code into a separate open-source library.
That's how [Selenide](http://selenide.org) was born.

![right]({{ BASE_PATH }}/images/selenide-logo-100x100.png)

### Что такое Selenide
[Selenide](http://selenide.org) - это обёртка вокруг Selenium WebDriver, позволяющая быстро и просто его использовать при написании тестов, сосредоточившись на логике, а не суете с браузером.

Вот пример теста. Как видите, код минимален. Вызвал метод `open` - и браузер открылся.

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

При вызове метода open Selenide сам запускает браузер и открывает страницу `http://localhost:8080/login` (порт и хост конфигурируется, естественно). А также заботится о том, чтобы в конце браузер закрылся.

### Ключевые особенности Selenide
Если вкратце, вот главные особенности Selenide

+  Лаконичный синтаксис в духе jQuery
+  Автоматическое решение большинства проблем с Ajax, ожиданием и таймаутами.
+  Управление жизнедеятельностью браузера
+  Автоматическое создание скриншотов

Одним словом, цель Selenide - сосредоточиться на бизнес-логике и не заниматься вечными надоедливыми мелкими проблемами.

### Дополнительные вкусности Selenide
Selenide предоставляет дополнительные методы для действий, которые невозможно сделать одной командой Selenium WebDriver. Это выбор радио-кнопки, выбор элемента из выпадающего списка, создание снимка экрана, очистка кэша браузера и т.п.

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

### Как побороть Ajax?

И особняком стоит вопрос Ajax: при тестировании приложений, использующих Ajax, приходится изобретать код, который чего-то ждёт (когда кнопка станет зелёной).
Selenide решает эту проблему как никто другой. В то время как Selenium предлагает богатый API для ожидания разного рода
событий [Например](http://xpinjection.com/2013/04/04/waits-and-timeouts-in-webdriver/), Selenide
просто предлагает вам не заморачиваться. Если вы хотите поверить, что кнопка зелёная, а она пока
что не зелёная, Selenide просто подождёт, пока она станет зелёной. Конечно, таймаут конфигурируется
(по умолчанию 4 секунды). Это уникальное решение - простое и надёжное.

```java
@Test
public void pageUsingAjax() {
  $("#username").shouldBe(visible);   // ждёт, пока элемент появится
  $("#username").shouldHave(text("Hello, Johny!")); // ждёт, пока текст элемента изменится на "Hello, Johny!"
  $("#login-button").shouldHave(cssClass("green-button")); // ждёт, пока кнопка станет зелёной
  $("#login-button").shouldBe(disabled); // ждёт, пока кнопка станет неактивной
  $(".error").shouldNotBe(visible);  // ждёт, пока элемент исчезнет
  $(".error").should(disappear);     // попробуйте-ка сделать это с Selenium в одну строчку!
}
```

### Как автоматически делать скриншоты?
Легко! Если вы используете JUnit, просто добавьте эту строчку в свой тестовый класс:

```java
   @Rule
   public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();
```

Тогда после каждого упавшего теста будет автоматически создаваться скриншот (на самом деле два файла, .PNG и .HTML).

А если вы хотите снимать вообще все тесты, а не только упавшие, тогда подойдёт такая строчка:

```java
   @Rule
   public ScreenShooter makeScreenshotOnEveryTest =
              ScreenShooter.failedTests().succeededTests();
```

Если вы используете TestNG, просто добавьте следующую аннотацию к своему тестовому классу:

```java
@Listeners({ ScreenShooter.class})
```

### Я хочу попробовать, с чего начать?

Добавь в свой проект зависимость Selenide:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>2.1</version>
</dependency>
```

Импортируй нужный класс:

```java
include static com.codeborne.selenide.Selenide.*
```

И готово! Пиши тесты, едрён-батон!

### Кто-нибудь это реально использует?
Да, мы <a href="http://ru.codeborne.com/" target="_blank">в фирме Codeborne</a> используем Selenide в нескольких реальных проектах:

*   Java + ANT + JUnit
*   Java + Gradle + JUnit
*   Scala + ANT + ScalaTest

Так что можете быть уверены, проект не сырой, реально используется и поддерживается.
Есть ещё небольшой эталонный open-source проект, в котором используется Selenide: [игра Виселица](https://github.com/asolntsev/hangman).
А также мы создали проект [Selenide examples](https://github.com/codeborne/selenide_examples), где мы храним примеры использования
Selenide для тестирования [Gmail](https://github.com/codeborne/selenide_examples/tree/master/gmail/test/org/selenide/examples/gmail),
[Github](https://github.com/codeborne/selenide_examples/tree/master/github/test/org/selenide/examples/github)
и других классических примеров.

### Откуда такое название - Selenide?
Библиотека Selenium взяла своё название от химического элемента (Селен). А селениды - это соединения селена с  другими элементами.

Вот и у нас:

*   Selenide = Selenium + JUnit
*   Selenide = Selenium + TestNG
*   Selenide = Selenium + ScalaTest
*   Selenide = Selenium + что угодно

Химичьте на здоровье!

### Поделитесь с нами опытом!
Нам было бы очень интересно услышать ваши отзывы - что пробовали, что получилось, с чем испытывали проблемы.
Напишите нам в [гуглогруппу](mailto:selenide-ru@googlegroups.com) или [лично](mailto:andrei тчк solntsev сбк gmail тчк com)!
