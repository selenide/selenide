# selenide-appium
Selenide adaptor for Appium framework.

NB! This is only needed if you want to use PageFactory (annotations like `@AndroidFindBy`).

If you don't need `@FooFindBy` annotations, consider using a [more simple approach](https://github.com/selenide-examples/selenide-appium).
It just uses usual Selenide `$` calls to find elements in mobile app. 

### How to run the example

* (optionally) Run device manager and created "default" device:
  > sdkmanager "system-images;android-25;google_apis;x86"
  > avdmanager create avd -n default --abi google_apis/x86 -k "system-images;android-25;google_apis;x86" -p ~/.android/avd/default

* Run the emulator:
  > emulator -avd default

* Run appium server:
   > appium

* And finally, run the test:
   > ./gradlew test
