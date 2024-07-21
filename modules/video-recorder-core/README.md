# Tests video recorder

### Concept
Now the last versions of Selenium have [BiDi functionality](https://www.selenium.dev/documentation/webdriver/bidi/w3c/).
This allows us to use unified API for at lest Chrome and Firefox.  

Usually to take screenshot we use the next code:
```java
Webdriver driver = new ChromeDriver();
...
File screenshot =  ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
```
So, in general, we should take screenshot once a second and save it 24 times to output video file.  
But we will decrease the speed of tests running because all these actions should be done at the same Thread as test.
But it would be better to make this in parallel, no matter if tests use some waiters or sleeps.  
The solution is to connect to already opened browser by debug port or use WebSocket connection.  
Selenium can do such connections using:
* CDP DevTools
* BiDi  

CDP can be used with Chromium-based browsers only. So this approach is not universal.  
We decided to use BiDi functionality.  

## Init BiDi.  
When you start your browser you should add some ~~magic~~ browser options to activate BiDi.    
For example:
```java
FirefoxOptions firefoxOptions = new FirefoxOptions();
firefoxOptions.setCapability("webSocketUrl", true);
Webdriver driver = new FirefoxDriver(firefoxOptions);
```
These couple lines of code allows you to use BiDi. In our case it allows to use [`BrowsingContext`](https://www.selenium.dev/documentation/webdriver/bidi/w3c/browsing_context/).  

## VideoRecorderScreenShot class
[`VideoRecorderScreenShot`](src/main/java/com/selenide/videorecorder/VideoRecorderScreenShotTests.java) is used to record video. It extends from TimerTask class to run in parallel thread.  

To learn how it works see [this tests](src/test/java/integration/VideoRecorderScreenShotTests.java)  

To run recorder directly you should have 2 variables of VideoRecorderScreenShot and ScheduledThreadPoolExecutor accordingly. One is for creating task for recording another is a runner for this task.  
Next you should do this:  
```java
videoRecorder = new VideoRecorderScreenShot(webdriver().object(),
      testInfo.getTestClass().get().getSimpleName(),
      testInfo.getTestMethod().get().getName());
    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
```
in the first line the parameter for `VideoRecorderScreenShot` constructor should pass `Webdriver` instance. The last line schedules task every second.
According to Playwright documentation to stop recording you should close `BrowserContext`. In our case you should stop recorder
```java
    videoRecorder.stopRecording();
    videoRecorder.cancel();
    executor.shutdown();
```
After that you can get reference to already recorded file.
```java
videoRecorder.getVideoFile();
```
For now the default folder is `build/records`.
Constructor for `VideoRecorderScreenShot` takes 3 parameters: 
* `Webdriver` instance
* Tests class name. Might be `null` or `""`
* Test name. Might be `null` or `""`

In general if all constructor's parameters are set as non-empty string then video file will be saved to `build/records/<tests class name>/<test name>/<timestamp in miliseconds>.webm`  
If leave those parameters empty then video file will appear in `build/records` folder.  
If any of those parameters are not empty then file will appear in `build/record/<one of non empty parameters>` folder.  



