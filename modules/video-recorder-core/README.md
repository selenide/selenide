# Tests video recorder

### Concept

Usually to take screenshot we use the next code:

```java
Webdriver driver = new ChromeDriver();
...
File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
```

So, in general, we should take screenshot once a second and save it 24 times to output video file.

First add dependency to the project
**Maven**
```xml
<dependency>
  <groupId>com.codeborne</groupId>
  <artifactId>video-recorder-core</artifactId>
  <version>LAST_VERSION_OF_SELENIDE</version>
</dependency>
``` 

**Gradle**
```groovy
implementation("com.codeborne:video-recorder-core:LAST_VERSION_OF_SELENIDE")
```

To run recorder directly, pseudo code :

```java
videoRecorder =new

VideoRecorderScreenShot(
  webdriver().object(),
  RecorderFileUtils.generateVideoFileName(<testClassName>, <testName>));
executor =new ScheduledThreadPoolExecutor(1);
executor.scheduleAtFixedRate(videoRecorder, 0,1000,TimeUnit.MILLISECONDS);
```

in the first line the parameter for `VideoRecorderScreenShot` constructor should pass `Webdriver` instance.
`<testClassName>` and `<testName>` can be `null`. These parameters are needed to point recorder to the video file.

| testsClassName | testName   | result file name location                                                                       |
|----------------|------------|-------------------------------------------------------------------------------------------------|
| null or ""     | null or "" | `Configuration.reportsFolder + "/records/" + <randomFileName>.webm`                             |
| not empty      | null or "" | `Configuration.reportsFolder + "/records" + "/testClassName/" + <randomFileName>.webm`          |
| null or ""     | not empty  | `Configuration.reportsFolder + "/records" + "/testName/" + <randomFileName>.webm`               |
| not empty      | not empty  | `Configuration.reportsFolder + "/records" + "/testClassName/testName/" + <randomFileName>.webm` |

The last line schedules task every second.
After recording you should stop recorder

```java
videoRecorder.stopRecording();
executor.shutdown();
```

After that you can get reference to already recorded file.

```java
Path videoFile = RecorderFileUtils.getLastModifiedFile(
  RecorderFileUtils.generateOrGetVideoFolderName(
    testInfo.getTestClass().get().getSimpleName(), 
    testInfo.getTestMethod().get().getName())
);
```



