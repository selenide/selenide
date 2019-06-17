# selenide-appium

Selenide adaptor for Appium framework.

### How to run the example

* Run the emulator:
  open Android Stuiod -> "Android Virtual Device Manager" -> Run

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
    <version>1.2</version>
</dependency>
```

NB! This library is only needed if you want to use PageFactory (annotations like `@AndroidFindBy`).

If you don't need `@FooFindBy` annotations, consider using a [more simple approach](https://github.com/selenide-examples/selenide-appium).
It just uses usual Selenide `$` calls to find elements in mobile app. 
