# Selenide Commands

## Click
```java
$("button").click()
$("button").doubleClick()
$("button").contextClick()
$("button").click(ClickOptions.usingJavaScript())
```

## Type and input
```java
$("input").setValue("text")       // clears then types
$("input").val("text")            // alias for setValue
$("input").append("more text")    // types without clearing
$("input").type("text")           // human-like typing
$("input").clear()                // clear input
$("input").pressEnter()
$("input").pressEscape()
$("input").pressTab()
```

## Select (dropdown)
```java
$("select").selectOption("Option text")
$("select").selectOptionByValue("opt1")
$("select").selectOption(2)        // by index (0-based)
$("select").getSelectedOption().shouldHave(text("Option 1"))
```

## File upload
```java
$("input[type=file]").uploadFile(new File("photo.jpg"))
$("input[type=file]").uploadFromClasspath("test.txt")
```

## File download
```java
File file = $("a.download").download()
File file = $("a.download").download(DownloadOptions.using(FOLDER))
```

## Drag and drop
```java
$("source").dragAndDrop(DragAndDropOptions.to($("target")))
```

## Keys
```java
$("input").sendKeys(Keys.chord(Keys.CONTROL, "a"))
$("input").sendKeys(Keys.BACK_SPACE)
```

## Scrolling
```java
$("element").scrollTo()
$("element").scrollIntoView(true)
$("element").scrollIntoView("{block: 'center'}")
```

## Reading values
```java
String text = $("h1").getText()
String text = $("h1").getOwnText()     // without children text
String value = $("input").getValue()
String attr = $("a").getAttribute("href")
String css = $("div").getCssValue("color")
String html = $("div").innerHtml()
String html = $("div").innerText()
boolean visible = $("div").isDisplayed()
boolean exists = $("div").exists()
```

## JavaScript
```java
executeJavaScript("alert('hi')")
String result = executeJavaScript("return document.title")
```
