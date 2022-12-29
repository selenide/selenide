# selenide-appium

Selenide adaptor for Appium framework. It defines concise fluent API, natural language assertions and let you focus
on the business logic of your tests.

### How to add dependency to your project

Just add to pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide-appium</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Initiating Android / Ios Driver Session

1. Create a class and implement WebDriverProvider interface

```
@ParametersAreNonnullByDefault
public class AndroidDriverForApiDemos implements WebDriverProvider {

    @Override
    @CheckReturnValue
    @Nonnull
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
            return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

2. Set Configuration.browser = AndroidDriverForApiDemos.class.getName(); //class name which implemented WebDriverProvider interface in previous step
3. Call SelenideAppium.launchApp();

```
  @Test
  void testAndroidTap()  {
    Configuration.browser = AndroidDriverWithCalculator.class.getName();
    SelenideAppium.launchApp();
    $(AppiumBy.xpath(".//*[@text='Views']")).shouldBe(visible).click(tap());
    $(AppiumBy.xpath(".//*[@text='Animation']")).shouldHave(visible);
  }
```

### Features

1. Additional Locators - Please refer [here](https://github.com/selenide/selenide-appium/blob/main/src/test/java/integration/android/AndroidSelectorsTest.java)
2. Working with Deep links is easier than ever
```
SelenideAppium.openAndroidDeepLink("mydemoapprn://product-details/1", "com.saucelabs.mydemoapp.rn");
SelenideAppium.openIOSDeepLink("mydemoapprn://product-details/1");
```
3. Wrapper methods
```
   $(AppiumBy.xpath(".//*[@text='Views']")).click(tap()); //perform native event tap
   $(AppiumBy.xpath(".//*[@text='Views']")).click(tapWithOffset(100, -60)) //perform tap with offset from center of the element
   $(AppiumBy.xpath(".//*[@text='People Names']")).click(longPress()); 
   
   //drag and drop
   SelenideElement from = $(By.id("io.appium.android.apis:id/drag_dot_1")).shouldBe(visible);
   SelenideElement to = $(By.id("io.appium.android.apis:id/drag_dot_2")).shouldBe(visible);
   from.dragAndDropTo(to);
```

4. Don't worry about casts
```
AndroidDriver androidDriver = AppiumDriverRunner.getAndroidDriver();
IOSDriver iosDriver = AppiumDriverRunner.getIosDriver();
boolean isAndroid = AppiumDriverRunner.isAndroidDriver();
boolean isIos = AppiumDriverRunner.isIosDriver();
```

### Changelog

Here is [CHANGELOG](https://github.com/selenide/selenide-appium/blob/main/CHANGELOG)

### Reference

Please check our [sample project](https://github.com/selenide-examples/selenide-appium). 
