# Selenide Collections

## Creating collections
```java
$$("li")                           // all <li> elements
$$x("//li")                        // XPath collection
$("ul").$$("li")                   // children of element
```

## Filtering
```java
$$("li").filterBy(visible)         // only visible
$$("li").filterBy(text("active"))  // containing text
$$("li").excludeWith(hidden)       // exclude hidden
```

## Extracting elements
```java
$$("li").first()                   // first element
$$("li").last()                    // last element
$$("li").get(2)                    // by index (0-based)
$$("li").findBy(text("Item 1"))    // first matching condition
```

## Assertions
```java
$$("li").shouldHave(size(3))
$$("li").shouldHave(sizeGreaterThan(1))
$$("li").shouldHave(sizeLessThan(10))

$$("li").shouldHave(texts("One", "Two", "Three"))
$$("li").shouldHave(exactTexts("One", "Two", "Three"))
$$("li").shouldHave(containExactTextsCaseSensitive("One", "Two"))

$$("li").shouldHave(itemWithText("Special"))     // at least one
$$("li").shouldBe(empty)                         // size == 0
```

## Iteration
```java
List<String> texts = $$("li").texts()             // all texts
List<String> attrs = $$("li").attributes("href")  // all attributes

$$("li").forEach(el -> el.shouldBe(visible))
$$("li").snapshot()                // freeze collection state
```
