# selenide-appium
Selenide adaptor for Appium framework

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
