# selenide-appium

Selenide adaptor for Appium framework. It defines concise fluent API, natural language assertions and lets you focus
on the business logic of your tests.

### How to add dependency to your project

Just add to pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide-appium</artifactId>
    <version>7.9.1</version>
</dependency>
```

### Initiating Android / Ios Driver Session

1. Create a class and implement WebDriverProvider interface

```java
@NullMarked
public class AndroidDriverForApiDemos implements WebDriverProvider {

    @Override
    @CheckReturnValue
    @NonNull
    public WebDriver createDriver(Capabilities capabilities) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.merge(capabilities);
        options.setPlatformName("Android");
        options.setPlatformVersion("9.0");
        options.setDeviceName("Android Emulator");
        options.setNewCommandTimeout(Duration.ofSeconds(11));
        options.setFullReset(false);
        options.setApp("path-to-apk-file");

        try {
            return new AndroidDriver(new URL("http://127.0.0.1:4723/"), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

2. Set `Configuration.browser = AndroidDriverForApiDemos.class.getName();` // class name which implemented WebDriverProvider interface in previous step
3. Call `SelenideAppium.launchApp();`

```java
  @Test
  void testAndroidTap() {
    Configuration.browser = AndroidDriverWithCalculator.class.getName();
    SelenideAppium.launchApp();
    $(AppiumBy.xpath(".//*[@text='Views']")).shouldBe(visible).click(tap());
    $(AppiumBy.xpath(".//*[@text='Animation']")).shouldBe(visible);
  }
```

### Features

1. Additional [Android Locators](https://github.com/selenide/selenide/blob/main/modules/appium/src/test/java/it/mobile/android/AndroidSelectorsTest.java) and [iOS selectors](https://github.com/selenide/selenide/blob/main/modules/appium/src/test/java/it/mobile/ios/IosSelectorsTest.java) 
2. Working with Deep links is easier than ever
```java
SelenideAppium.openAndroidDeepLink("mydemoapprn://product-details/1", "com.saucelabs.mydemoapp.rn");
SelenideAppium.openIOSDeepLink("mydemoapprn://product-details/1");
```
3. Wrapper methods
```java
   $(AppiumBy.xpath(".//*[@text='Views']")).click(tap()); //perform native event tap
   $(AppiumBy.xpath(".//*[@text='Views']")).tap(); //perform native event tap
   $(AppiumBy.xpath(".//*[@text='Views']")).doubleTap(); //perform native event double tap
   $(AppiumBy.xpath(".//*[@text='Views']")).click(tapWithOffset(100, -60)) //perform tap with offset from center of the element
   $(AppiumBy.xpath(".//*[@text='People Names']")).click(longPress());
   $(AppiumBy.xpath(".//*[@text='People Names']")).click(longPressFor(ofSeconds(5)));
   $(AppiumBy.xpath(".//*[@text='People Names']")).tap(longPressFor(ofSeconds(4)));
```

4. Drag and drop
```java
  SelenideElement source = $(By.id("io.appium.android.apis:id/drag_dot_1")).shouldBe(visible);
  SelenideElement target = $(By.id("io.appium.android.apis:id/drag_dot_2")).shouldBe(visible);
  source.dragAndDrop(to(target));
```

5. Don't worry about casts
```java
AndroidDriver androidDriver = AppiumDriverRunner.getAndroidDriver();
IOSDriver iosDriver = AppiumDriverRunner.getIosDriver();
boolean isAndroid = AppiumDriverRunner.isAndroidDriver();
boolean isIos = AppiumDriverRunner.isIosDriver();
```

6. Rich Assertions

In Appium, it is common that we want to extract values from different attributes.

_Appium way:_
```java
public String getAttribute() {
  return driver instance of AndroidDriver 
    ? mobileELement.getAttribute("content-desc")
    : mobileELement.getAttribute("name")
}
  
// Assertion in test
Assertions.assertThat(getAttribute()).isEqualTo("expected-value")  
```

_Selenide-Appium way:_
```java
  /*
    if android, fetch text attribute and assert with value 
    if ios, fetch name attribute and assert with value
  */
CombinedAttribute combinedAttribute = CombinedAttribute.android("text").ios("name");
$(element).shouldHave(AppiumCondition.attribute(combinedAttribute, "value"));
```

We can also assert collection
```java
CombinedAttribute combinedAttribute = CombinedAttribute.android("content-desc").ios("label");
List<String> expectedList = Arrays.asList("API Demos", "KeyEventText", "Linkify", "LogTextBox", "Marquee", "Unicode");

$$(AppiumBy.xpath("//android.widget.TextView"))
      .shouldHave(AppiumCollectionCondition.attributes(combinedAttribute, expectedList));
```

7. We handle the scrolling for you

```java
$(By.xpath(".//*[@text='Tabs']")).scrollTo().click(); //scroll max of 30 times in downward direction to find element
$(By.xpath(".//*[@text='Tabs']")).scroll(with(DOWN, 10)); //scroll max of 10 times in downward direction to find element
$(By.xpath(".//*[@text='Animation']")).scroll(up()); //scroll max of 30 times in upward direction to find element
$(By.xpath(".//*[@text='Animation']")).scroll(up(0.15f, 0.60f)); //scroll max of 30 times in upward direction with custom swiping height relative to device height
$(By.xpath(".//*[@text='Animation']")).scroll(down(0.15f, 0.60f)); //scroll max of 30 times in downward direction with custom swiping height relative to device height
```

8. We got covered you to the left and right

```java
$(By.xpath(".//*[@text='Tabs']")).swipeTo().click(); //swipe max of 30 times in right direction to find element
$(By.xpath(".//*[@text='Tabs']")).swipe(left()).click(); //swipe max of 30 times in left direction to find element
$(By.xpath(".//*[@text='Tabs']")).swipe(left(10)).click(); //swipe max of 10 times in left direction to find element
```

9. Not able to use page factory for dynamic elements? We got you covered with CombinedBy for writing dynamic locators for both android and ios platforms

```java
int index = 1;
CombinedBy username = CombinedBy
      .android(AppiumBy.xpath("(//android.widget.EditText)["+index+"]"))
      .ios(AppiumBy.xpath("(//XCUIElementTypeTextField)["+index+"]"));

$(username).setValue("selenide-rocks");
```

10. Want to add additional custom methods to SelenideAppiumElement? Just extend base interface and register your Command.

Custom element type:
```java
public interface CustomElement extends SelenideAppiumElement {
  CustomElement myCommand(Object... args);
}
```

Page Object:
```java
public class MyPage {
    @AndroidFindBy(id = "element")
    public CustomElement element;
}
```

Custom Command:
```java
public class CustomCommand implements Command<CustomElement> {
  
  public CustomElement execute(SelenideElement proxy, WebElementSource source, @Nullable Object[] args) {
    // your implementation here
    return (CustomElement) proxy;
  }
}
```

Usage:
```java
Commands.getInstance().add("myCommand", new CustomCommand());
MyPage page = Selenide.page(MyPage.class);
page.element.myCommand("argument");
```


### Changelog

* Here is [CHANGELOG](https://github.com/selenide/selenide-appium/blob/main/CHANGELOG) for selenide-appium 1.0.0 ... 2.8.1
* Here is [CHANGELOG](https://github.com/selenide/selenide/blob/main/CHANGELOG.md) for selenide-appium 6.15.0+

### Reference

Please check our [sample project](https://github.com/selenide-examples/selenide-appium).

Please check our [simple skeleton framework](https://github.com/amuthansakthivel/SelenideAppiumFramework)
