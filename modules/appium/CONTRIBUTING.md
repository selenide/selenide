Welcome to `selenide-appium` internals!

Any contributions to `selenide-appium` are appreciated.  

## Branches
- The latest state is always in `main` branch
- Every bugfix/feature is developed in a separate branches created from `main` branch
- Once bugfix/feature is accepted, someone of maintainers merges it to `main` branch
- When we make a release, we make it from `main` branches

## How to send a pull request?
- Create a github account
- Fork `selenide` repo  
  - go to https://github.com/selenide/selenide
  - click "Fork" link in the right upper corner
- Check out your `selenide` copy:  
  - `git clone https://github.com/username/selenide`
- Create a new branch from `main` branch
  - `git checkout -b remove-duplicate-screenshots-in-allure`   (choose any reasonable branch name)
- Make your changes
- Run tests (see below)
- Commit, say a little prayer and push
  - it's recommended to start commit message with issue id, like `#ID remove duplicate screenshots in Allure`
  - also read a [commit message tutorial](https://chris.beams.io/posts/git-commit/)
  - prefer small independent commits to one big commit with many changes
- go to http://github.com/username/selenide/
  - you will see a automatic suggestion to create a pull request
  - accept it, fill all the required fields.
  - wait. Don't worry, we review pull requests often. We will contact you.  


## How to build selenide-appium locally?

```
git clone https://github.com/selenide/selenide.git
cd selenide
./gradlew    (or `gradlew.bat` on Windows)
```

### How to run tests?
Selenide-appium project has three types of tests: unit tests, Android tests and iOS tests.
* Running unit-tests is simple.
* Running iOS tests is only possible on macOS (with Appium + Xcode/Simulator installed).
* Running Android tests is possible on any OS, but you need to create an Android Emulator device.

#### 1. Run the emulator:

To create a new emulator:
> open Android Studio -> "Android Virtual Device Manager"

NB! Use Pixel_3_XL device profile to match your local environment and our CI setup.
For more details please read [official docs](https://developer.android.com/studio/run/managing-avds).
#### 2. Run appium server:
```
> appium --base-path /wd/hub --relaxed-security
```

#### 3. And finally, run the test:
```
> ./gradlew test
> ./gradlew android
> ./gradlew ios
```

## Welcome to the club!
Feel free to fork, clone, build, run tests and contribute pull requests for Selenide!
