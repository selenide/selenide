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

## Can I take screenshot?

Yes, you can. Thought typically you don't need it, because Selenide automatically takes screenshots on test failures. This is very useful for error analysis.

For JUnit and TestNG there is a special support for taking screenshots also on succeeded tests.

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
