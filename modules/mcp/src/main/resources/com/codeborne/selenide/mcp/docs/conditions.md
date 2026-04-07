# Selenide Conditions

Conditions are used in `shouldBe()`, `shouldHave()`, `shouldNot()` assertions.

## Visibility
```java
$("button").shouldBe(visible)
$("button").shouldBe(hidden)
$("button").shouldNotBe(visible)
```

## Text
```java
$("h1").shouldHave(text("Welcome"))          // case-insensitive substring
$("h1").shouldHave(exactText("Welcome!"))    // exact match
$("h1").shouldHave(textCaseSensitive("OK"))  // case-sensitive
$("h1").shouldNotHave(text("Error"))
```

## Attributes
```java
$("input").shouldHave(attribute("type", "email"))
$("input").shouldHave(attribute("disabled"))  // attribute exists
$("a").shouldHave(href("/login"))
$("input").shouldHave(value("admin"))
$("input").shouldHave(name("username"))
$("div").shouldHave(id("content"))
```

## CSS
```java
$("div").shouldHave(cssClass("active"))
$("div").shouldHave(cssValue("color", "rgb(0, 0, 0)"))
```

## State
```java
$("input").shouldBe(enabled)
$("input").shouldBe(disabled)
$("input").shouldBe(readonly)
$("input").shouldBe(focused)
$("option").shouldBe(selected)
$("div").shouldBe(exist)       // exists in DOM
```

## Input-specific
```java
$("input").shouldBe(empty)
$("input").shouldHave(exactValue("foo"))
```

## Collection conditions
```java
$$("li").shouldHave(size(3))
$$("li").shouldHave(sizeGreaterThan(2))
$$("li").shouldHave(texts("One", "Two", "Three"))
$$("li").shouldHave(exactTexts("One", "Two", "Three"))
$$("li").shouldHave(containExactTextsCaseSensitive("One"))
```

## Custom timeout
```java
$("button").shouldBe(visible, Duration.ofSeconds(10))
```
