# selenide-appium

Selenide adaptor for Appium framework.

### How to run the example

* Run the emulator:

  > open Android Studio -> "Android Virtual Device Manager" -> Run

* Run appium server:
   > appium

* And finally, run the test:
   > ./gradlew test

### How to add dependency to your project

Just add to pom.xml:

```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide-appium</artifactId>
    <version>2.1.1</version>
</dependency>
```

For a reference, see a [sample project](https://github.com/selenide-examples/selenide-appium). 
