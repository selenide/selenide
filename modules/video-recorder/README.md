# Tests video recorder

## Precaution
> This feature is for debug needs. So use it wisely.

Video recorder allows you to create a video of a browser window while your test is running. 
It can be useful to debug flaky tests. 

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

1. Add JUnit extension `@ExtendWith(VideoRecorderExtension.class)`.
2. Annotate with `@Video` to those test methods you need to investigate.

Example of test is [here](../video-recorder-junit/src/test/java/integration/videorecorder/junit5/VideoRecorderJunit1Test.java)

P.S. If needed, you can get reference to the recorded video file.
This method should be called in the same as the test itself: 

```java
Optional<Path> videoFile = VideoRecorderExtension.getRecordedVideo();
```


### TestNG

1. Add `@Listeners(VideoRecorderListener.class)` to a test class.
2. Annotate with `@Video` to those test methods you need to investigate.

Example of test is [here](../video-recorder-testng/src/test/java/integration/videorecorder/testng/VideoRecorderTestNg1Test.java)

P.S. If needed, you can get reference to the recorded video file.  
This method should be called in the same as the test itself:

```java
Optional<Path> videoFile = VideoRecorderListener.getRecordedVideo();
```


### Result

If such annotated test fails, you will see video URL in the error message:
```java
Element should have text "Oreshnik"
Actual value: text="Kokoshnik"

Screenshot: file:/.../build/reports/tests/1732761758509.0.png
Page source: file:/.../build/reports/tests/1732761758509.0.html
Video: file:/.../build/reports/tests/1732761754743.0.mp4
```

Also, video file will be printed to console output in a specific format:
```java
[[ATTACHMENT|/projects/magura/build/reports/tests/1746735811373.0.mp4]]
```

Some CI tools (at least GitLab and Jenkins) understand this format and automatically save these files in build results.


### Configuration

The abovementioned behaviour and some parameters can be configured in file `selenide.properties`. 
Just put it to classpath (meaning, create file `src/test/resources/selenide.properties`).

You can find all parameters here:
[VideoConfiguration](https://github.com/selenide/selenide/blob/main/modules/video-recorder/src/main/java/org/selenide/videorecorder/core/VideoConfiguration.java).

### Record video for all tests
If you don't want to annotate every single test with `@Video`, it's possible to enable video recording
globally for all tests by adding this line to `selenide.properties`:
```properties
selenide.video.mode=ALL
```

To skip video for some specific test, annotate this test with `@NoVideo`.


### Other testing frameworks

Selenide has built-in support for two testing frameworks: JUnit5 and TestNG.

If you use some other framework, you can run video recorder directly like this:

```java
// BEFORE the test:
VideoRecorder videoRecorder = new VideoRecorder();
videoRecorder.start();

// test itself
... 

// AFTER the test
videoRecorder.finish();
```

## How it works

Video recorder starts at the beginning of your test, 
and constantly takes screenshots of a current browser window (using a standard WebDriver API).

When the test finishes, the recorder puts all these screenshots together into a video file using [FFMPeg](https://ffmpeg.org/).

The `selenide-video-recorder` module requires installed FFMPeg on system.

Feel free to share your [experience](https://github.com/selenide/selenide/discussions)
and [suggestions](https://github.com/selenide/selenide/issues/new)!

And good luck with [FLAKY TESTS](https://www.youtube.com/watch?v=18J2_4a4Cl4&ab_channel=Jfokus)!
