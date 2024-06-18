# Video Recorder for TestNG
> [!IMPORTANT]
> This feature is for debug needs. So use it wisely

## How to use
If you want to record TestNG tests in test class you should add dependency to your project

**Maven**
```xml
<dependency>
  <groupId>com.codeborne</groupId>
  <artifactId>video-recorder-testng</artifactId>
  <version>LAST_VERSION_OF_SELENIDE</version>
</dependency>
```  

**Gradle**
```groovy
implementation("com.codeborne:video-recorder-testng:LAST_VERSION_OF_SELENIDE")
```

Add `@Listeners(BrowserRecorderListener.class)`. Recording will be done automatically. Videos will be saved in the folders structure
as described [here](../video-recorder-core/README.md).

If you want to skip recording of proper test you should mark this test with `@DisableVideoRecording` annotation.

Example of test is [here](src/test/java/integration/VideoRecorderScreenShotTestNgTests.java)
