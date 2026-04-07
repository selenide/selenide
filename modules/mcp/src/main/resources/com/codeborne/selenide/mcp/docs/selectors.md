# Selenide Selectors

## CSS selectors (default)
```java
$("button")                   // by tag
$("#submit")                  // by id
$(".primary")                 // by class
$("input[type=email]")        // by attribute
$("form .login-btn")          // nested
```

## By text
```java
$(byText("Sign In"))          // exact text match
$(withText("Sign"))           // partial text match
```

## By attribute
```java
$(byAttribute("data-testid", "submit"))
$(by("name", "email"))        // shorthand
$(byName("email"))            // equivalent
$(byId("login"))              // equivalent to $("#login")
```

## XPath
```java
$(byXpath("//div[@class='content']"))
$x("//div[@class='content']") // shorthand
```

## By value
```java
$(byValue("admin"))           // input value
```

## Combining selectors
```java
$("div").find("button")       // child lookup
$("div").$("button")          // same as find
$("div").closest("form")      // ancestor lookup
$("div").parent()             // direct parent
$("div").sibling(0)           // sibling by index
```

## Collections
```java
$$("li")                      // all matching elements
$$(".item").first()           // first element
$$(".item").last()            // last element
$$(".item").get(2)            // by index (0-based)
$$(".item").filterBy(visible) // filter by condition
```
