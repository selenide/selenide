# Tests video recorder

## Precaution
> [!IMPORTANT]
> This feature is for debug needs. So use it wisely

## How it works

Video recorder starts in the beginning of your test, and constantly takes screenshots of a current browser window (using a standard WebDriver API).
When the test finishes, recorder puts all these screenshots together into a video file.

## How to use
To record a video from your tests, add a dependency to your project:

**Maven**
```xml
<dependency>
  <groupId>com.codeborne</groupId>
  <artifactId>selenide-video-recorder</artifactId>
  <version>LAST_VERSION_OF_SELENIDE</version>
</dependency>
```  

**Gradle**
```groovy
implementation("com.codeborne:selenide-video-recorder:LAST_VERSION_OF_SELENIDE")
```

### JUnit 5

Annotate test class with `@RecordVideoJunit`. Videos will be recorded automatically.

To skip recording for a specific test, annotate this test with `@DisableVideoRecording`.

Example of test is [here](../video-recorder-junit/src/test/java/integration/video_recorder/junit5/VideoRecorderScreenShotJunitTests.java)


### TestNG

Add `@Listeners(BrowserRecorderListener.class)` to a test class. Videos will be recorded automatically.

To skip video recording for a specific test, annotate this test with `@DisableVideoRecording`.

Example of test is [here](../video-recorder-testng/src/test/java/integration/video_recorder/testng/VideoRecorderScreenShotTestNgTests.java)

### Other testing frameworks

Selenide has built-in support for two testing frameworks: JUnit5 and TestNG.

If you use some other framework, you can run video recorder directly like this (pseudocode):

```java
videoRecorder = new VideoRecorderScreenShot(
  webdriver().object(),
  RecorderFileUtils.generateVideoFileName(<testClassName>, <testName>));
executor = new ScheduledThreadPoolExecutor(1);
executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
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
After recording, you should stop recorder:

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
