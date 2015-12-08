---
layout: page
title :
header : Screenshots
group: navigation
cssClass: docs
header-text: >
  <h4>Documentation</h4>
  
  Screenshots
---
{% include JB/setup %}

{% include documentation-menu.md %}

## Can Selenide take screenshots?

Yes, Selenide takes screenshots **automatically** on every test failure. This is very useful for error analysis.

By default Selenide puts screenshots to folder `build/reports/tests`.


### Can I tell Selenide to put screenshots to a specific folder?

Yes. 
You can use property `-Dselenide.reports=test-result/reports` to set any directory to store screenshots to.

Another option is to set this folder directly from your code:

```java
Configuration.reportsFolder = "test-result/reports";
```


## JUnit and TestNG support

For JUnit and TestNG, there is a special support for taking screenshots also on successful tests.


### For JUnit:

To automatically take screenshot of every failed test:

```java
import com.codeborne.selenide.junit.ScreenShooter;

@Rule
public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();
```

Actually it's rudiment. You don't need it, because Selenide does it automatically.

But if you wish to automatically take screenshot of every test (even succeeded), use the following command:

```java
@Rule
public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests().succeededTests();
```


### For TestNG:

```java
import com.codeborne.selenide.testng.ScreenShooter;

@Listeners({ ScreenShooter.class})
```

To automatically take screenshots after every test (even succeeded), execute the following command before running tests:
```java
ScreenShooter.captureSuccessfulTests = true;
```

### At any moment you wish
Additionally, you can take screenshot at any moment with a single line of code:

```java
import static com.codeborne.selenide.Selenide.screenshot;

screenshot("my_file_name");
```

Selenide will create two files: `my_file_name.png` и `my_file_name.html`
