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

1. Annotate test class with `@Video`. Videos will be recorded automatically.
To skip recording for a specific test, annotate this test with `@NoVideo`.

2. Alternatively, you can enable JUnit extension `@ExtendWith(VideoRecorderExtension.class)`.

Example of test is [here](../video-recorder-junit/src/test/java/integration/videorecorder/junit5/VideoRecorderJunitTest.java)

P.S. If needed, you can get reference to the recorded video file:

```java
Optional<Path> videoFile = VideoRecorderExtension.getRecordedVideo();
```


### TestNG

Add `@Listeners(VideoRecorderListener.class)` to a test class. Videos will be recorded automatically.

To skip video recording for a specific test, annotate this test with `@NoVideo`.

Example of test is [here](../video-recorder-testng/src/test/java/integration/videorecorder/testng/VideoRecorderTestNgTest.java)

P.S. If needed, you can get reference to the recorded video file:

```java
Optional<Path> videoFile = VideoRecorderListener.getRecordedVideo();
```

### Other testing frameworks

Selenide has built-in support for two testing frameworks: JUnit5 and TestNG.

If you use some other framework, you can run video recorder directly like this:

```java
VideoRecorder videoRecorder = new VideoRecorder(
  RecorderFileUtils.generateVideoFileName(<testClassName>, <testName>));
videoRecorder.start();
```

in the first line the parameter for `VideoRecorder` constructor should pass `Webdriver` instance.
`<testClassName>` and `<testName>` can be `null`. These parameters are needed to point recorder to the video file.

| testsClassName | testName   | result file name location                                                                     |
|----------------|------------|-----------------------------------------------------------------------------------------------|
| null or ""     | null or "" | `Configuration.reportsFolder + "/video/" + <randomFileName>.webm`                             |
| not empty      | null or "" | `Configuration.reportsFolder + "/video" + "/testClassName/" + <randomFileName>.webm`          |
| null or ""     | not empty  | `Configuration.reportsFolder + "/video" + "/testName/" + <randomFileName>.webm`               |
| not empty      | not empty  | `Configuration.reportsFolder + "/video" + "/testClassName/testName/" + <randomFileName>.webm` |

The last line schedules task every second.
After recording, you should stop recorder:

```java
videoRecorder.stopRecording();
executor.shutdown();
```
